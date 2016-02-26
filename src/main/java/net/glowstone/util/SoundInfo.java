package net.glowstone.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * A class that encapsulates relevant data for playing sounds (volume and pitch).
 */
public class SoundInfo {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    /**
     * Constructs a new GlowSound with the given sound and a volume and pitch of 1
     *
     * @param sound The Bukkit sound enum constant
     */
    public SoundInfo(Sound sound) {
        this(sound, 1F, 1F);
    }

    /**
     * Constructs a new GlowSound with the given sound, volume and pitch
     *
     * @param sound  The Bukkit sound enum constant
     * @param volume Volume of sound
     * @param pitch  Pitch of sound
     */
    public SoundInfo(Sound sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
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
     * @param player   Player to which to play the sound
     * @param location Location at which to play the sound
     */
    public void playTo(Player player, Location location) {
        player.playSound(location, sound, volume, pitch);
    }
}
