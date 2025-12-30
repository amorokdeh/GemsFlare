package com.gemsflare.gemsflare.invoice.service;

import com.gemsflare.gemsflare.invoice.jpa.InvoiceCounterEntity;
import com.gemsflare.gemsflare.invoice.jpa.InvoiceEntity;
import com.gemsflare.gemsflare.invoice.model.InvoiceDTO;
import com.gemsflare.gemsflare.invoice.repository.InvoiceCounterRepository;
import com.gemsflare.gemsflare.invoice.repository.InvoiceRepository;
import com.gemsflare.gemsflare.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private InvoiceCounterRepository invoiceCounterRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoicePdfService invoicePdfService;

    public ResponseEntity<?> addInvoice(HttpServletRequest request, InvoiceDTO invoiceDTO) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: No token provided");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Invalid token");
        }

        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setNumber(generateInvoiceNumber());
        invoice.setIssuedate(LocalDate.now());
        invoice.setOrderdate(LocalDate.now());
        invoice.setOrdernumber(invoiceDTO.getOrdernumber());
        invoice.setBilladdress(invoiceDTO.getBilladdress());
        invoice.setShippingaddress(invoiceDTO.getShippingaddress());
        invoice.setItems(invoiceDTO.getItems());
        invoice.setTotalamount(invoiceDTO.getTotalamount());
        invoice.setTotalamountwithouttax(invoiceDTO.getTotalamountwithouttax());
        invoice.setTax(invoiceDTO.getTax());
        invoice.setPayment(invoiceDTO.getPayment());

        invoiceRepository.save(invoice);

        return ResponseEntity.ok(invoice);
    }

    public ResponseEntity<?> getInvoiceByNumber(String invoiceNumber) {
        Optional<InvoiceEntity> invoiceOpt = invoiceRepository.findByNumber(invoiceNumber);
        if (invoiceOpt.isPresent()) {
            return ResponseEntity.ok(invoiceOpt.get());
        } else {
            return ResponseEntity.status(404).body("Invoice not found with number: " + invoiceNumber);
        }
    }

    public ResponseEntity<?> getInvoiceByOrderNumber(String orderNumber) {
        Optional<InvoiceEntity> invoiceOpt = invoiceRepository.findByOrdernumber(orderNumber);
        if (invoiceOpt.isPresent()) {
            return ResponseEntity.ok(invoiceOpt.get());
        } else {
            return ResponseEntity.status(404).body("Invoice not found with order number: " + orderNumber);
        }
    }

    public ResponseEntity<?> getInvoicePdfByNumber(String number) {
        try {
            Optional<InvoiceEntity> invoiceOpt = invoiceRepository.findByNumber(number);
            if (invoiceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Invoice not found with number: " + number);
            }

            InvoiceEntity invoiceEntity = invoiceOpt.get();
            InvoiceDTO invoiceDTO = invoiceEntityToDTO(invoiceEntity);

            byte[] pdfBytes = invoicePdfService.generatePdf(invoiceDTO);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getInvoicePdfByOrderNumber(String orderNumber) {
        try {
            Optional<InvoiceEntity> invoiceOpt = invoiceRepository.findByOrdernumber(orderNumber);
            if (invoiceOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Invoice not found with order number: " + orderNumber);
            }

            InvoiceEntity invoiceEntity = invoiceOpt.get();
            InvoiceDTO invoiceDTO = invoiceEntityToDTO(invoiceEntity);

            byte[] pdfBytes = invoicePdfService.generatePdf(invoiceDTO);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

    public String generateInvoiceNumber() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();

        InvoiceCounterEntity counterRecord = null;
        Optional<InvoiceCounterEntity> counterRecordOpt = invoiceCounterRepository.findByDate(currentDate);
        if (counterRecordOpt.isPresent()) {
            counterRecord = counterRecordOpt.get();
        }

        if (counterRecord == null) {
            counterRecord = new InvoiceCounterEntity();
            counterRecord.setDate(currentDate);
            counterRecord.setCounter(1);
            invoiceCounterRepository.save(counterRecord);
        } else {
            counterRecord.setCounter(counterRecord.getCounter() + 1);
            invoiceCounterRepository.save(counterRecord);
        }

        String formattedCounter = String.format("%06d", counterRecord.getCounter());

        return String.format("GEMSFLARE-%d-%02d-%02d-%s", year, month, day, formattedCounter);
    }

    private InvoiceDTO invoiceEntityToDTO(InvoiceEntity invoiceEntity) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoiceEntity.getId());
        dto.setNumber(invoiceEntity.getNumber());
        dto.setIssuedate(invoiceEntity.getIssuedate());
        dto.setOrderdate(invoiceEntity.getOrderdate());
        dto.setOrdernumber(invoiceEntity.getOrdernumber());
        dto.setBilladdress(invoiceEntity.getBilladdress());
        dto.setShippingaddress(invoiceEntity.getShippingaddress());
        dto.setItems(invoiceEntity.getItems());
        dto.setTotalamount(invoiceEntity.getTotalamount());
        dto.setTotalamountwithouttax(invoiceEntity.getTotalamountwithouttax());
        dto.setTax(invoiceEntity.getTax());
        dto.setPayment(invoiceEntity.getPayment());
        return dto;
    }


}
