package net.glowstone.constants;

import org.apache.commons.lang3.Validate;
import org.bukkit.Sound;

import static org.bukkit.Sound.*;

/**
 * Mappings from the Sound enum to individual sound names.
 */
public final class GlowSound {

    private static final String[] names = new String[values().length];

    static {
        for (Sound sound : values()) {
            set(sound, sound.name().toLowerCase().replaceAll("\\.", ""));
            System.out.println(sound.name().toLowerCase().replaceAll("\\.", ""));
        }
    }

    private GlowSound() {
    }

    /**
     * Get the sound name for a specified Sound.
     *
     * @param sound the Sound.
     * @return the sound name.
     */
    public static String getName(Sound sound) {
        Validate.notNull(sound, "Sound cannot be null");
        return names[sound.ordinal()];
    }

    private static void set(Sound sound, String key) {
        names[sound.ordinal()] = key;
    }

}
