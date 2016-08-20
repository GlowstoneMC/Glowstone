package net.glowstone.net.event;

import com.flowpowered.network.Message;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PacketEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Message packet;

    public PacketEvent(Message packet) {
        this.packet = packet;
    }

    public Message getPacket() {
        return packet;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
