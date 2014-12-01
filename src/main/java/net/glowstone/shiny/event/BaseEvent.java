package net.glowstone.shiny.event;

import org.spongepowered.api.util.event.Event;
import org.spongepowered.api.util.event.callback.CallbackList;

/**
 * A base class for defining events.
 */
public class BaseEvent implements Event {

    private final CallbackList callbacks = new CallbackList();

    public BaseEvent() {}

    @Override
    public CallbackList getCallbacks() {
        return callbacks;
    }
}
