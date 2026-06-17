package com.notifications.providers.sms;

import com.notifications.contract.NotificationProvider;
import com.notifications.domain.Notification;
import com.notifications.domain.SmsNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.NotificationChannel;
import com.notifications.result.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class TwilioProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(TwilioProvider.class);

    private final ApiCredential credential;
    private final String fromNumber;

    public TwilioProvider(ApiCredential credential, String fromNumber) {
        this.credential = Objects.requireNonNull(credential, "credential must not be null");
        this.fromNumber = Objects.requireNonNull(fromNumber, "fromNumber must not be null");
        if (fromNumber.isBlank()) {
            throw new IllegalArgumentException("fromNumber must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }

    @Override
    public String providerName() {
        return "twilio";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof SmsNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        SmsNotification sms = (SmsNotification) notification;
        String sid = "SM" + UUID.randomUUID().toString().replace("-", "").substring(0, 32);

        log.info(
                "Twilio simulated send | from={} to={} credential={}",
                fromNumber,
                sms.recipient().value(),
                credential
        );

        return NotificationResult.success(
                sms.id(),
                NotificationChannel.SMS,
                providerName(),
                sid,
                Map.of(
                        "from", fromNumber,
                        "status", "queued"
                )
        );
    }
}
