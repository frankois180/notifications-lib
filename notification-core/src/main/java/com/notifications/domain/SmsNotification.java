package com.notifications.domain;

import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;

import java.util.Objects;

public record SmsNotification(
        NotificationId id,
        Recipient recipient,
        MessageBody body
) implements Notification {

    public SmsNotification {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(body, "body must not be null");
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SMS;
    }
}
