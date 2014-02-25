package net.glowstone.util;

import net.glowstone.constants.GlowAchievement;
import net.glowstone.constants.GlowStatistic;
import net.glowstone.net.message.play.game.StatisticMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

/**
 * A container for achievement and statistic tracking.
 */
public final class StatisticMap {

    private final Map<String, Integer> values = new HashMap<>();

    public StatisticMap() {

    }

    public StatisticMessage toMessage() {
        return new StatisticMessage(values);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helpers

    private int getValue(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        } else {
            return 0;
        }
    }

    private void setValue(String key, int value) {
        values.put(key, value < 0 ? 0 : value);
    }

    private String name(Statistic stat) {
        return GlowStatistic.getName(stat);
    }

    private String name(Statistic stat, Material mat) {
        if (mat.isBlock()) {
            Validate.isTrue(stat.getType() == Statistic.Type.BLOCK, "Statistic " + stat + " is not a block statistic");
        } else {
            Validate.isTrue(stat.getType() == Statistic.Type.ITEM, "Statistic " + stat + " is not an item statistic");
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private String name(Statistic stat, EntityType type) {
        Validate.isTrue(stat.getType() == Statistic.Type.ENTITY, "Statistic " + stat + " is not an entity statistic");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Achievement and stat accessors

    public boolean hasAchievement(Achievement achievement) {
        return getValue(GlowAchievement.getName(achievement)) != 0;
    }

    public void setAchievement(Achievement achievement, boolean has) {
        setValue(GlowAchievement.getName(achievement), has ? 1 : 0);
    }

    public int get(Statistic stat) {
        return getValue(name(stat));
    }

    public int get(Statistic stat, Material material) {
        return getValue(name(stat, material));
    }

    public int get(Statistic stat, EntityType entityType) {
        return getValue(name(stat, entityType));
    }

    public void set(Statistic stat, int value) {
        setValue(name(stat), value);
    }

    public void set(Statistic stat, Material material, int value) {
        setValue(name(stat, material), value);
    }

    public void set(Statistic stat, EntityType entityType, int value) {
        setValue(name(stat, entityType), value);
    }

    public void add(Statistic stat, int modify) {
        final String name = name(stat);
        setValue(name, getValue(name) + modify);
    }

    public void add(Statistic stat, Material material, int modify) {
        final String name = name(stat, material);
        setValue(name, getValue(name) + modify);
    }

    public void add(Statistic stat, EntityType entityType, int modify) {
        final String name = name(stat, entityType);
        setValue(name, getValue(name) + modify);
    }
}
