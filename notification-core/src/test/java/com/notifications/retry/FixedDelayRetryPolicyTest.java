package com.notifications.retry;

import com.notifications.contract.NotificationProvider;
import com.notifications.contract.RetryPolicy;
import com.notifications.domain.SmsNotification;
import com.notifications.exception.ProviderException;
import com.notifications.exception.RetryExhaustedException;
import com.notifications.model.MessageBody;
import com.notifications.model.NotificationChannel;
import com.notifications.model.NotificationId;
import com.notifications.model.NotificationStatus;
import com.notifications.model.Recipient;
import com.notifications.result.NotificationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedDelayRetryPolicyTest {

    @Mock
    private NotificationProvider provider;

    @Test
    void retriesUntilSuccess() {
        SmsNotification notification = new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Hello")
        );

        NotificationResult success = NotificationResult.success(
                notification.id(),
                NotificationChannel.SMS,
                "twilio",
                "SM123",
                Map.of()
        );

        when(provider.send(notification))
                .thenThrow(new ProviderException("temporary failure"))
                .thenReturn(success);

        FixedDelayRetryPolicy retryPolicy = new FixedDelayRetryPolicy(2, 0);
        NotificationResult result = retryPolicy.execute(new RetryPolicy.RetryContext(notification, provider));

        assertEquals(NotificationStatus.SUCCESS, result.status());
        verify(provider, times(2)).send(notification);
    }

    @Test
    void throwsWhenAttemptsAreExhausted() {
        SmsNotification notification = new SmsNotification(
                NotificationId.generate(),
                new Recipient("+14155552671"),
                new MessageBody("Hello")
        );

        when(provider.send(notification)).thenThrow(new ProviderException("still failing"));

        FixedDelayRetryPolicy retryPolicy = new FixedDelayRetryPolicy(3, 0);

        assertThrows(RetryExhaustedException.class, () ->
                retryPolicy.execute(new RetryPolicy.RetryContext(notification, provider))
        );

        verify(provider, times(3)).send(notification);
    }
}
