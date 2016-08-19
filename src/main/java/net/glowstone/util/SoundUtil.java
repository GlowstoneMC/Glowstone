package net.glowstone.util;

import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class SoundUtil {

    private static final ThreadLocalRandom rand = ThreadLocalRandom.current();

    public static void playSoundAtLocationExcept(Location location, Sound sound, float volume, float pitch, GlowPlayer... exclude) {
        if (location == null || sound == null) return;
        GlowWorld world = (GlowWorld) location.getWorld();
        double radiusSquared = Math.pow(volume * 16, 2);
        world.getRawPlayers().stream().filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared).filter(player -> !Arrays.asList(exclude).contains(player)).forEach(player -> player.playSound(location, sound, volume, pitch));
    }

    public static void playSoundPitchRange(Location location, Sound sound, float volume, float pitchBase, float pitchRange, boolean allowNegative, GlowPlayer... exclude) {
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
        return (rand.nextFloat() - rand.nextFloat()) * range;
    }

}
