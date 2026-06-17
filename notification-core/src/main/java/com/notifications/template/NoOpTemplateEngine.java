package com.notifications.template;

import com.notifications.contract.TemplateEngine;
import com.notifications.domain.Notification;

public final class NoOpTemplateEngine implements TemplateEngine {

    @Override
    public Notification apply(Notification notification) {
        return notification;
    }
}
