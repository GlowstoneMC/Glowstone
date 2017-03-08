package net.glowstone.net.event;

import com.flowpowered.network.Message;
import net.glowstone.net.GlowSession;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

/**
 * Part of unofficial packet api. That class can be removed in any next version.
 */
public abstract class PacketEvent extends Event implements Cancellable {

    private final GlowSession session;
    private Message message;

    private boolean cancelled;

    public PacketEvent(GlowSession session, Message message) {
        super(true);
        this.session = session;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public GlowSession getSession() {
        return session;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

