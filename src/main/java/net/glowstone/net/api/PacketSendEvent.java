package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import org.bukkit.event.Cancellable;

public class PacketSendEvent extends PacketEvent implements Cancellable {

    private boolean cancelled;

    public PacketSendEvent(Message packet, GlowSession session, GlowPlayer player) {
        super(packet, session, player, GlowPacket.Destination.OUT);
    }

    /**
     * Whether or not the packet is cancelled
     *
     * @return true if the packet is cancelled, false otherwise
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether or not the packet should be cancelled.
     * Cancelling a packet will prevent it from being sent to the destination
     *
     * @param cancelled true if the packet is cancelled, false otherwise
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
