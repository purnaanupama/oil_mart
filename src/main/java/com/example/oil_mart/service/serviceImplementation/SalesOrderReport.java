package com.example.oil_mart.service.serviceImplementation;

import com.example.oil_mart.model.SalesProfit;
import com.example.oil_mart.model.Sales_Order;
import com.example.oil_mart.model.Sales_Order_Item;
import com.example.oil_mart.repository.SalesOrderItemRepository;
import com.example.oil_mart.repository.SalesOrderRepository;
import com.example.oil_mart.repository.SalesProfitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    private ReportService reportService;

    public static class OrderData {
        private Sales_Order order;
        private List<Sales_Order_Item> items;
        private SalesProfit profit;
        private String orderDate;

        // Constructors
        public OrderData() {}

        public OrderData(Sales_Order order, List<Sales_Order_Item> items, SalesProfit profit, String orderDate) {
            this.order = order;
            this.items = items;
            this.profit = profit;
            this.orderDate = orderDate;
        }

        // Getters and Setters
        public Sales_Order getOrder() { return order; }
        public void setOrder(Sales_Order order) { this.order = order; }

        public List<Sales_Order_Item> getItems() { return items; }
        public void setItems(List<Sales_Order_Item> items) { this.items = items; }

        public SalesProfit getProfit() { return profit; }
        public void setProfit(SalesProfit profit) { this.profit = profit; }

        public String getOrderDate() { return orderDate; }
        public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    }

    public String generateSalesOrderReportBase64(String startDate, String endDate) throws Exception {
        // Parse input dates
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date start = inputFormat.parse(startDate);
        Date end = inputFormat.parse(endDate);

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

        for (Sales_Order order : orders) {
            List<Sales_Order_Item> items = salesOrderItemRepository.findAllBySalesOrder(order);
            SalesProfit profit = salesProfitRepository.findBySalesOrderNo(order.getSalesOrderNo()).orElse(null);

            String orderDate = extractDateFromISO(order.getCreatedAt());

            ordersData.add(new OrderData(order, items, profit, orderDate));

            totalSalesAmount += order.getTotalAmount();
            if (profit != null) {
                totalProfitAmount += profit.getTotalProfitAmount();
            }
        }

        // Prepare template data
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        Map<String, Object> dataModel = Map.of(
                "ordersData", ordersData,
                "totalSalesAmount", totalSalesAmount,
                "totalProfitAmount", totalProfitAmount,
                "totalOrders", orders.size(),
                "generatedDate", now.format(dateFormatter),
                "generatedTime", now.format(timeFormatter),
                "startDate", startDate,
                "endDate", endDate
        );

        byte[] pdfBytes = reportService.generatePdf("sales-profit-report", dataModel);
        return Base64.getEncoder().encodeToString(pdfBytes);
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
                // Skip orders with invalid date format
                continue;
            }
        }

        // Sort by creation date
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
            // Return original string if parsing fails
            return isoDateTime.substring(0, Math.min(10, isoDateTime.length()));
        }
    }

    // Keep the original method for backward compatibility
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