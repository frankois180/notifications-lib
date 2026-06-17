package com.notifications.contract;

import com.notifications.domain.Notification;
import com.notifications.result.NotificationResult;

public interface NotificationEventPublisher {

    void onSuccess(NotificationResult result);

    void onFailure(Notification notification, Throwable error);
}
