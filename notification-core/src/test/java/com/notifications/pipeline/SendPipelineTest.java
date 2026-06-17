package com.notifications.pipeline;

import com.notifications.contract.NotificationProvider;
import com.notifications.domain.EmailNotification;
import com.notifications.event.NoOpEventPublisher;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.result.NotificationResult;
import com.notifications.retry.NoRetryPolicy;
import com.notifications.template.NoOpTemplateEngine;
import com.notifications.validation.NoOpValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendPipelineTest {

    @Mock
    private NotificationProvider emailProvider;

    @Test
    void sendsNotificationThroughConfiguredProvider() {
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
                "msg-123",
                Map.of()
        );

        when(emailProvider.channel()).thenReturn(NotificationChannel.EMAIL);
        when(emailProvider.providerName()).thenReturn("sendgrid");
        when(emailProvider.supports(notification)).thenReturn(true);
        when(emailProvider.send(notification)).thenReturn(expected);

        SendPipeline pipeline = new SendPipeline(
                ProviderRegistry.of(emailProvider),
                new NoOpValidator(),
                new NoOpTemplateEngine(),
                new NoRetryPolicy(),
                new NoOpEventPublisher()
        );

        NotificationResult result = pipeline.send(notification);

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertEquals("sendgrid", result.providerName());
        verify(emailProvider).send(any(EmailNotification.class));
    }

    @Test
    void failsWhenChannelHasNoProvider() {
        SendPipeline pipeline = new SendPipeline(
                ProviderRegistry.of(),
                new NoOpValidator(),
                new NoOpTemplateEngine(),
                new NoRetryPolicy(),
                new NoOpEventPublisher()
        );

        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello"),
                "Subject"
        );

        assertThrows(com.notifications.exception.ConfigurationException.class, () -> pipeline.send(notification));
    }
}
