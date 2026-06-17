package com.notifications.retry;

import com.notifications.contract.RetryPolicy;
import com.notifications.exception.ProviderException;
import com.notifications.exception.RetryExhaustedException;
import com.notifications.result.NotificationResult;

public final class FixedDelayRetryPolicy implements RetryPolicy {

    private final int maxAttempts;
    private final long delayMillis;

    public FixedDelayRetryPolicy(int maxAttempts, long delayMillis) {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be >= 1");
        }
        if (delayMillis < 0) {
            throw new IllegalArgumentException("delayMillis must be >= 0");
        }
        this.maxAttempts = maxAttempts;
        this.delayMillis = delayMillis;
    }

    @Override
    public NotificationResult execute(RetryContext context) {
        ProviderException lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return context.provider().send(context.notification());
            } catch (ProviderException error) {
                lastError = error;
                if (attempt < maxAttempts) {
                    sleep(delayMillis);
                }
            }
        }

        throw new RetryExhaustedException(
                "Retry exhausted after " + maxAttempts + " attempts",
                maxAttempts,
                lastError
        );
    }

    private static void sleep(long millis) {
        if (millis == 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new ProviderException("Retry interrupted", interrupted);
        }
    }
}
