package net.glowstone.net.event;

import com.flowpowered.network.Message;
import net.glowstone.net.GlowSession;
import org.bukkit.event.HandlerList;

/**
 * Part of unofficial packet api. That class can be removed in any next version.
 *
 * Event called when server send packet to player.
 * You have to enable packet event for each session by {@link GlowSession#enablePacketEvents()}
 */
public class PacketSendEvent extends PacketEvent {

    private static HandlerList handlerList = new HandlerList();

    public PacketSendEvent(GlowSession session, Message message) {
        super(session, message);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }
}
