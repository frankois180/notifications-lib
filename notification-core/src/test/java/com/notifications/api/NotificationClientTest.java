package com.notifications.api;

import com.notifications.config.EmailChannelConfig;
import com.notifications.contract.NotificationProvider;
import com.notifications.domain.EmailNotification;
import com.notifications.exception.ValidationException;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.result.NotificationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private NotificationProvider emailProvider;

    @Test
    void sendsEmailEndToEnd() {
        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello"),
                "Subject"
        );

        NotificationResult expected = NotificationResult.success(
                notification.id(),
                NotificationChannel.EMAIL,
                "sendgrid",
                "msg-1",
                Map.of()
        );

        when(emailProvider.channel()).thenReturn(NotificationChannel.EMAIL);
        when(emailProvider.providerName()).thenReturn("sendgrid");
        when(emailProvider.supports(any())).thenReturn(true);
        when(emailProvider.send(any())).thenReturn(expected);

        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(emailProvider, "noreply@example.com"))
                .build();

        NotificationResult result = client.send(notification);

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertEquals("sendgrid", result.providerName());
        assertTrue(result.isSuccess());
    }

    @Test
    void rejectsInvalidEmail() {
        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(emailProvider, "noreply@example.com"))
                .build();

        assertThrows(ValidationException.class, () -> client.send(new EmailNotification(
                NotificationId.generate(),
                new Recipient("not-an-email"),
                new MessageBody("Hello"),
                "Subject"
        )));
    }
}
