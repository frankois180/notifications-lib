package com.notifications.providers.email;

import com.notifications.domain.EmailNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.result.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendGridProviderTest {

    @Test
    void simulatesSuccessfulSend() {
        SendGridProvider provider = new SendGridProvider(
                new ApiCredential("SG.test", "secret"),
                "noreply@example.com"
        );

        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello"),
                "Welcome"
        );

        NotificationResult result = provider.send(notification);

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertEquals(NotificationChannel.EMAIL, result.channel());
        assertEquals("sendgrid", result.providerName());
        assertTrue(result.externalMessageId().startsWith("sg-"));
    }
}
