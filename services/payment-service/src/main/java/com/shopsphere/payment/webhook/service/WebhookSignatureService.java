package com.shopsphere.payment.webhook.service;

import com.shopsphere.payment.webhook.exception.InvalidWebhookSignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class WebhookSignatureService {

    private final byte[] secret;

    public WebhookSignatureService(
            @Value("${payment.webhook.secret}") String secret
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment webhook secret must be configured"
            );
        }

        this.secret = secret.getBytes(StandardCharsets.UTF_8);
    }

    public void verify(String rawBody, String signatureHeader) {
        if (rawBody == null || signatureHeader == null
                || signatureHeader.isBlank()) {
            throw new InvalidWebhookSignatureException(
                    "Missing webhook body or signature"
            );
        }

        String suppliedSignature = signatureHeader.startsWith("sha256=")
                ? signatureHeader.substring("sha256=".length())
                : signatureHeader;

        final byte[] suppliedBytes;

        try {
            suppliedBytes = HexFormat.of().parseHex(suppliedSignature);
        } catch (IllegalArgumentException exception) {
            throw new InvalidWebhookSignatureException(
                    "Malformed webhook signature"
            );
        }

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));

            byte[] expectedBytes = mac.doFinal(
                    rawBody.getBytes(StandardCharsets.UTF_8)
            );

            if (!MessageDigest.isEqual(expectedBytes, suppliedBytes)) {
                throw new InvalidWebhookSignatureException(
                        "Invalid webhook signature"
                );
            }
        } catch (InvalidWebhookSignatureException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to verify webhook signature",
                    exception
            );
        }
    }
}
