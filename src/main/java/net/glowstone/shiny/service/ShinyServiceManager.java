package net.glowstone.shiny.service;

import com.google.common.base.Optional;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.ProvisioningException;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.util.Owner;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link ServiceManager}.
 */
public class ShinyServiceManager implements ServiceManager {

    private final Map<Class<?>, Registration> services = new HashMap<>();

    @Override
    public <T> void setProvider(Class<T> service, T provider, boolean replaceable, Owner owner) throws ProviderExistsException {
        Registration existing = services.get(service);
        if (existing != null && !existing.replaceable) {
            throw new ProviderExistsException("Service " + service.getName() + " is already provided by " + existing.owner.getId() + " (" + existing.provider + ")");
        }
        services.put(service, new Registration(provider, replaceable, owner));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> provide(Class<T> service) {
        if (services.containsKey(service)) {
            return Optional.of((T) services.get(service).provider);
        } else {
            return Optional.absent();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T provideUnchecked(Class<T> service) throws ProvisioningException {
        if (services.containsKey(service)) {
            return (T) services.get(service).provider;
        } else {
            throw new ProvisioningException("Service " + service.getName() + " has no provider", service);
        }
    }

    private static class Registration {
        private final Object provider;
        private final boolean replaceable;
        private final Owner owner;

        private Registration(Object provider, boolean replaceable, Owner owner) {
            this.provider = provider;
            this.replaceable = replaceable;
            this.owner = owner;
        }
    }
}
