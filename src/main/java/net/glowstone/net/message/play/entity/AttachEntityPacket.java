package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class AttachEntityPacket implements Message {

    private final int id, vehicle;
    private final boolean leash;

}
