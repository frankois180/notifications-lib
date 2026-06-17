package com.notifications.domain;

import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;

import java.util.Objects;

public record SlackNotification(
        NotificationId id,
        Recipient recipient,
        MessageBody body,
        String channelName
) implements Notification {

    public SlackNotification {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(body, "body must not be null");
        Objects.requireNonNull(channelName, "channelName must not be null");
        if (channelName.isBlank()) {
            throw new IllegalArgumentException("channelName must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SLACK;
    }
}
