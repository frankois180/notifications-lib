package com.notifications.result;

import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record NotificationResult(
        NotificationId notificationId,
        NotificationStatus status,
        NotificationChannel channel,
        String providerName,
        Instant timestamp,
        String externalMessageId,
        Map<String, String> metadata
) {

    public NotificationResult {
        Objects.requireNonNull(notificationId, "notificationId must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(providerName, "providerName must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static NotificationResult success(
            NotificationId notificationId,
            NotificationChannel channel,
            String providerName,
            String externalMessageId,
            Map<String, String> metadata
    ) {
        return new NotificationResult(
                notificationId,
                NotificationStatus.SUCCESS,
                channel,
                providerName,
                Instant.now(),
                externalMessageId,
                metadata
        );
    }

    public boolean isSuccess() {
        return status == NotificationStatus.SUCCESS;
    }
}
