package net.glowstone.constants;

import org.bukkit.Sound;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Sound.values;

/**
 * Mappings from the Sound enum to individual sound names.
 */
public final class GlowSound {

    private static final String[] names = new String[values().length];
    private static final Pattern SOUND_ENUM_PATTERN = Pattern.compile("\\.");

    static {
        for (Sound sound : values()) {
            set(sound, SOUND_ENUM_PATTERN.matcher(sound.name().toLowerCase()).replaceAll(""));
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
        checkNotNull(sound, "Sound cannot be null");
        return names[sound.ordinal()];
    }

    private static void set(Sound sound, String key) {
        names[sound.ordinal()] = key;
    }

}
