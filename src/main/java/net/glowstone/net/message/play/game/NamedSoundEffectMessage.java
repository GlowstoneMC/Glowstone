package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.SoundCategory;

@Data
public final class NamedSoundEffectMessage implements Message {

    private final String sound;
    private final SoundCategory soundCategory;
    private final double x;
    private final double y;
    private final double z;
    private final float volume;
    private final float pitch;
}
