package com.notifications.config;

import com.notifications.contract.NotificationProvider;

import java.util.Objects;

public record PushChannelConfig(
        NotificationProvider provider
) {

    public PushChannelConfig {
        Objects.requireNonNull(provider, "provider must not be null");
    }
}
