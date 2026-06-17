package com.notifications.config;

import com.notifications.contract.NotificationProvider;

import java.util.Objects;

public record SlackChannelConfig(
        NotificationProvider provider,
        String webhookUrl
) {

    public SlackChannelConfig {
        Objects.requireNonNull(provider, "provider must not be null");
        Objects.requireNonNull(webhookUrl, "webhookUrl must not be null");
        if (webhookUrl.isBlank()) {
            throw new IllegalArgumentException("webhookUrl must not be blank");
        }
    }
}
