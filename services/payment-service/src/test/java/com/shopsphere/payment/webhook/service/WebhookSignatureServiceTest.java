
package com.shopsphere.payment.webhook.service;

import com.shopsphere.payment.webhook.exception.InvalidWebhookSignatureException;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class WebhookSignatureServiceTest {

    private static final String SECRET =
            "local-test-secret-use-a-long-random-value";

    private final WebhookSignatureService signatureService =
            new WebhookSignatureService(SECRET);

    @Test
    void verifyShouldAcceptCorrectSignature() throws Exception {
        String body = "{\"eventId\":\"evt-001\",\"status\":\"SUCCESS\"}";

        assertDoesNotThrow(
                () -> signatureService.verify(body, sign(body))
        );
    }

    @Test
    void verifyShouldRejectTamperedBody() throws Exception {
        String originalBody = "{\"eventId\":\"evt-001\"}";
        String changedBody = "{\"eventId\":\"evt-002\"}";

        String signature = sign(originalBody);

        assertThrows(
                InvalidWebhookSignatureException.class,
                () -> signatureService.verify(changedBody, signature)
        );
    }

    @Test
    void verifyShouldRejectMissingSignature() {
        assertThrows(
                InvalidWebhookSignatureException.class,
                () -> signatureService.verify("{}", null)
        );
    }

    private String sign(String body) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        ));

        return HexFormat.of().formatHex(
                mac.doFinal(body.getBytes(StandardCharsets.UTF_8))
        );
    }
}
