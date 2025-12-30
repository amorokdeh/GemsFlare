package com.gemsflare.gemsflare.payment.service;

import com.gemsflare.gemsflare.checkout.model.CheckoutDTO;
import com.gemsflare.gemsflare.checkout.service.CheckoutService;
import com.gemsflare.gemsflare.item.model.ItemDTO;
import com.gemsflare.gemsflare.item.service.ItemService;
import com.gemsflare.gemsflare.order.service.OrderService;
import com.paypal.orders.*;
import com.paypal.http.HttpResponse;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PayPalPayService {

    @Autowired
    private ItemService itemService;
    @Autowired
    private CheckoutService checkoutService;
    @Autowired
    private OrderService orderService;

    private static final String CLIENT_ID = "ATYS8MMPhsOwP9lnzkZZP6ZMnLc2hJivJyOTcwCDYttwkeZilxl5GsMd6wjln1S-J2AE0tQQdX00aQk2";
    private static final String CLIENT_SECRET = "EPYP0PEn7PyzXsIW1opgMi1_369lZhQ2q5s4kg8De5ZaPOZOGgLMTo_bmABqoiHIbqUEToFgURq_kas2";
    private static final Boolean IS_SANDBOX = false;

    private final PayPalHttpClient client;

    public PayPalPayService() {
        PayPalEnvironment environment = IS_SANDBOX
                ? new PayPalEnvironment.Sandbox(CLIENT_ID, CLIENT_SECRET)
                : new PayPalEnvironment.Live(CLIENT_ID, CLIENT_SECRET);

        this.client = new PayPalHttpClient(environment);
    }

    public Order createOrder(String checkoutNumber) throws IOException {
        CheckoutDTO checkoutDTO = checkoutService.getCheckoutDTOByNumber(checkoutNumber);
        for (ItemDTO item : checkoutDTO.getItems()) {
            ItemDTO storeItem = itemService.getItemByNumber(item.getNumber());

            if (storeItem == null) {
                throw new IllegalArgumentException("Item not found: " + item.getNumber());
            }

            if (storeItem.getAmount() < item.getAmount()) {
                throw new IllegalArgumentException("Insufficient stock for item: " + item.getName());
            }
        }

        String value = checkoutDTO.getSum().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        ApplicationContext context = new ApplicationContext()
                .brandName("Gemsflare")
                .landingPage("BILLING")
                .cancelUrl("https://gemsflare.com/cancel")
                .returnUrl("https://gemsflare.com/return")
                .userAction("PAY_NOW");

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("EUR")
                .value(value);

        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest().amountWithBreakdown(amount);

        orderRequest.applicationContext(context);
        orderRequest.purchaseUnits(List.of(purchaseUnit));

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);

        int maxRetries = 3;
        int retryDelay = 1000;
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<Order> response = client.execute(request);
                return response.result();
            } catch (IOException e) {
                lastException = e;
                System.err.println("Attempt " + attempt + " to create PayPal order failed: " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        throw lastException;
    }

    public Order captureOrder(HttpServletRequest request, String orderId, String checkoutNumber) throws IOException {
        OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(orderId);
        captureRequest.requestBody(new OrderRequest());

        int maxRetries = 3;
        int retryDelay = 1000;
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<Order> response = client.execute(captureRequest);
                Order capturedOrder = response.result();

                if (orderService.getOrderDTOByTransaction(orderId) == null) {

                    if (checkoutNumber == null) {
                        throw new IllegalStateException("Checkout number not found in captured PayPal order.");
                    }

                    String captureId = capturedOrder.purchaseUnits()
                            .get(0)
                            .payments()
                            .captures()
                            .get(0)
                            .id();

                    CheckoutDTO checkoutDTO = checkoutService.getCheckoutDTOByNumber(checkoutNumber);

                    orderService.addOrder(request, checkoutDTO, captureId);
                }

                return capturedOrder;

            } catch (IOException e) {
                lastException = e;
                System.err.println("Attempt " + attempt + " to capture PayPal order failed: " + e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ignored) {}
                }
            }
        }

        throw lastException;
    }

}