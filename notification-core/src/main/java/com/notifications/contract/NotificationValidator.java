package com.notifications.contract;

import com.notifications.domain.Notification;

public interface NotificationValidator {

    void validate(Notification notification);
}
