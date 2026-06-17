package com.notifications.domain;

import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;

public sealed interface Notification
        permits EmailNotification, SmsNotification, PushNotification, SlackNotification {

    NotificationId id();

    Recipient recipient();

    MessageBody body();

    NotificationChannel channel();
}
