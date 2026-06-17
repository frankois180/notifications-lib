package com.notifications.providers.slack;

import com.notifications.domain.SlackNotification;
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

class SlackWebhookProviderTest {

    @Test
    void simulatesSuccessfulSlackSend() {
        SlackWebhookProvider provider = new SlackWebhookProvider(
                new ApiCredential("slack", "token"),
                "https://hooks.slack.com/services/T000/B000/XXXXXXXX"
        );

        SlackNotification notification = new SlackNotification(
                NotificationId.generate(),
                new Recipient("#alerts"),
                new MessageBody("Deployment finished"),
                "deployments"
        );

        NotificationResult result = provider.send(notification);

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertEquals(NotificationChannel.SLACK, result.channel());
        assertEquals("slack-webhook", result.providerName());
        assertTrue(result.externalMessageId().startsWith("slack-"));
        assertEquals("deployments", result.metadata().get("channel"));
    }
}
