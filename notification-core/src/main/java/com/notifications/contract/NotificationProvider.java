package com.notifications.contract;

import com.notifications.domain.Notification;
import com.notifications.model.NotificationChannel;
import com.notifications.result.NotificationResult;

public interface NotificationProvider {

    NotificationChannel channel();

    String providerName();

    boolean supports(Notification notification);

    NotificationResult send(Notification notification);
}
