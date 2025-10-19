package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.model.ExpensesLog;
import com.example.oil_mart.model.SalesProfit;
import com.example.oil_mart.model.Sales_Order;
import com.example.oil_mart.model.Sales_Order_Item;
import com.example.oil_mart.repository.ExpensesLogRepository;
import com.example.oil_mart.repository.SalesOrderItemRepository;
import com.example.oil_mart.repository.SalesOrderRepository;
import com.example.oil_mart.repository.SalesProfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesOrderReport {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private SalesOrderItemRepository salesOrderItemRepository;

    @Autowired
    private SalesProfitRepository salesProfitRepository;

    @Autowired
    private ExpensesLogRepository expensesLogRepository;

    @Autowired
    private ReportService reportService;

    public static class OrderData {
        private Sales_Order order;
        private List<Sales_Order_Item> items;
        private SalesProfit profit;
        private String orderDate;

        public OrderData() {}

        public OrderData(Sales_Order order, List<Sales_Order_Item> items, SalesProfit profit, String orderDate) {
            this.order = order;
            this.items = items;
            this.profit = profit;
            this.orderDate = orderDate;
        }

        public Sales_Order getOrder() { return order; }
        public void setOrder(Sales_Order order) { this.order = order; }

        public List<Sales_Order_Item> getItems() { return items; }
        public void setItems(List<Sales_Order_Item> items) { this.items = items; }

        public SalesProfit getProfit() { return profit; }
        public void setProfit(SalesProfit profit) { this.profit = profit; }

        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    }

    public static class ExpenseSummary {
        private String expenseType;
        private BigDecimal totalAmount;
        private int occurrences;

        public ExpenseSummary(String expenseType, BigDecimal totalAmount, int occurrences) {
            this.expenseType = expenseType;
            this.totalAmount = totalAmount;
            this.occurrences = occurrences;
        }

        public String getExpenseType() { return expenseType; }
        public void setExpenseType(String expenseType) { this.expenseType = expenseType; }

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

        public int getOccurrences() { return occurrences; }
        public void setOccurrences(int occurrences) { this.occurrences = occurrences; }
    }

    public String generateSalesOrderReportBase64(String startDate, String endDate, Double legacyExpenses) throws Exception {
        // Parse input dates
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = inputFormat.parse(startDate);
        Date end = inputFormat.parse(endDate);

        // Convert to LocalDate for expense filtering
        LocalDate startLocalDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Adjust end date to end of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        end = cal.getTime();

        // Get all orders within date range
        List<Sales_Order> orders = getAllOrdersInDateRange(start, end);

        if (orders.isEmpty()) {
            throw new RuntimeException("No sales orders found for the given date range!");
        }

        // Prepare data for each order
        List<OrderData> ordersData = new ArrayList<>();
        double totalSalesAmount = 0.0;
        double totalProfitAmount = 0.0;
        double pendingCreditAmount = 0.0;

        for (Sales_Order order : orders) {
            List<Sales_Order_Item> items = salesOrderItemRepository.findAllBySalesOrder(order);
            SalesProfit profit = salesProfitRepository.findBySalesOrderNo(order.getSalesOrderNo()).orElse(null);

            String orderDate = extractDateFromISO(order.getCreatedAt());
            if(Boolean.FALSE.equals(order.getStatus())){
                pendingCreditAmount += order.getTotalAmount();
            }
            ordersData.add(new OrderData(order, items, profit, orderDate));

            totalSalesAmount += order.getTotalAmount();
            if (profit != null && !Boolean.FALSE.equals(order.getStatus())) {
                totalProfitAmount += profit.getTotalProfitAmount();
            }
        }

        // Get expenses within the date range
        List<ExpensesLog> expensesInRange = getExpensesInDateRange(startLocalDate, endLocalDate);

        // Group expenses by type and calculate totals
        Map<String, ExpenseSummary> expensesByType = expensesInRange.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getExpenses().getExpenseName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                logs -> new ExpenseSummary(
                                        logs.get(0).getExpenses().getExpenseName(),
                                        logs.stream()
                                                .map(ExpensesLog::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                                        logs.size()
                                )
                        )
                ));

        // Calculate total expenses from ExpensesLog
        double totalExpensesFromLog = expensesInRange.stream()
                .map(ExpensesLog::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        // Add legacy expenses if provided (for backward compatibility)
        double totalExpenses = totalExpensesFromLog + (legacyExpenses != null ? legacyExpenses : 0.0);

        // Calculate profit before and after expenses
        double profitBeforeExpenses = totalProfitAmount;
        double profitAfterExpenses = totalProfitAmount - totalExpenses;

        // Prepare template data
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Convert expense summaries to a list for template
        List<ExpenseSummary> expenseSummaryList = new ArrayList<>(expensesByType.values());
        expenseSummaryList.sort(Comparator.comparing(ExpenseSummary::getExpenseType));

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("ordersData", ordersData);
        dataModel.put("totalSalesAmount", totalSalesAmount);
        dataModel.put("profitBeforeExpenses", profitBeforeExpenses);
        dataModel.put("profitAfterExpenses", profitAfterExpenses);
        dataModel.put("totalPendingCredit", pendingCreditAmount);
        dataModel.put("totalExpenses", totalExpenses);
        dataModel.put("expenseSummaryList", expenseSummaryList);
        dataModel.put("totalOrders", orders.size());
        dataModel.put("generatedDate", now.format(dateFormatter));
        dataModel.put("generatedTime", now.format(timeFormatter));
        dataModel.put("startDate", startDate);
        dataModel.put("endDate", endDate);

        byte[] pdfBytes = reportService.generatePdf("sales-profit-report", dataModel);
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    private List<ExpensesLog> getExpensesInDateRange(LocalDate startDate, LocalDate endDate) {
        List<ExpensesLog> allExpenses = expensesLogRepository.findAll();

        return allExpenses.stream()
                .filter(log -> {
                    LocalDate logDate = log.getDate();
                    return (logDate.isEqual(startDate) || logDate.isAfter(startDate)) &&
                            (logDate.isEqual(endDate) || logDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
    }

    private List<Sales_Order> getAllOrdersInDateRange(Date startDate, Date endDate) throws ParseException {
        List<Sales_Order> allOrders = salesOrderRepository.findAll();
        List<Sales_Order> filteredOrders = new ArrayList<>();

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (Sales_Order order : allOrders) {
            try {
                Date orderDate = isoFormat.parse(order.getCreatedAt());
                if ((orderDate.equals(startDate) || orderDate.after(startDate)) &&
                        (orderDate.equals(endDate) || orderDate.before(endDate))) {
                    filteredOrders.add(order);
                }
            } catch (ParseException e) {
                continue;
            }
        }

        filteredOrders.sort((o1, o2) -> {
            try {
                Date d1 = isoFormat.parse(o1.getCreatedAt());
                Date d2 = isoFormat.parse(o2.getCreatedAt());
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return 0;
            }
        });

        return filteredOrders;
    }

    private String extractDateFromISO(String isoDateTime) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(isoDateTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (ParseException e) {
            return isoDateTime.substring(0, Math.min(10, isoDateTime.length()));
        }
    }

    public String generateSalesProfitReportBase64() throws Exception {
        List<SalesProfit> profits = salesProfitRepository.findAll();

        if (profits.isEmpty()) {
            throw new RuntimeException("No sales profits found!");
        }

        SalesProfit profit = profits.get(profits.size() - 1);

        Sales_Order order = salesOrderRepository
                .findBySalesOrderNo(profit.getSalesOrderNo())
                .orElseThrow(() -> new RuntimeException("Order not found for profit!"));

        List<Sales_Order_Item> items = salesOrderItemRepository.findAllBySalesOrder(order);

        Map<String, Object> dataModel = Map.of(
                "profit", profit,
                "order", order,
                "items", items
        );

        byte[] pdfBytes = reportService.generatePdf("sales-profit-report", dataModel);

        return Base64.getEncoder().encodeToString(pdfBytes);
    }
}