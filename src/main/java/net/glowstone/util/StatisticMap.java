package net.glowstone.util;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import net.glowstone.constants.GlowStatistic;
import net.glowstone.net.message.play.game.StatisticsMessage;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Statistic.Type;
import org.bukkit.entity.EntityType;

/**
 * A container for statistic tracking.
 */
public final class StatisticMap {

    private final Map<String, Integer> values = new HashMap<>();

    public StatisticsMessage toMessage() {
        Map<Statistic, Integer> statKeyedValues = new EnumMap<>(Statistic.class);
        for (Statistic stat : Statistic.values()) {
            Integer value = values.get(stat.name());
            if (value != null) {
                statKeyedValues.put(stat, value);
            }
        }
        return new StatisticsMessage(statKeyedValues);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Helpers

    private int getValue(String key) {
        return values.getOrDefault(key, 0);
    }

    private void setValue(String key, int value) {
        values.put(key, Math.max(value, 0));
    }

    private String name(Statistic stat) {
        return GlowStatistic.getName(stat);
    }

    private String name(Statistic stat, Material mat) {
        if (mat.isBlock()) {
            checkArgument(
                stat.getType() == Type.BLOCK,
                "Statistic " + stat + " is not a block statistic");
        } else {
            checkArgument(
                stat.getType() == Type.ITEM, "Statistic " + stat + " is not an item statistic");
        }
        return GlowStatistic.getName(stat, mat);
    }

    private String name(Statistic stat, EntityType type) {
        checkArgument(
            stat.getType() == Type.ENTITY, "Statistic " + stat + " is not an entity statistic");
        return GlowStatistic.getName(stat, type);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Stat accessors

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
        String name = name(stat);
        setValue(name, getValue(name) + modify);
    }

    /**
     * Increment the given Statistic for the given Material.
     *
     * @param stat     the Statistic
     * @param material the Material
     * @param modify   the amount to add
     */
    public void add(Statistic stat, Material material, int modify) {
        String name = name(stat);

        if (name != null) {
            setValue(name, getValue(name) + modify);
        }

        name = name(stat, material);
        setValue(name, getValue(name) + modify);
    }

    /**
     * Increment the given Statistic for the given EntityType.
     *
     * @param stat       the Statistic
     * @param entityType the EntityType
     * @param modify     the amount to add
     */
    public void add(Statistic stat, EntityType entityType, int modify) {
        String name = name(stat);

        if (name != null) {
            setValue(name, getValue(name) + modify);
        }

        name = name(stat, entityType);
        setValue(name, getValue(name) + modify);
    }

    public Map<String, Integer> getValues() {
        // TODO: Replace with facade
        return values;
    }
}
