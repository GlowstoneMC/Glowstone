package net.glowstone.mixin.event;

import net.glowstone.interfaces.IHandlerList;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Mixin(value = HandlerList.class, remap = false)
public abstract class MixinHandlerList implements IHandlerList {

    @Shadow
    private EnumMap<EventPriority, ArrayList<RegisteredListener>> handlerslots;

    @Override
    public List<RegisteredListener> getRegisteredListenersByPriority(EventPriority priority) {
        return handlerslots.get(priority);
    }
}
