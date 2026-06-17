package com.notifications.api;

import com.notifications.config.EmailChannelConfig;
import com.notifications.contract.NotificationProvider;
import com.notifications.domain.EmailNotification;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationClientAsyncTest {

    @Mock
    private NotificationProvider emailProvider;

    @Test
    void sendAsyncReturnsResult() throws Exception {
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
                .executor(Executors.newSingleThreadExecutor())
                .build();

        CompletableFuture<NotificationResult> future = client.sendAsync(notification);
        NotificationResult result = future.get();

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertTrue(result.isSuccess());
    }
}
