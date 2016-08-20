package net.glowstone.net.event;

import com.flowpowered.network.Message;

public class PacketReceiveEvent extends PacketEvent {
    public PacketReceiveEvent(Message packet) {
        super(packet);
    }
}
