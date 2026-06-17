package com.notifications.event;

@FunctionalInterface
public interface NotificationEventListener {

    void onEvent(NotificationEvent event);
}
