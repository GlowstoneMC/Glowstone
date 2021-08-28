package net.glowstone.scoreboard;

import lombok.Getter;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import net.glowstone.util.TextMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
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
    @Getter
    private GlowScoreboard scoreboard;
    private String displayName;
    private RenderType renderType;

    /**
     * Creates a scoreboard objective.
     *
     * @param scoreboard the scoreboard to add to
     * @param name the name of the objective
     * @param criteria one of the constants from {@link Criterias}, or anything else if this score
     *         is only modified by commands and/or plugins.
     */
    public GlowObjective(GlowScoreboard scoreboard, String name, String criteria) {
        this.scoreboard = scoreboard;
        this.name = name;
        this.criteria = criteria;
        renderType = RenderType.INTEGER;
        displayName = name;
    }

    /**
     * Removes this objective from the scoreboard.
     *
     * @throws IllegalStateException if this objective already isn't registered with a scoreboard
     */
    @Override
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

    @Override
    public String getName() throws IllegalStateException {
        checkValid();
        return name;
    }

    @Override
    public @NotNull Component displayName() throws IllegalStateException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public void displayName(@Nullable Component component) throws IllegalStateException, IllegalArgumentException {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public String getCriteria() throws IllegalStateException {
        checkValid();
        return criteria;
    }

    @Override
    public String getDisplayName() throws IllegalStateException {
        checkValid();
        return displayName;
    }

    /**
     * Sets the display name.
     *
     * @param displayName the new display name, up to 32 characters long
     * @throws IllegalArgumentException if {@code displayName} is null or longer than 32 characters
     * @throws IllegalStateException if this objective isn't registered with a scoreboard
     */
    @Override
    public void setDisplayName(String displayName)
        throws IllegalStateException, IllegalArgumentException {
        checkValid();
        checkNotNull(displayName, "displayName cannot be null");
        checkArgument(displayName.length() <= 32,
            "displayName cannot be longer than 32 characters");

        this.displayName = displayName;
        scoreboard.broadcast(
                ScoreboardObjectiveMessage.update(name, new TextMessage(displayName), renderType));
    }

    @Override
    public DisplaySlot getDisplaySlot() throws IllegalStateException {
        checkValid();
        return displaySlot;
    }

    @Override
    public org.bukkit.scoreboard.@NotNull RenderType getRenderType() throws IllegalStateException {
        return renderType == RenderType.HEARTS
                ? org.bukkit.scoreboard.RenderType.HEARTS
                : org.bukkit.scoreboard.RenderType.INTEGER;
    }

    @Override
    public void setRenderType(org.bukkit.scoreboard.@NotNull RenderType renderType)
            throws IllegalStateException {
        this.renderType = (renderType == org.bukkit.scoreboard.RenderType.HEARTS
                ? RenderType.HEARTS : RenderType.INTEGER);
    }

    public void setRenderType(String renderType) {
        setRenderType(org.bukkit.scoreboard.RenderType.valueOf(renderType.toUpperCase(Locale.ROOT)));
    }

    /**
     * Sets the {@link DisplaySlot} where this objective displays.
     *
     * @param slot the DisplaySlot, or null to hide the objective
     * @throws IllegalStateException if this objective isn't registered with a scoreboard
     */
    @Override
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

    /**
     * Sets the {@link RenderType} for this objective.
     *
     * @param renderType the new render type
     * @throws IllegalArgumentException if {@code renderType} is null
     * @throws IllegalStateException if this objective isn't registered with a scoreboard
     */
    public void setType(RenderType renderType) throws IllegalStateException {
        checkValid();
        checkNotNull(renderType, "RenderType cannot be null");
        this.renderType = renderType;
        scoreboard.broadcast(
                ScoreboardObjectiveMessage.update(name, new TextMessage(displayName), renderType));
    }

    @Override
    public boolean isModifiable() throws IllegalStateException {
        checkValid();
        return !criteria.equalsIgnoreCase(Criterias.HEALTH);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Score management

    /**
     * Returns a score, creating it if necessary.
     *
     * @param entry the key (e.g. player name or team name)
     * @return the score for {@code entry}
     * @throws IllegalArgumentException if {@code entry} is null
     * @throws IllegalStateException if this objective isn't registered with a scoreboard
     */
    @Override
    public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
        checkNotNull(entry, "Entry cannot be null");
        checkValid();

        return scores.computeIfAbsent(entry, entryCopy -> {
            GlowScore score = new GlowScore(this, entryCopy);
            scores.put(entryCopy, score);
            scoreboard.getScoresForName(entryCopy).add(score);
            return score;
        });
    }

    @Override
    @Deprecated
    public Score getScore(OfflinePlayer player)
        throws IllegalArgumentException, IllegalStateException {
        checkNotNull(player, "Player cannot be null");
        return getScore(player.getName());
    }

    /**
     * Deletes a score directly.
     *
     * @param entry The key to delete.
     */
    void deleteScore(String entry) {
        scores.remove(entry);
    }

    /**
     * Returns whether a score is defined.
     *
     * @param entry the key (e.g. player name or team name)
     * @return true if the score exists; false otherwise
     * @throws IllegalArgumentException if {@code entry} is null
     * @throws IllegalStateException if this objective isn't registered with a scoreboard
     */
    public boolean hasScore(String entry) throws IllegalArgumentException, IllegalStateException {
        checkNotNull(entry, "Entry cannot be null");
        checkValid();

        return scores.containsKey(entry);
    }

    public Set<String> getEntries() throws IllegalStateException {
        return scores.keySet();
    }
}
