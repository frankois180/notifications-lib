package com.notifications.pipeline;

import com.notifications.contract.NotificationEventPublisher;
import com.notifications.contract.NotificationProvider;
import com.notifications.contract.NotificationValidator;
import com.notifications.contract.RetryPolicy;
import com.notifications.contract.TemplateEngine;
import com.notifications.domain.Notification;
import com.notifications.exception.ProviderException;
import com.notifications.result.NotificationResult;

import java.util.Objects;

public final class SendPipeline {

    private final ProviderRegistry providerRegistry;
    private final NotificationValidator validator;
    private final TemplateEngine templateEngine;
    private final RetryPolicy retryPolicy;
    private final NotificationEventPublisher eventPublisher;

    public SendPipeline(
            ProviderRegistry providerRegistry,
            NotificationValidator validator,
            TemplateEngine templateEngine,
            RetryPolicy retryPolicy,
            NotificationEventPublisher eventPublisher
    ) {
        this.providerRegistry = Objects.requireNonNull(providerRegistry, "providerRegistry must not be null");
        this.validator = Objects.requireNonNull(validator, "validator must not be null");
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine must not be null");
        this.retryPolicy = Objects.requireNonNull(retryPolicy, "retryPolicy must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    public NotificationResult send(Notification notification) {
        Objects.requireNonNull(notification, "notification must not be null");

        try {
            validator.validate(notification);
            Notification prepared = templateEngine.apply(notification);
            NotificationProvider provider = providerRegistry.resolve(prepared.channel());

            if (!provider.supports(prepared)) {
                throw new ProviderException(
                        "Provider '" + provider.providerName() + "' does not support notification: " + prepared.id()
                );
            }

            NotificationResult result = retryPolicy.execute(new RetryPolicy.RetryContext(prepared, provider));
            eventPublisher.onSuccess(result);
            return result;
        } catch (RuntimeException runtimeException) {
            eventPublisher.onFailure(notification, runtimeException);
            throw runtimeException;
        }
    }
}
