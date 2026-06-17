package com.notifications.event;

import com.notifications.domain.EmailNotification;
import com.notifications.exception.ValidationException;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.result.NotificationResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultEventPublisherTest {

    @Test
    void publishesSuccessEvent() {
        List<NotificationEvent> events = new ArrayList<>();
        DefaultEventPublisher publisher = new DefaultEventPublisher(events::add);

        NotificationId id = NotificationId.generate();
        NotificationResult result = NotificationResult.success(
                id,
                NotificationChannel.EMAIL,
                "sendgrid",
                "msg-1",
                Map.of()
        );

        publisher.onSuccess(result);

        assertEquals(1, events.size());
        assertEquals(NotificationEventType.SENT, events.getFirst().type());
        assertEquals(id, events.getFirst().notificationId());
        assertEquals(NotificationStatus.SUCCESS, events.getFirst().status());
    }

    @Test
    void publishesFailureEvent() {
        List<NotificationEvent> events = new ArrayList<>();
        DefaultEventPublisher publisher = new DefaultEventPublisher(events::add);

        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                new Recipient("bad-email"),
                new MessageBody("Hello"),
                "Subject"
        );

        publisher.onFailure(notification, new ValidationException("Invalid email"));

        assertEquals(1, events.size());
        assertEquals(NotificationEventType.FAILED, events.getFirst().type());
        assertEquals(NotificationChannel.EMAIL, events.getFirst().channel());
        assertNotNull(events.getFirst().cause());
    }
}
