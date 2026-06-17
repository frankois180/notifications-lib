package com.notifications.model;

import java.util.Objects;

public record Recipient(String value) {

    public Recipient {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
