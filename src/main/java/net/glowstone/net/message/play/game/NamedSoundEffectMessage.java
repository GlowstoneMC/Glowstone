package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.Sound;

@Data
public final class NamedSoundEffectMessage implements Message {

    private final String sound;
    private final Sound.Category soundCategory;
    private final double x, y, z;
    private final float volume, pitch;
}
