package com.notifications.contract;

import com.notifications.domain.Notification;

public interface TemplateEngine {

    Notification apply(Notification notification);
}
