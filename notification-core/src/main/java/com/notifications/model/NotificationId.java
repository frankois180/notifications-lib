package com.notifications.model;

import java.util.Objects;
import java.util.UUID;

public record NotificationId(String value) {

    public NotificationId {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return value;
    }
}
