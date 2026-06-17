package com.notifications.config;

import com.notifications.contract.NotificationProvider;

import java.util.Objects;

public record EmailChannelConfig(
        NotificationProvider provider,
        String fromAddress
) {

    public EmailChannelConfig {
        Objects.requireNonNull(provider, "provider must not be null");
        Objects.requireNonNull(fromAddress, "fromAddress must not be null");
        if (fromAddress.isBlank()) {
            throw new IllegalArgumentException("fromAddress must not be blank");
        }
    }
}
