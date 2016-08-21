package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PacketEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Message packet;
    private final GlowSession session;
    private final GlowPlayer player;
    private final PacketType packetType;

    public PacketEvent(Message packet, GlowSession session, GlowPlayer player, PacketType.Destination destination) {
        this.packet = packet;
        this.session = session;
        this.player = player;
        this.packetType = PacketType.getType(packet.getClass(), destination);
    }

    public Message getPacket() {
        return packet;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public GlowSession getSession() {
        return session;
    }

    public GlowPlayer getPlayer() {
        return player;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
