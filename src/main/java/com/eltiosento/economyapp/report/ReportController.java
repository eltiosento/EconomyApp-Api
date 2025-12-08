package com.eltiosento.economyapp.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/summary/month/{month}/year/{year}")
    public ResponseEntity<byte[]> getReport(@PathVariable @Min(value = 1) @Max(value = 12) int month,
            @PathVariable @Min(value = 2025) @Max(value = 2100) int year) {

        byte[] pdf = pdfService.generatePdf(month, year);
        String filename = String.format("informe-%02d-%d.pdf", month, year);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
