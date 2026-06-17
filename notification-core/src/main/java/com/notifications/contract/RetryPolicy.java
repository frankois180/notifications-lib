package com.notifications.contract;

import com.notifications.domain.Notification;
import com.notifications.result.NotificationResult;

@FunctionalInterface
public interface RetryPolicy {

    NotificationResult execute(RetryContext context);

    record RetryContext(Notification notification, NotificationProvider provider) {
    }
}
