package com.notifications.domain;

import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;

import java.util.Objects;

public record PushNotification(
        NotificationId id,
        Recipient recipient,
        MessageBody body,
        String title
) implements Notification {

    public PushNotification {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(body, "body must not be null");
        Objects.requireNonNull(title, "title must not be null");
        if (title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }
}
