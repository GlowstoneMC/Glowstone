package net.glowstone.scoreboard;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Scoreboard objective and associated data.
 */
public final class GlowObjective implements Objective {

    private GlowScoreboard scoreboard;
    private final String name;
    private final String criteria;

    private String displayName;
    public DisplaySlot displaySlot;

    public GlowObjective(GlowScoreboard scoreboard, String name, String criteria) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.criteria = criteria;
        displayName = name;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public String getName() throws IllegalStateException {
        checkValid();
        return name;
    }

    public String getCriteria() throws IllegalStateException {
        checkValid();
        return criteria;
    }

    public String getDisplayName() throws IllegalStateException {
        checkValid();
        return displayName;
    }

    public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
        checkValid();
        Validate.notNull(displayName, "displayName cannot be null");
        Validate.isTrue(displayName.length() <= 32, "displayName cannot be longer than 32 characters");

        this.displayName = displayName;
    }

    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        checkValid();
        return displaySlot;
    }

    public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {
        checkValid();
        scoreboard.setDisplaySlot(slot, this);
    }

    public boolean isModifiable() throws IllegalStateException {
        checkValid();
        return false;
    }

    public void unregister() throws IllegalStateException {
        checkValid();
        scoreboard.remove(this);
        scoreboard = null;
    }

    public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
        checkValid();
        // todo
        return null;
    }

    @Deprecated
    public Score getScore(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        return getScore(player.getName());
    }

    private void checkValid() {
        if (scoreboard == null) {
            throw new IllegalStateException("Cannot manipulate unregistered objective");
        }
    }
}
