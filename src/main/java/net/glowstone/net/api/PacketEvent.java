package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PacketEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Message packetObject;
    private final GlowSession session;
    private final GlowPlayer player;
    private final GlowPacket packet;

    public PacketEvent(Message packetObject, GlowSession session, GlowPlayer player, GlowPacket.Destination destination) {
        this.packetObject = packetObject;
        this.session = session;
        this.player = player;
        this.packet = GlowPacket.getPacket(packetObject.getClass(), destination);
    }

    public Message getPacketObject() {
        return packetObject;
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

    public GlowPacket getPacket() {
        return packet;
    }

    public PacketDeconstructor deconstruct() {
        return new PacketDeconstructor(packet, packetObject);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
