package net.glowstone.scoreboard;

public enum RenderType {
    /**
     * Displays scores as numbers.
     */
    INTEGER,
    /**
     * Displays scores as hearts, similar to hearts shown to a player to indicate their health.
     *
     * <p>Only valid for the {@link org.bukkit.scoreboard.DisplaySlot#PLAYER_LIST} display slot.
     */
    HEARTS
}
