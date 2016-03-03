package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class SoundEffectMessage implements Message {

    private final int sound;
    private final NamedSoundEffectMessage.SoundCategory category;
    private final double x, y, z;
    private final float volume, pitch;
}
