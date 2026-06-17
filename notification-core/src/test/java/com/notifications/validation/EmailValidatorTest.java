package com.notifications.validation;

import com.notifications.domain.EmailNotification;
import com.notifications.exception.ValidationException;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailValidatorTest {

    private final EmailValidator validator = new EmailValidator();

    @Test
    void acceptsValidEmail() {
        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello"),
                "Subject"
        );

        assertDoesNotThrow(() -> validator.validate(notification));
    }

    @Test
    void rejectsInvalidEmail() {
        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("invalid-email"),
                new MessageBody("Hello"),
                "Subject"
        );

        assertThrows(ValidationException.class, () -> validator.validate(notification));
    }
}
