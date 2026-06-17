package com.notifications.template;

import com.notifications.contract.TemplateEngine;
import com.notifications.domain.EmailNotification;
import com.notifications.domain.Notification;
import com.notifications.domain.PushNotification;
import com.notifications.domain.SlackNotification;
import com.notifications.domain.SmsNotification;
import com.notifications.model.MessageBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SimpleTemplateEngine implements TemplateEngine {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([a-zA-Z0-9_]+)}}");

    private final Map<String, String> variables;

    public SimpleTemplateEngine(Map<String, String> variables) {
        Objects.requireNonNull(variables, "variables must not be null");
        this.variables = Map.copyOf(variables);
    }

    @Override
    public Notification apply(Notification notification) {
        Objects.requireNonNull(notification, "notification must not be null");

        return switch (notification) {
            case EmailNotification email -> new EmailNotification(
                    email.id(),
                    email.recipient(),
                    new MessageBody(render(email.body().value())),
                    render(email.subject())
            );
            case SmsNotification sms -> new SmsNotification(
                    sms.id(),
                    sms.recipient(),
                    new MessageBody(render(sms.body().value()))
            );
            case PushNotification push -> new PushNotification(
                    push.id(),
                    push.recipient(),
                    new MessageBody(render(push.body().value())),
                    render(push.title())
            );
            case SlackNotification slack -> new SlackNotification(
                    slack.id(),
                    slack.recipient(),
                    new MessageBody(render(slack.body().value())),
                    slack.channelName()
            );
        };
    }

    public SimpleTemplateEngine withVariable(String key, String value) {
        Map<String, String> merged = new HashMap<>(variables);
        merged.put(key, value);
        return new SimpleTemplateEngine(merged);
    }

    private String render(String template) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = variables.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
    }
}
