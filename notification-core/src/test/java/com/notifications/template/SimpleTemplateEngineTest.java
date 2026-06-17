package com.notifications.template;

import com.notifications.domain.EmailNotification;
import com.notifications.domain.SmsNotification;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTemplateEngineTest {

    @Test
    void replacesPlaceholdersInEmailBodyAndSubject() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine(Map.of(
                "name", "Ana",
                "code", "123456"
        ));

        EmailNotification rendered = (EmailNotification) engine.apply(new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello {{name}}, your code is {{code}}"),
                "Welcome {{name}}"
        ));

        assertEquals("Hello Ana, your code is 123456", rendered.body().value());
        assertEquals("Welcome Ana", rendered.subject());
    }

    @Test
    void replacesPlaceholdersInSmsBody() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine(Map.of("code", "999999"));

        SmsNotification rendered = (SmsNotification) engine.apply(new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Verification code: {{code}}")
        ));

        assertEquals("Verification code: 999999", rendered.body().value());
    }

    @Test
    void keepsUnknownPlaceholderUntouched() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine(Map.of());

        SmsNotification rendered = (SmsNotification) engine.apply(new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Value: {{missing}}")
        ));

        assertEquals("Value: {{missing}}", rendered.body().value());
    }
}
