package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;

public class PacketReceiveEvent extends PacketEvent {

    private boolean ignored;

    public PacketReceiveEvent(Message packet, GlowSession session, GlowPlayer player) {
        super(packet, session, player, GlowPacket.Destination.IN);
    }

    /**
     * Whether the received packet should be ignored
     *
     * @return true if the packet should be ignored, false otherwise
     */
    public boolean isIgnored() {
        return ignored;
    }

    /**
     * Sets whether or not the packet should be ignored
     *
     * @param ignored true if the packet should be ignored, false otherwise
     */
    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }
}
