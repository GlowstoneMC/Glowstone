package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SteerVehicleMessage implements Message {

    private final float sideways;
    private final float forward;
    private final boolean jump;
    private final boolean unmount;

}

