package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.SoundCategory;

@Data
public class SoundEffectMessage implements Message {

    private final int sound;
    private final SoundCategory category;
    private final double x;
    private final double y;
    private final double z;
    private final float volume;
    private final float pitch;
}
