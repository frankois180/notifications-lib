package com.notifications.validation;

import com.notifications.contract.NotificationValidator;
import com.notifications.domain.EmailNotification;
import com.notifications.domain.Notification;
import com.notifications.exception.ValidationException;
import com.notifications.model.NotificationChannel;

import java.util.regex.Pattern;

public final class EmailValidator implements NotificationValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public void validate(Notification notification) {
        if (notification.channel() != NotificationChannel.EMAIL) {
            return;
        }

        if (!(notification instanceof EmailNotification emailNotification)) {
            throw new ValidationException("Expected EmailNotification for EMAIL channel");
        }

        String address = emailNotification.recipient().value();
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new ValidationException("Invalid email address: " + address);
        }
    }
}
