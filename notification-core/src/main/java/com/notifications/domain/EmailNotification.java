package com.notifications.domain;

import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;

import java.util.Objects;

public record EmailNotification(
        NotificationId id,
        Recipient recipient,
        MessageBody body,
        String subject
) implements Notification {

    public EmailNotification {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(body, "body must not be null");
        Objects.requireNonNull(subject, "subject must not be null");
        if (subject.isBlank()) {
            throw new IllegalArgumentException("subject must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.EMAIL;
    }
}
