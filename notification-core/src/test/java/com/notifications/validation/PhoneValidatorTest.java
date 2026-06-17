package com.notifications.validation;

import com.notifications.domain.SmsNotification;
import com.notifications.exception.ValidationException;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneValidatorTest {

    private final PhoneValidator validator = new PhoneValidator();

    @Test
    void acceptsValidE164Number() {
        SmsNotification notification = new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Hello")
        );

        assertDoesNotThrow(() -> validator.validate(notification));
    }

    @Test
    void rejectsNumberWithoutPlusPrefix() {
        SmsNotification notification = new SmsNotification(
                NotificationId.generate(),
                new Recipient("14155552671"),
                new MessageBody("Hello")
        );

        assertThrows(ValidationException.class, () -> validator.validate(notification));
    }

    @Test
    void rejectsInvalidNumber() {
        SmsNotification notification = new SmsNotification(
                NotificationId.generate(),
                new Recipient("+123"),
                new MessageBody("Hello")
        );

        assertThrows(ValidationException.class, () -> validator.validate(notification));
    }
}
