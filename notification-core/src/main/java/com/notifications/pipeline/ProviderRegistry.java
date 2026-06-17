package com.notifications.pipeline;

import com.notifications.contract.NotificationProvider;
import com.notifications.exception.ConfigurationException;
import com.notifications.model.NotificationChannel;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class ProviderRegistry {

    private final Map<NotificationChannel, NotificationProvider> providers;

    public ProviderRegistry(Map<NotificationChannel, NotificationProvider> providers) {
        Objects.requireNonNull(providers, "providers must not be null");
        this.providers = Map.copyOf(providers);
    }

    public static ProviderRegistry of(NotificationProvider... providers) {
        Map<NotificationChannel, NotificationProvider> map = new EnumMap<>(NotificationChannel.class);
        for (NotificationProvider provider : providers) {
            map.put(provider.channel(), provider);
        }
        return new ProviderRegistry(map);
    }

    public NotificationProvider resolve(NotificationChannel channel) {
        NotificationProvider provider = providers.get(channel);
        if (provider == null) {
            throw new ConfigurationException("No provider configured for channel: " + channel);
        }
        return provider;
    }
}
