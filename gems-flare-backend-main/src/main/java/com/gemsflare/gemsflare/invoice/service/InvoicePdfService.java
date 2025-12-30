package com.gemsflare.gemsflare.invoice.service;

import com.gemsflare.gemsflare.invoice.model.InvoiceDTO;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class InvoicePdfService {

    private final TemplateEngine templateEngine;

    public InvoicePdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdf(InvoiceDTO invoiceDTO) throws Exception {
        Context context = new Context();
        context.setVariable("invoice", invoiceDTO);

        String htmlContent = templateEngine.process("invoice", context);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            renderer.createPDF(baos);
            return baos.toByteArray();
        }
    }
}