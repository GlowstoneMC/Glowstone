package net.glowstone.interfaces;

import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;

import java.util.List;

public interface IHandlerList {

    List<RegisteredListener> getRegisteredListenersByPriority(EventPriority priority);
}
