package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class BeaconEffectMessage implements Message {

    private final int primary;
    private final int secondary;
}
