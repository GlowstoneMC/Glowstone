package net.glowstone.net.event;

import net.glowstone.net.GlowSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Part of unofficial packet api. That class can be removed in any next version.
 *
 * Event called when server create network session.
 */
public class SessionInitEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    private final GlowSession session;

    public SessionInitEvent(GlowSession session) {
        super(true);
        this.session = session;
    }

    public GlowSession getSession() {
        return session;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
