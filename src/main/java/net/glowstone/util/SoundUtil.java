package net.glowstone.util;

import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SoundUtil {

    public static Map<String, SoundCategory> sounds = new HashMap<>();

    static {
        // TODO: categories
        for (Sound sound : Sound.values()) {
            reg("minecraft:" + sound.name().toLowerCase().replaceAll("_", ""), SoundCategory.MASTER);
        }
    }

    public static void reg(String id, SoundCategory category) {
        sounds.put(id, category);
    }

    public static void getCategory(String id) {
        sounds.get(id);
    }

    public static String getVanillaId(Sound sound) {
        return "minecraft:" + sound.name().toLowerCase().replaceAll("_", "");
    }

    public static Sound getVanillaSound(String id) {
        if (id.startsWith("minecraft:")) {
        for (Sound sound : Sound.values()) {
            if (("minecraft:" + sound.name().toLowerCase().replaceAll("_", "").equals(id)) {
                return sound;
            }
        }
        }
        return null;
    }

    public static SoundCategory getCategory(int category) {
        return SoundCategory.values()[category];
    }

    public static void playSoundAtLocationExcept(Location location, Sound sound, float volume, float pitch, GlowPlayer... exclude) {
        if (location == null || sound == null) return;
        GlowWorld world = (GlowWorld) location.getWorld();
        double radiusSquared = volume * volume * 256;
        world.getRawPlayers().parallelStream().filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared).filter(player -> !Arrays.asList(exclude).contains(player)).forEach(player -> player.playSound(location, sound, volume, pitch));
    }

    public static void playSoundPitchRange(Location location, Sound sound, float volume, float pitchBase, float pitchRange, boolean allowNegative, GlowPlayer... exclude) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        float pitch = pitchBase;
        if (allowNegative) {
            pitch += randomReal(pitchRange);
        } else {
            pitch += rand.nextFloat() * pitchRange;
        }
        playSoundAtLocationExcept(location, sound, volume, pitch, exclude);
    }

    public static void playSoundPitchRange(Location location, Sound sound, float volume, float pitchBase, float pitchRange, GlowPlayer... exclude) {
        playSoundPitchRange(location, sound, volume, pitchBase, pitchRange, true, exclude);
    }

    public static float randomReal(float range) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return (rand.nextFloat() - rand.nextFloat()) * range;
    }
}
