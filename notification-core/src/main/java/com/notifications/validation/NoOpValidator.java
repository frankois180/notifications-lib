package com.notifications.validation;

import com.notifications.contract.NotificationValidator;
import com.notifications.domain.Notification;

public final class NoOpValidator implements NotificationValidator {

    @Override
    public void validate(Notification notification) {
        // no-op
    }
}
