package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.SoundCategory;

@Data
public class SoundEffectMessage implements Message {

    private final SoundCategory category;
    private final int sound;
    private final String parrot;
    private final double x, y, z;
    private final float volume, pitch;
}
