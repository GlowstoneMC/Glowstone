package net.glowstone.net.message.play.inv;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class SetBeaconEffectMessage implements Message {
    private final int primaryEffect;
    private final int secondaryEffect;
}
