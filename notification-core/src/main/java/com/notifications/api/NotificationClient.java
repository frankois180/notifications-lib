package com.notifications.api;

import com.notifications.config.EmailChannelConfig;
import com.notifications.config.PushChannelConfig;
import com.notifications.config.SlackChannelConfig;
import com.notifications.config.SmsChannelConfig;
import com.notifications.contract.NotificationEventPublisher;
import com.notifications.contract.NotificationProvider;
import com.notifications.contract.NotificationValidator;
import com.notifications.contract.RetryPolicy;
import com.notifications.contract.TemplateEngine;
import com.notifications.domain.Notification;
import com.notifications.event.NoOpEventPublisher;
import com.notifications.exception.ConfigurationException;
import com.notifications.model.NotificationChannel;
import com.notifications.pipeline.ProviderRegistry;
import com.notifications.pipeline.SendPipeline;
import com.notifications.result.NotificationResult;
import com.notifications.retry.NoRetryPolicy;
import com.notifications.template.NoOpTemplateEngine;
import com.notifications.validation.CompositeValidator;
import com.notifications.validation.EmailValidator;
import com.notifications.validation.NoOpValidator;
import com.notifications.validation.PhoneValidator;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public final class NotificationClient {

    private final SendPipeline pipeline;
    private final Executor executor;

    NotificationClient(SendPipeline pipeline, Executor executor) {
        this.pipeline = pipeline;
        this.executor = executor;
    }

    public NotificationResult send(Notification notification) {
        return pipeline.send(notification);
    }

    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> pipeline.send(notification), executor);
    }

    public CompletableFuture<List<NotificationResult>> sendBatchAsync(List<Notification> notifications) {
        Objects.requireNonNull(notifications, "notifications must not be null");
        if (notifications.isEmpty()) {
            return CompletableFuture.completedFuture(List.of());
        }

        List<CompletableFuture<NotificationResult>> futures = notifications.stream()
                .map(this::sendAsync)
                .toList();

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private EmailChannelConfig email;
        private SmsChannelConfig sms;
        private PushChannelConfig push;
        private SlackChannelConfig slack;
        private final NotificationValidator validator = new CompositeValidator(
                new NoOpValidator(),
                new EmailValidator(),
                new PhoneValidator()
        );
        private TemplateEngine templateEngine = new NoOpTemplateEngine();
        private final RetryPolicy retryPolicy = new NoRetryPolicy();
        private NotificationEventPublisher eventPublisher = new NoOpEventPublisher();
        private Executor executor = ForkJoinPool.commonPool();

        public Builder email(EmailChannelConfig config) {
            this.email = config;
            return this;
        }

        public Builder sms(SmsChannelConfig config) {
            this.sms = config;
            return this;
        }

        public Builder push(PushChannelConfig config) {
            this.push = config;
            return this;
        }

        public Builder slack(SlackChannelConfig config) {
            this.slack = config;
            return this;
        }


        public Builder templateEngine(TemplateEngine templateEngine) {
            this.templateEngine = Objects.requireNonNull(templateEngine);
            return this;
        }

        public Builder eventPublisher(NotificationEventPublisher eventPublisher) {
            this.eventPublisher = Objects.requireNonNull(eventPublisher);
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = Objects.requireNonNull(executor);
            return this;
        }

        public NotificationClient build() {
            Map<NotificationChannel, NotificationProvider> providers = new EnumMap<>(NotificationChannel.class);

            if (email != null) {
                providers.put(NotificationChannel.EMAIL, email.provider());
            }
            if (sms != null) {
                providers.put(NotificationChannel.SMS, sms.provider());
            }
            if (push != null) {
                providers.put(NotificationChannel.PUSH, push.provider());
            }
            if (slack != null) {
                providers.put(NotificationChannel.SLACK, slack.provider());
            }

            if (providers.isEmpty()) {
                throw new ConfigurationException("At least one channel must be configured");
            }

            SendPipeline pipeline = new SendPipeline(
                    new ProviderRegistry(providers),
                    validator,
                    templateEngine,
                    retryPolicy,
                    eventPublisher
            );

            return new NotificationClient(pipeline, executor);
        }
    }
}
