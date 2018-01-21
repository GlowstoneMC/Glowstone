package net.glowstone.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * A class that encapsulates relevant data for playing sounds (volume and pitch).
 */
@Data
@RequiredArgsConstructor
public class SoundInfo {

    /** The Bukkit sound enum constant. */
    private final Sound sound;
    /** The volume. */
    private final float volume;
    /** The pitch multiplier. */
    private final float pitch;

    /**
     * Constructs a new GlowSound with the given sound and a volume and pitch of 1.
     *
     * @param sound The Bukkit sound enum constant
     */
    public SoundInfo(Sound sound) {
        this(sound, 1F, 1F);
    }

    /**
     * Plays the sound to all players at the given location.
     *
     * @param location Location at which to play the sound
     */
    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    /**
     * Plays the sound to the given player at the given location.
     *
     * @param player Player to which to play the sound
     * @param location Location at which to play the sound
     */
    public void playTo(Player player, Location location) {
        player.playSound(location, sound, volume, pitch);
    }
}
