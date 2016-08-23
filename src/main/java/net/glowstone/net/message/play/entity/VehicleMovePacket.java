package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class VehicleMovePacket implements Message {

    private final double x, y, z;
    private final float yaw, pitch;
}
