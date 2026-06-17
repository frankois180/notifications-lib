package com.notifications.api;

import com.notifications.config.EmailChannelConfig;
import com.notifications.config.SlackChannelConfig;
import com.notifications.config.SmsChannelConfig;
import com.notifications.contract.NotificationProvider;
import com.notifications.domain.EmailNotification;
import com.notifications.domain.Notification;
import com.notifications.domain.SlackNotification;
import com.notifications.domain.SmsNotification;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationClientBatchAsyncTest {

    @Mock
    private NotificationProvider emailProvider;

    @Mock
    private NotificationProvider smsProvider;

    @Mock
    private NotificationProvider slackProvider;

    @Test
    void sendBatchAsyncReturnsAllResults() throws Exception {
        when(emailProvider.channel()).thenReturn(NotificationChannel.EMAIL);
        when(emailProvider.providerName()).thenReturn("sendgrid");
        when(emailProvider.supports(any())).thenReturn(true);

        when(smsProvider.channel()).thenReturn(NotificationChannel.SMS);
        when(smsProvider.providerName()).thenReturn("twilio");
        when(smsProvider.supports(any())).thenReturn(true);

        when(slackProvider.channel()).thenReturn(NotificationChannel.SLACK);
        when(slackProvider.providerName()).thenReturn("slack-webhook");
        when(slackProvider.supports(any())).thenReturn(true);

        when(emailProvider.send(any())).thenAnswer(invocation -> {
            EmailNotification notification = invocation.getArgument(0);
            return success(notification.id(), NotificationChannel.EMAIL, "sendgrid");
        });
        when(smsProvider.send(any())).thenAnswer(invocation -> {
            SmsNotification notification = invocation.getArgument(0);
            return success(notification.id(), NotificationChannel.SMS, "twilio");
        });
        when(slackProvider.send(any())).thenAnswer(invocation -> {
            SlackNotification notification = invocation.getArgument(0);
            return success(notification.id(), NotificationChannel.SLACK, "slack-webhook");
        });

        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(emailProvider, "noreply@example.com"))
                .sms(new SmsChannelConfig(smsProvider, "+14155551234"))
                .slack(new SlackChannelConfig(slackProvider, "https://hooks.slack.com/services/test"))
                .executor(Executors.newFixedThreadPool(3))
                .build();

        List<Notification> batch = List.of(
                new EmailNotification(
                        NotificationId.generate(),
                        new Recipient("user@example.com"),
                        new MessageBody("Hello"),
                        "Subject"
                ),
                new SmsNotification(
                        NotificationId.generate(),
                        new Recipient("+14155552671"),
                        new MessageBody("SMS")
                ),
                new SlackNotification(
                        NotificationId.generate(),
                        new Recipient("#alerts"),
                        new MessageBody("Slack"),
                        "alerts"
                )
        );

        CompletableFuture<List<NotificationResult>> future = client.sendBatchAsync(batch);
        List<NotificationResult> results = future.get();

        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(result -> result.status() == NotificationStatus.SUCCESS));
    }

    @Test
    void sendBatchAsyncWithEmptyListReturnsEmptyResult() throws Exception {
        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(emailProvider, "noreply@example.com"))
                .build();

        when(emailProvider.channel()).thenReturn(NotificationChannel.EMAIL);

        List<NotificationResult> results = client.sendBatchAsync(List.of()).get();

        assertTrue(results.isEmpty());
    }

    private static NotificationResult success(
            NotificationId id,
            NotificationChannel channel,
            String providerName
    ) {
        return NotificationResult.success(id, channel, providerName, "msg-1", Map.of());
    }
}
