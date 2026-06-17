package com.notifications.examples;

import com.notifications.api.NotificationClient;
import com.notifications.config.SlackChannelConfig;
import com.notifications.domain.SlackNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.providers.slack.SlackWebhookProvider;
import com.notifications.result.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationExamplesTest {

    private static final String SLACK_WEBHOOK = "https://hooks.slack.com/services/T000/B000/XXXXXXXX";

    @Test
    void slackExampleWorks() {
        NotificationClient client = NotificationClient.builder()
                .slack(new SlackChannelConfig(
                        new SlackWebhookProvider(new ApiCredential("slack", "token"), SLACK_WEBHOOK),
                        SLACK_WEBHOOK
                ))
                .build();

        NotificationResult result = client.send(new SlackNotification(
                NotificationId.generate(),
                new Recipient("#alerts"),
                new MessageBody("Hello Slack"),
                "alerts"
        ));

        assertEquals(NotificationStatus.SUCCESS, result.status());
        assertTrue(result.externalMessageId().startsWith("slack-"));
    }
}
