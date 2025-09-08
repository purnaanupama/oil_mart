package com.example.oil_mart.service.serviceImplementation;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Service
public class ReportService {

    private final TemplateEngine templateEngine;

    public ReportService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(String templateName, Map<String, Object> dataModel) throws Exception {
        // Render HTML from Thymeleaf template
        Context context = new Context();
        context.setVariables(dataModel);
        String htmlContent = templateEngine.process(templateName, context);

        // Convert HTML to PDF
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(os);
        builder.run();

        return os.toByteArray();
    }
}
