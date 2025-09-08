package com.example.oil_mart.controller;

import com.example.oil_mart.service.serviceImplementation.ReportService;
import com.example.oil_mart.service.serviceImplementation.SalesOrderReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private SalesOrderReport salesOrderReport;

    // Original endpoint (unchanged for backward compatibility)
    @GetMapping("/sales-profit")
    public Map<String, String> getSalesProfitReport() throws Exception {
        String base64Pdf = salesOrderReport.generateSalesProfitReportBase64();
        return Map.of("pdfBase64", base64Pdf);
    }

    // New endpoint for sales order report with date range
    @GetMapping("/sales-orders")
    public Map<String, String> getSalesOrderReport(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) throws Exception {

        String base64Pdf = salesOrderReport.generateSalesOrderReportBase64(startDate, endDate);
        return Map.of("pdfBase64", base64Pdf);
    }

    // Alternative POST endpoint for more complex parameters
    @PostMapping("/sales-orders")
    public Map<String, String> getSalesOrderReportPost(@RequestBody Map<String, String> dateRange) throws Exception {
        String startDate = dateRange.get("startDate");
        String endDate = dateRange.get("endDate");

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both startDate and endDate are required");
        }

        String base64Pdf = salesOrderReport.generateSalesOrderReportBase64(startDate, endDate);
        return Map.of("pdfBase64", base64Pdf);
    }
}