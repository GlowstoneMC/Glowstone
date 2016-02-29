package net.glowstone.interfaces.event;

import org.spongepowered.api.event.cause.Cause;

public interface IEvent {

    void setCause(Cause cause);

    Cause getCause();
}
