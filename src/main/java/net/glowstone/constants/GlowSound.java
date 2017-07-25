package net.glowstone.constants;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public class GlowSound {

    private static final Map<String, SoundCategory> SOUNDS = new HashMap<>();

    static {
        // register vanilla sounds
        // as of 1.11, sounds do not have a default category
        // instead, the category of the sound playing is determined by the source of the sound
        for (Sound sound : Sound.values()) {
            reg(getVanillaId(sound), SoundCategory.MASTER);
        }
    }

    public static void reg(String id, SoundCategory category) {
        SOUNDS.put(id, category);
    }

    public static SoundCategory getSoundCategory(String id) {
        return SOUNDS.get(id);
    }

    public static String getVanillaId(Sound sound) {
        return "minecraft:" + sound.name().toLowerCase().replaceAll("_", ".");
    }

    public static Sound getVanillaSound(String id) {
        if (id.startsWith("minecraft:")) {
            for (Sound sound : Sound.values()) {
                if (getVanillaId(sound).equals(id)) {
                    return sound;
                }
            }
        }
        return null;
    }

    public static SoundCategory getCategory(int category) {
        return SoundCategory.values()[category];
    }

    public static Map<String, SoundCategory> getSounds() {
        return (Map) ImmutableMap.builder().putAll(SOUNDS).build();
    }
}
