package com.gemsflare.gemsflare.invoice.controller;

import com.gemsflare.gemsflare.invoice.model.InvoiceDTO;
import com.gemsflare.gemsflare.invoice.service.InvoiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/addInvoice")
    public ResponseEntity<?> addInvoice(HttpServletRequest request,
                                      @RequestBody InvoiceDTO invoiceDTO) {
        return invoiceService.addInvoice(request, invoiceDTO);
    }

    @GetMapping("/getInvoiceByNumber/{invoiceNumber}")
    public ResponseEntity<?> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return invoiceService.getInvoiceByNumber(invoiceNumber);
    }

    @GetMapping("/getInvoiceByOrderNumber/{orderNumber}")
    public ResponseEntity<?> getInvoiceByOrderNumber(@PathVariable String orderNumber) {
        return invoiceService.getInvoiceByOrderNumber(orderNumber);
    }

    @GetMapping("/getInvoicePdfByNumber/{invoiceNumber}")
    public ResponseEntity<?> getInvoicePdfByNumber(@PathVariable String invoiceNumber) {
        return invoiceService.getInvoicePdfByNumber(invoiceNumber);
    }

    @GetMapping("/getInvoicePdfByOrderNumber/{orderNumber}")
    public ResponseEntity<?> getInvoicePdfByOrderNumber(@PathVariable String orderNumber) {
        return invoiceService.getInvoicePdfByOrderNumber(orderNumber);
    }
}
