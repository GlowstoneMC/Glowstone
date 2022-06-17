package net.glowstone.block;

import lombok.Data;
import org.bukkit.Sound;
import org.bukkit.SoundGroup;

@Data
public class GlowSoundGroup implements SoundGroup {
    public static final float DEFAULT_VOLUME = 1F;
    public static final float DEFAULT_PITCH = 0.75F;

    private float volume;
    private float pitch;
    private Sound breakSound;
    private Sound stepSound;
    private Sound placeSound;
    private Sound hitSound;
    private Sound fallSound;
}
