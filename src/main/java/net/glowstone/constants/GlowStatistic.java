package net.glowstone.constants;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Statistic.BREAK_ITEM;
import static org.bukkit.Statistic.CRAFT_ITEM;
import static org.bukkit.Statistic.DROP;
import static org.bukkit.Statistic.ENTITY_KILLED_BY;
import static org.bukkit.Statistic.KILL_ENTITY;
import static org.bukkit.Statistic.MINE_BLOCK;
import static org.bukkit.Statistic.PICKUP;
import static org.bukkit.Statistic.PLAY_ONE_TICK;
import static org.bukkit.Statistic.USE_ITEM;
import static org.bukkit.Statistic.values;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.jetbrains.annotations.NonNls;

/**
 * Name mappings for statistics.
 */
public final class GlowStatistic {
    @NonNls
    private static final Map<Statistic, String> STATISTICS = new HashMap<>(values().length);
    @NonNls
    private static final Map<Statistic, Map<Enum, String>> SUB_STATISTICS = new EnumMap<>(Statistic.class);
    @NonNls
    private static final String STAT_PREFIX = "stat.";

    static {
        generateStatistics();
        generateMaterialStatistics();
        generateEntityStatistics();
    }

    private GlowStatistic() {
    }

    /**
     * Get the statistic name for a specified Statistic.
     *
     * @param stat the Statistic.
     * @return the statistic name.
     */
    public static String getName(Statistic stat) {
        checkNotNull(stat, "Statistic cannot be null"); // NON-NLS
        return STATISTICS.get(stat);
    }

    /**
     * Get the statistic name for a specified Statistic and Material.
     *
     * @param stat the Statistic
     * @param material the Material
     * @return the statistic name
     */
    public static String getName(Statistic stat, Material material) {
        checkNotNull(stat, "Statistic cannot be null"); // NON-NLS
        checkNotNull(material, "Material cannot be null"); // NON-NLS

        if (SUB_STATISTICS.containsKey(stat)) {
            return SUB_STATISTICS.get(stat).get(material);
        }

        return null;
    }

    /**
     * Get the statistic name for a specified Statistic and EntityType.
     *
     * @param stat the Statistic
     * @param entityType the EntityType
     * @return the statistic name
     */
    public static String getName(Statistic stat, EntityType entityType) {
        checkNotNull(stat, "Statistic cannot be null"); // NON-NLS
        checkNotNull(entityType, "EntityType cannot be null"); // NON-NLS

        if (SUB_STATISTICS.containsKey(stat)) {
            return SUB_STATISTICS.get(stat).get(entityType);
        }

        return null;
    }

    private static void set(Statistic statistic, Enum data, @NonNls String key) {
        if (!SUB_STATISTICS.containsKey(statistic)) {
            SUB_STATISTICS.put(statistic, new HashMap<>());
        }
        SUB_STATISTICS.get(statistic).put(data, STAT_PREFIX + key);
    }

    private static void generateStatistics() {
        for (Statistic stat : values()) {
            STATISTICS.put(stat, STAT_PREFIX + UPPER_UNDERSCORE.to(LOWER_CAMEL, stat.name()));
        }

        // Specific case
        STATISTICS.put(PLAY_ONE_TICK, STAT_PREFIX + "playOneMinute");
    }

    private static void generateMaterialStatistics() {
        for (Material material : Material.values()) {
            String name = ItemIds.getKeyName(material);

            if (material.isBlock()) {
                set(MINE_BLOCK, material, "mineBlock.minecraft." + name);
            }

            if (material.isItem()) {
                set(USE_ITEM, material, "useItem.minecraft." + name);
                set(CRAFT_ITEM, material, "craftItem.minecraft." + name);

                if (material.getMaxDurability() != 0) {
                    set(BREAK_ITEM, material, "breakItem.minecraft." + name);
                }
            }

            set(DROP, material, "drop.minecraft." + name);
            set(PICKUP, material, "pickup.minecraft." + name);
        }
    }

    private static void generateEntityStatistics() {
        for (EntityType entityType : EntityType.values()) {
            Class<? extends Entity> entityClass = entityType.getEntityClass();

            if (entityClass == null) {
                continue;
            }

            if (Monster.class.isAssignableFrom(entityClass)) {
                set(ENTITY_KILLED_BY, entityType, "entityKilledBy."
                        + entityClass.getSimpleName());
            }

            if (Creature.class.isAssignableFrom(entityClass)) {
                set(KILL_ENTITY, entityType, "killEntity."
                        + entityClass.getSimpleName());
            }
        }
    }
}
