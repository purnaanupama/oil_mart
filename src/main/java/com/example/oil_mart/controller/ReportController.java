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
            @RequestParam(value = "expenses", required = false, defaultValue = "0") Double expenses,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) throws Exception {

        String base64Pdf = salesOrderReport.generateSalesOrderReportBase64(startDate, endDate, expenses);
        return Map.of("pdfBase64", base64Pdf);
    }

}