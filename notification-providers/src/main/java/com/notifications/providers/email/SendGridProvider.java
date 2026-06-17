package com.notifications.providers.email;

import com.notifications.contract.NotificationProvider;
import com.notifications.domain.EmailNotification;
import com.notifications.domain.Notification;
import com.notifications.model.ApiCredential;
import com.notifications.model.NotificationChannel;
import com.notifications.result.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SendGridProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(SendGridProvider.class);

    private final ApiCredential credential;
    private final String fromAddress;

    public SendGridProvider(ApiCredential credential, String fromAddress) {
        this.credential = Objects.requireNonNull(credential, "credential must not be null");
        this.fromAddress = Objects.requireNonNull(fromAddress, "fromAddress must not be null");
        if (fromAddress.isBlank()) {
            throw new IllegalArgumentException("fromAddress must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public String providerName() {
        return "sendgrid";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof EmailNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        EmailNotification email = (EmailNotification) notification;
        String messageId = "sg-" + UUID.randomUUID();

        log.info(
                "SendGrid simulated send | from={} to={} subject={} credential={}",
                fromAddress,
                email.recipient().value(),
                email.subject(),
                credential
        );

        return NotificationResult.success(
                email.id(),
                NotificationChannel.EMAIL,
                providerName(),
                messageId,
                Map.of(
                        "from", fromAddress,
                        "providerResponse", "202 Accepted"
                )
        );
    }
}
