package com.notifications.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.notifications.contract.NotificationValidator;
import com.notifications.domain.Notification;
import com.notifications.domain.SmsNotification;
import com.notifications.exception.ValidationException;
import com.notifications.model.NotificationChannel;

public final class PhoneValidator implements NotificationValidator {

    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    @Override
    public void validate(Notification notification) {
        if (notification.channel() != NotificationChannel.SMS) {
            return;
        }

        if (!(notification instanceof SmsNotification smsNotification)) {
            throw new ValidationException("Expected SmsNotification for SMS channel");
        }

        String rawNumber = smsNotification.recipient().value();
        if (!rawNumber.startsWith("+")) {
            throw new ValidationException("Phone number must be in E.164 format: " + rawNumber);
        }

        try {
            Phonenumber.PhoneNumber parsed = PHONE_UTIL.parse(rawNumber, null);
            if (!PHONE_UTIL.isValidNumber(parsed)) {
                throw new ValidationException("Invalid phone number: " + rawNumber);
            }

            String e164 = PHONE_UTIL.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
            if (!e164.equals(rawNumber)) {
                throw new ValidationException("Phone number must be normalized E.164: " + rawNumber);
            }
        } catch (NumberParseException parseException) {
            throw new ValidationException("Invalid phone number: " + rawNumber, parseException);
        }
    }
}
