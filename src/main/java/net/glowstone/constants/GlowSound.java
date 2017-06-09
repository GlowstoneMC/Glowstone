package net.glowstone.constants;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public class GlowSound {

    private static Map<String, SoundCategory> sounds = new HashMap<>();

    static {
        // register vanilla sounds
        // as of 1.11, sounds do not have a default category
        // instead, the category of the sound playing is determined by the source of the sound
        for (Sound sound : Sound.values()) {
            reg(getVanillaId(sound), SoundCategory.MASTER);
        }
    }

    public static void reg(String id, SoundCategory category) {
        sounds.put(id, category);
    }

    public static SoundCategory getSoundCategory(String id) {
        return sounds.get(id);
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
}
