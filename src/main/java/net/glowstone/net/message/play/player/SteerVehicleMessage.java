package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SteerVehicleMessage implements Message {

    private final float sideways, forward;
    private final boolean jump, unmount;

}

