package net.glowstone.constants;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public class GlowSound {

    private static Map<String, SoundCategory> sounds = new HashMap<>();

    static {
        // register vanilla sounds
        for (Sound sound : Sound.values()) {
            if (sound.name().startsWith("AMBIENT")) {
                reg(getVanillaId(sound), SoundCategory.AMBIENT);
                continue;
            }
            if (sound.name().startsWith("BLOCK")) {
                reg(getVanillaId(sound), SoundCategory.BLOCKS);
                continue;
            }
            if (sound.name().startsWith("ENTITY_HOSTILE")) {
                reg(getVanillaId(sound), SoundCategory.HOSTILE);
                continue;
            }
            if (sound.name().startsWith("ENTITY_PLAYER")) {
                reg(getVanillaId(sound), SoundCategory.PLAYERS);
                continue;
            }
            if (sound.name().startsWith("ENTITY")) {
                reg(getVanillaId(sound), SoundCategory.NEUTRAL);
                continue;
            }
            if (sound.name().startsWith("MUSIC")) {
                reg(getVanillaId(sound), SoundCategory.MUSIC);
                continue;
            }
            if (sound.name().startsWith("RECORD")) {
                reg(getVanillaId(sound), SoundCategory.RECORDS);
                continue;
            }
            if (sound.name().startsWith("WEATHER")) {
                reg(getVanillaId(sound), SoundCategory.WEATHER);
                continue;
            }
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
