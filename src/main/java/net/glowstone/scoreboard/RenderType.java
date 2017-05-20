package net.glowstone.scoreboard;

import org.bukkit.scoreboard.DisplaySlot;

public enum RenderType {
    /**
     * Displays scores as numbers.
     */
    INTEGER,
    /**
     * Displays scores as hearts, similar to hearts shown to a player to indicate their health.
     * Only valid for the {@link DisplaySlot#PLAYER_LIST} display slot.
     */
    HEARTS
}
