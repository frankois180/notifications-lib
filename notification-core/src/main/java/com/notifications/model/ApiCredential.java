package com.notifications.model;

import java.util.Objects;

public record ApiCredential(String key, String secret) {

    public ApiCredential {
        Objects.requireNonNull(key, "key must not be null");
        Objects.requireNonNull(secret, "secret must not be null");
        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        if (secret.isBlank()) {
            throw new IllegalArgumentException("secret must not be blank");
        }
    }

    @Override
    public String toString() {
        return "ApiCredential[key=****, secret=****]";
    }
}
