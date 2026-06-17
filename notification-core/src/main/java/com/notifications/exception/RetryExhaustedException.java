package com.notifications.exception;

public class RetryExhaustedException extends NotificationException {

    private final int attempts;

    public RetryExhaustedException(String message, int attempts, Throwable cause) {
        super(message, cause);
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }
}
