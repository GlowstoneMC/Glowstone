package net.glowstone.scoreboard;

import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Scoreboard objective and associated data.
 */
public final class GlowObjective implements Objective {

    private final String name;
    private final String criteria;
    private final HashMap<String, GlowScore> scores = new HashMap<>();
    DisplaySlot displaySlot;
    private GlowScoreboard scoreboard;
    private String displayName;
    private RenderType renderType;

    public GlowObjective(GlowScoreboard scoreboard, String name, String criteria) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.criteria = criteria;
        renderType = RenderType.INTEGER;
        displayName = name;
    }

    public GlowScoreboard getScoreboard() {
        return scoreboard;
    }

    public void unregister() throws IllegalStateException {
        checkValid();
        for (Entry<String, GlowScore> entry : scores.entrySet()) {
            scoreboard.getScoresForName(entry.getKey()).remove(entry.getValue());
        }
        scoreboard.removeObjective(this);
        scoreboard = null;
    }

    void checkValid() {
        if (scoreboard == null) {
            throw new IllegalStateException("Cannot manipulate unregistered objective");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Properties

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
        checkNotNull(displayName, "displayName cannot be null");
        checkArgument(displayName.length() <= 32, "displayName cannot be longer than 32 characters");

        this.displayName = displayName;
        scoreboard.broadcast(ScoreboardObjectiveMessage.update(name, displayName, renderType));
    }

    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        checkValid();
        return displaySlot;
    }

    public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {
        checkValid();
        if (slot != displaySlot) {
            if (displaySlot != null) {
                scoreboard.setDisplaySlot(displaySlot, null);
            }
            if (slot != null) {
                scoreboard.setDisplaySlot(slot, this);
            }
        }
    }

    public RenderType getType() throws IllegalStateException {
        checkValid();
        return renderType;
    }

    public void setType(RenderType renderType) throws IllegalStateException {
        checkValid();
        checkNotNull(renderType, "RenderType cannot be null");
        this.renderType = renderType;
        scoreboard.broadcast(ScoreboardObjectiveMessage.update(name, displayName, renderType));
    }

    public boolean isModifiable() throws IllegalStateException {
        checkValid();
        return !criteria.equalsIgnoreCase(Criterias.HEALTH);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Score management

    public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
        checkNotNull(entry, "Entry cannot be null");
        checkValid();

        GlowScore score = scores.get(entry);
        if (score == null) {
            score = new GlowScore(this, entry);
            scores.put(entry, score);
            scoreboard.getScoresForName(entry).add(score);
        }
        return score;
    }

    /**
     * Deletes a score directly.
     *
     * @param entry The key to delete.
     */
    void deleteScore(String entry) {
        scores.remove(entry);
    }

    @Deprecated
    public Score getScore(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
        checkNotNull(player, "Player cannot be null");
        return getScore(player.getName());
    }

    public void setRenderType(String renderType) {
        // TODO
    }

    public boolean hasScore(String entry) throws IllegalArgumentException, IllegalStateException {
        checkNotNull(entry, "Entry cannot be null");
        checkValid();

        return scores.containsKey(entry);
    }

    public Set<String> getEntries() throws IllegalStateException {
        return scores.keySet();
    }
}
