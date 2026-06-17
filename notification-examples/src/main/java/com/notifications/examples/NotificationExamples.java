package com.notifications.examples;

import com.notifications.api.NotificationClient;
import com.notifications.config.EmailChannelConfig;
import com.notifications.config.PushChannelConfig;
import com.notifications.config.SlackChannelConfig;
import com.notifications.config.SmsChannelConfig;
import com.notifications.domain.EmailNotification;
import com.notifications.domain.PushNotification;
import com.notifications.domain.SlackNotification;
import com.notifications.domain.SmsNotification;
import com.notifications.domain.Notification;
import com.notifications.event.DefaultEventPublisher;
import com.notifications.model.ApiCredential;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationId;
import com.notifications.model.Recipient;
import com.notifications.providers.email.SendGridProvider;
import com.notifications.providers.push.FcmProvider;
import com.notifications.providers.slack.SlackWebhookProvider;
import com.notifications.providers.sms.TwilioProvider;
import com.notifications.result.NotificationResult;
import com.notifications.template.SimpleTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public final class NotificationExamples {

    private static final String SLACK_WEBHOOK = "https://hooks.slack.com/services/T000/B000/XXXXXXXX";

    private NotificationExamples() {
    }

    public static void main(String[] args) throws Exception {
        ApiCredential sendGridCredential = new ApiCredential("SG.xxxx", "secret");
        ApiCredential twilioCredential = new ApiCredential("ACxxxx", "auth-token");
        ApiCredential fcmCredential = new ApiCredential("firebase", "server-key");
        ApiCredential slackCredential = new ApiCredential("slack", "webhook-token");

        NotificationClient client = NotificationClient.builder()
                .email(new EmailChannelConfig(
                        new SendGridProvider(sendGridCredential, "noreply@example.com"),
                        "noreply@example.com"
                ))
                .sms(new SmsChannelConfig(
                        new TwilioProvider(twilioCredential, "+14155551234"),
                        "+14155551234"
                ))
                .push(new PushChannelConfig(
                        new FcmProvider(fcmCredential, "my-firebase-project")
                ))
                .slack(new SlackChannelConfig(
                        new SlackWebhookProvider(slackCredential, SLACK_WEBHOOK),
                        SLACK_WEBHOOK
                ))
                .templateEngine(new SimpleTemplateEngine(Map.of(
                        "name", "Francisco",
                        "code", "123456"
                )))
                .eventPublisher(new DefaultEventPublisher(event ->
                        log.info("Event -> {} | {}", event.type(), event.message())
                ))
                .build();

        NotificationResult emailResult = client.send(new EmailNotification(
                NotificationId.generate(),
                new Recipient("user@example.com"),
                new MessageBody("Hello {{name}}, welcome to our platform!"),
                "Welcome {{name}}"
        ));

        NotificationResult smsResult = client.send(new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Your verification code is {{code}}")
        ));

        NotificationResult pushResult = client.send(new PushNotification(
                NotificationId.generate(),
                new Recipient("device-token-abc123"),
                new MessageBody("You have a new message"),
                "New message"
        ));

        NotificationResult slackResult = client.send(new SlackNotification(
                NotificationId.generate(),
                new Recipient("#alerts"),
                new MessageBody("Deployment completed for {{name}}"),
                "deployments"
        ));

        log.info("Email: {}", emailResult);
        log.info("SMS: {}", smsResult);
        log.info("Push: {}", pushResult);
        log.info("Slack: {}", slackResult);

        List<Notification> batch = List.of(
                new EmailNotification(
                        NotificationId.generate(),
                        new Recipient("batch@example.com"),
                        new MessageBody("Batch email for {{name}}"),
                        "Batch {{name}}"
                ),
                new SmsNotification(
                        NotificationId.generate(),
                        new Recipient("+14155552671"),
                        new MessageBody("Batch SMS code {{code}}")
                ),
                new SlackNotification(
                        NotificationId.generate(),
                        new Recipient("#ops"),
                        new MessageBody("Batch alert for {{name}}"),
                        "ops"
                )
        );

        List<NotificationResult> batchResults = client.sendBatchAsync(batch).get();
        log.info("Batch async results: {}", batchResults.size());
        batchResults.forEach(result ->
                log.info("  - {} via {}", result.channel(), result.providerName())
        );
    }
}
