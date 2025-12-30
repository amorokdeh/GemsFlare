package com.gemsflare.gemsflare.payment.controller;

import com.gemsflare.gemsflare.payment.service.PayPalPayService;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@Tag(name = "PayPal Payment")
public class PayPalController {

    @Autowired
    private PayPalPayService payPalPayService;

    @Operation(summary = "Create PayPal Order")
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam String checkoutNumber) {
        try {
            Order order = payPalPayService.createOrder(checkoutNumber);

            String approvalUrl = order.links().stream()
                    .filter(link -> link.rel().equalsIgnoreCase("approve"))
                    .map(LinkDescription::href)
                    .findFirst()
                    .orElse("No approval URL found");

            return ResponseEntity.ok(Map.of(
                    "orderID", order.id(),
                    "status", order.status(),
                    "approvalUrl", approvalUrl
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error creating order: " + e.getMessage());
        }
    }

    @Operation(summary = "Capture PayPal Order")
    @PostMapping("/capture-order")
    public ResponseEntity<?> captureOrder(HttpServletRequest request,
                                          @RequestParam String orderId,
                                          @RequestParam String checkoutNumber) {
        try {
            Order order = payPalPayService.captureOrder(request, orderId, checkoutNumber);
            return ResponseEntity.ok(Map.of(
                    "status", order.status(),
                    "orderId", order.id(),
                    "payer", order.payer()
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error capturing order: " + e.getMessage());
        }
    }
}