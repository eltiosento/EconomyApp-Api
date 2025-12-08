package com.eltiosento.economyapp.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.eltiosento.economyapp.balance.BalanceDTO;
import com.eltiosento.economyapp.balance.BalanceService;
import com.eltiosento.economyapp.balance.CategorySummaryDTO;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class PdfService {
    @Autowired
    private SpringTemplateEngine thymeleaf;

    @Autowired
    private BalanceService balanceService;

    public byte[] generatePdf(int month, int year) {

        List<CategorySummaryDTO> categories = balanceService.getAllExpensesAndSubcategoriesMonthly(month, year);

        BalanceDTO balance = balanceService.getMonthlySummary(month, year);
        // 1. Render Thymeleaf
        Context ctx = new Context();
        ctx.setVariable("categories", categories);
        ctx.setVariable("balance", balance);
        ctx.setVariable("generationDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        ctx.setVariable("month", getMonthName(month));
        ctx.setVariable("year", year);
        String html = thymeleaf.process("report", ctx);

        // 2. Convertir a PDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            // Calcula la ruta absoluta al directorio static
            String baseUri = new ClassPathResource("static/").getURL().toExternalForm();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, baseUri);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private String getMonthName(int month) {
        if (month < 1 || month > 12) {
            return null; // Invalid month
        }
        String[] months = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        return months[month - 1];

    }
}
