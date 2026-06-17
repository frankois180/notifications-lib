package com.notifications.validation;

import com.notifications.contract.NotificationValidator;
import com.notifications.domain.Notification;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CompositeValidator implements NotificationValidator {

    private final List<NotificationValidator> validators;

    public CompositeValidator(NotificationValidator... validators) {
        Objects.requireNonNull(validators, "validators must not be null");
        if (validators.length == 0) {
            throw new IllegalArgumentException("validators must not be empty");
        }
        this.validators = List.copyOf(Arrays.asList(validators));
    }

    @Override
    public void validate(Notification notification) {
        for (NotificationValidator validator : validators) {
            validator.validate(notification);
        }
    }
}
