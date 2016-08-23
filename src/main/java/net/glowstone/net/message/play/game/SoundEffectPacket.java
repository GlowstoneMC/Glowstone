package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.Sound;

@Data
public class SoundEffectPacket implements Message {

    private final int sound;
    private final Sound.Category category;
    private final double x, y, z;
    private final float volume, pitch;
}
