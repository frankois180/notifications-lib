package com.notifications.providers.slack;

import com.notifications.contract.NotificationProvider;
import com.notifications.domain.Notification;
import com.notifications.domain.SlackNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.NotificationChannel;
import com.notifications.result.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class SlackWebhookProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(SlackWebhookProvider.class);

    private final ApiCredential credential;
    private final String webhookUrl;

    public SlackWebhookProvider(ApiCredential credential, String webhookUrl) {
        this.credential = Objects.requireNonNull(credential, "credential must not be null");
        this.webhookUrl = Objects.requireNonNull(webhookUrl, "webhookUrl must not be null");
        if (webhookUrl.isBlank()) {
            throw new IllegalArgumentException("webhookUrl must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.SLACK;
    }

    @Override
    public String providerName() {
        return "slack-webhook";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof SlackNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        SlackNotification slack = (SlackNotification) notification;
        String messageId = "slack-" + UUID.randomUUID();

        log.info(
                "Slack simulated send | channel={} webhook={} credential={}",
                slack.channelName(),
                maskWebhook(webhookUrl),
                credential
        );

        return NotificationResult.success(
                slack.id(),
                NotificationChannel.SLACK,
                providerName(),
                messageId,
                Map.of(
                        "channel", slack.channelName(),
                        "providerResponse", "ok"
                )
        );
    }

    private static String maskWebhook(String url) {
        if (url.length() <= 20) {
            return "****";
        }
        return url.substring(0, 20) + "****";
    }
}
