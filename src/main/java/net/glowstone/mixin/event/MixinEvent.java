package net.glowstone.mixin.event;

import net.glowstone.interfaces.event.IEvent;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = org.bukkit.event.Event.class, remap = false)
public class MixinEvent implements Event, IEvent {

    private Cause cause;

    @Override
    public void setCause(Cause cause) {
        this.cause = cause;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
