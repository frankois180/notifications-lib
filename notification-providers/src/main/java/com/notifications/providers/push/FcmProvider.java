package com.notifications.providers.push;

import com.notifications.contract.NotificationProvider;
import com.notifications.domain.Notification;
import com.notifications.domain.PushNotification;
import com.notifications.model.ApiCredential;
import com.notifications.model.NotificationChannel;
import com.notifications.result.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FcmProvider implements NotificationProvider {

    private static final Logger log = LoggerFactory.getLogger(FcmProvider.class);

    private final ApiCredential credential;
    private final String projectId;

    public FcmProvider(ApiCredential credential, String projectId) {
        this.credential = Objects.requireNonNull(credential, "credential must not be null");
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        if (projectId.isBlank()) {
            throw new IllegalArgumentException("projectId must not be blank");
        }
    }

    @Override
    public NotificationChannel channel() {
        return NotificationChannel.PUSH;
    }

    @Override
    public String providerName() {
        return "fcm";
    }

    @Override
    public boolean supports(Notification notification) {
        return notification instanceof PushNotification;
    }

    @Override
    public NotificationResult send(Notification notification) {
        PushNotification push = (PushNotification) notification;
        String messageId = "fcm-" + UUID.randomUUID();

        log.info(
                "FCM simulated send | project={} token={} title={} credential={}",
                projectId,
                push.recipient().value(),
                push.title(),
                credential
        );

        return NotificationResult.success(
                push.id(),
                NotificationChannel.PUSH,
                providerName(),
                messageId,
                Map.of(
                        "projectId", projectId,
                        "providerResponse", "projects/" + projectId + "/messages/" + messageId
                )
        );
    }
}
