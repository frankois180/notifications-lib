package com.notifications.event;

import com.notifications.contract.NotificationEventPublisher;
import com.notifications.domain.Notification;
import com.notifications.result.NotificationResult;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DefaultEventPublisher implements NotificationEventPublisher {

    private final List<NotificationEventListener> listeners;

    public DefaultEventPublisher(NotificationEventListener... listeners) {
        Objects.requireNonNull(listeners, "listeners must not be null");
        if (listeners.length == 0) {
            throw new IllegalArgumentException("listeners must not be empty");
        }
        this.listeners = List.copyOf(Arrays.asList(listeners));
    }

    @Override
    public void onSuccess(NotificationResult result) {
        publish(NotificationEvent.success(result));
    }

    @Override
    public void onFailure(Notification notification, Throwable error) {
        publish(NotificationEvent.failure(
                notification.id(),
                notification.channel(),
                error.getMessage() == null ? "Notification failed" : error.getMessage(),
                error
        ));
    }

    private void publish(NotificationEvent event) {
        for (NotificationEventListener listener : listeners) {
            listener.onEvent(event);
        }
    }
}
