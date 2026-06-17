package com.notifications.config;

import com.notifications.contract.NotificationProvider;

import java.util.Objects;

public record SmsChannelConfig(
        NotificationProvider provider,
        String fromNumber
) {

    public SmsChannelConfig {
        Objects.requireNonNull(provider, "provider must not be null");
        Objects.requireNonNull(fromNumber, "fromNumber must not be null");
        if (fromNumber.isBlank()) {
            throw new IllegalArgumentException("fromNumber must not be blank");
        }
    }
}
