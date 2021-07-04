package net.glowstone.util;

import com.google.common.collect.Sets;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A collection of utility methods to play sounds.
 */
public class SoundUtil {

    /**
     * Plays a sound with a random pitch, but excludes specified players from hearing it.
     *
     * @param location the sound location
     * @param sound    the sound to play
     * @param volume   the volume multiplier
     * @param pitch    the pitch modifier
     * @param exclude  the players not to play the sound for
     * @throws NullPointerException if any of the location, sound, or exclude parameters is null.
     */
    public static void playSoundAtLocationExcept(Location location, Sound sound, float volume,
            float pitch, GlowPlayer... exclude) {
        checkNotNull(location);
        checkNotNull(sound);
        checkNotNull(exclude);

        Set<GlowPlayer> excludeSet = Sets.newHashSet(exclude);
        GlowWorld world = (GlowWorld) location.getWorld();
        double radiusSquared = volume * volume * 256;
        world.getRawPlayers()
                .stream()
                .filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared
                        && !excludeSet.contains(player))
                .forEach(player -> player.playSound(location, sound, volume, pitch));
    }

    /**
     * Plays a sound with a random pitch, but excludes specified players from hearing it.
     *
     * @param location      the sound location
     * @param sound         the sound to play
     * @param volume        the volume multiplier
     * @param pitchBase     if {@code allowNegative}, the average pitch modifier; otherwise, the
     *                      minimum
     * @param pitchRange    the maximum deviation of the pitch modifier compared to {@code
     * pitchBase}
     * @param allowNegative if true, distribution is triangular rather than uniform
     * @param exclude       the players not to play the sound for
     */
    public static void playSoundPitchRange(Location location, Sound sound, float volume,
            float pitchBase, float pitchRange, boolean allowNegative, GlowPlayer... exclude) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        float pitch = pitchBase;
        if (allowNegative) {
            pitch += randomReal(pitchRange);
        } else {
            pitch += rand.nextFloat() * pitchRange;
        }
        playSoundAtLocationExcept(location, sound, volume, pitch, exclude);
    }

    /**
     * Plays a sound with a random pitch, but excludes specified players from hearing it.
     *
     * @param location   the sound location
     * @param sound      the sound to play
     * @param volume     the volume multiplier
     * @param pitchBase  the average pitch modifier.
     * @param pitchRange the maximum deviation of the pitch modifier compared to {@code pitchBase}
     * @param exclude    the players not to play the sound for
     */
    public static void playSoundPitchRange(Location location, Sound sound, float volume,
            float pitchBase, float pitchRange, GlowPlayer... exclude) {
        playSoundPitchRange(location, sound, volume, pitchBase, pitchRange, true, exclude);
    }

    /**
     * Generates a random float between {@code -range} and {@code range}.
     *
     * @param range the bounds of the random float.
     * @return A randomly generated float.
     */
    public static float randomReal(float range) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        return (2.0F * rand.nextFloat() - 1.0F) * range;
    }

    /**
     * Convert a string to a SoundCategory. The comparison is done on the name and is not
     * case-sensitive.
     *
     * @param category The string name of the category
     * @return The matching SoundCategory, null if none.
     */
    public static SoundCategory buildSoundCategory(final String category) {
        if (category == null) {
            return null;
        }
        for (final SoundCategory soundCategory : SoundCategory.values()) {
            if (category.equalsIgnoreCase(soundCategory.name())) {
                return soundCategory;
            }
        }
        return null;
    }
}
