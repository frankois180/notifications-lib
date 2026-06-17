package com.notifications.event;

import com.notifications.contract.NotificationEventPublisher;
import com.notifications.domain.Notification;
import com.notifications.result.NotificationResult;

public final class NoOpEventPublisher implements NotificationEventPublisher {

    @Override
    public void onSuccess(NotificationResult result) {
        // no-op
    }

    @Override
    public void onFailure(Notification notification, Throwable error) {
        // no-op
    }
}
