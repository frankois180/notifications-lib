package com.notifications.retry;

import com.notifications.contract.RetryPolicy;
import com.notifications.result.NotificationResult;

public final class NoRetryPolicy implements RetryPolicy {

    @Override
    public NotificationResult execute(RetryContext context) {
        return context.provider().send(context.notification());
    }
}
