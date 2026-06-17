package com.notifications.event;

import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.result.NotificationResult;

import java.time.Instant;
import java.util.Objects;

public record NotificationEvent(
        NotificationEventType type,
        NotificationId notificationId,
        NotificationChannel channel,
        NotificationStatus status,
        Instant timestamp,
        String message,
        NotificationResult result,
        Throwable cause
) {

    public NotificationEvent {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(notificationId, "notificationId must not be null");
        Objects.requireNonNull(channel, "channel must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(message, "message must not be null");
    }

    public static NotificationEvent success(NotificationResult result) {
        return new NotificationEvent(
                NotificationEventType.SENT,
                result.notificationId(),
                result.channel(),
                NotificationStatus.SUCCESS,
                result.timestamp(),
                "Notification sent via " + result.providerName(),
                result,
                null
        );
    }

    public static NotificationEvent failure(
            NotificationId notificationId,
            NotificationChannel channel,
            String message,
            Throwable cause
    ) {
        return new NotificationEvent(
                NotificationEventType.FAILED,
                notificationId,
                channel,
                NotificationStatus.FAILED,
                Instant.now(),
                message,
                null,
                cause
        );
    }
}
