package net.glowstone.net.event;

import com.flowpowered.network.Message;
import org.bukkit.event.Cancellable;

public class PacketSendEvent extends PacketEvent implements Cancellable {

    private boolean cancelled;

    public PacketSendEvent(Message packet) {
        super(packet);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
