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

public final class MailgunProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(MailgunProvider.class);

    private final ApiCredential credential;
    private final String domain;

    public MailgunProvider(ApiCredential credential, String domain) {
        this.credential = Objects.requireNonNull(credential, "credential must not be null");
        this.domain = Objects.requireNonNull(domain, "domain must not be null");
        if (domain.isBlank()) {
            throw new IllegalArgumentException("domain must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public String providerName() {
        return "mailgun";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof EmailNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        EmailNotification email = (EmailNotification) notification;
        String messageId = "mg-" + UUID.randomUUID();

        log.info(
                "Mailgun simulated send | domain={} to={} subject={} credential={}",
                domain,
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
                        "domain", domain,
                        "providerResponse", "Queued"
                )
        );
    }
}
