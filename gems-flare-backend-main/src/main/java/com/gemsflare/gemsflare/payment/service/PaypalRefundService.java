package com.gemsflare.gemsflare.payment.service;

import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PaypalRefundService {

    private static final String CLIENT_ID = "ATYS8MMPhsOwP9lnzkZZP6ZMnLc2hJivJyOTcwCDYttwkeZilxl5GsMd6wjln1S-J2AE0tQQdX00aQk2";
    private static final String CLIENT_SECRET = "EPYP0PEn7PyzXsIW1opgMi1_369lZhQ2q5s4kg8De5ZaPOZOGgLMTo_bmABqoiHIbqUEToFgURq_kas2";
    private static final Boolean IS_SANDBOX = false;

    private final PayPalHttpClient client;

    public PaypalRefundService() {
        PayPalEnvironment environment = IS_SANDBOX
                ? new PayPalEnvironment.Sandbox(CLIENT_ID, CLIENT_SECRET)
                : new PayPalEnvironment.Live(CLIENT_ID, CLIENT_SECRET);

        this.client = new PayPalHttpClient(environment);
    }

    public Refund refundOrder(String captureId, String amount) throws IOException {
        RefundRequest refundRequest = new RefundRequest()
                .amount(new Money()
                        .currencyCode("EUR")
                        .value(amount));

        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.requestBody(refundRequest);

        int maxRetries = 3;
        int retryDelay = 1000;
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                HttpResponse<Refund> response = client.execute(request);
                return response.result();
            } catch (IOException e) {
                lastException = e;
                System.err.println("Attempt " + attempt + " to refund PayPal capture failed: " + e.getMessage());
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
