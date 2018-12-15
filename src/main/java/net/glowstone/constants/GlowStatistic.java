package net.glowstone.constants;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.bukkit.Material.GOLD_BLOCK;
import static org.bukkit.Material.GOLD_INGOT;
import static org.bukkit.Material.GOLD_NUGGET;
import static org.bukkit.Material.GOLD_ORE;
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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

/**
 * Name mappings for statistics.
 */
public final class GlowStatistic {

    private static final Map<Statistic, String> SIMPLE_STATISTIC = new HashMap<>(values().length);

    private static final Map<Statistic, Map<Material, String>> MATERIAL_STATISTIC = new HashMap<>();

    private static final Map<Statistic, Map<EntityType, String>> ENTITY_STATISTIC = new HashMap<>();

    static {
        generateSimpleStatistic();
        generateMaterialStatistic();
        generateEntityStatistic();
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
        checkNotNull(stat, "Statistic cannot be null");
        return SIMPLE_STATISTIC.get(stat);
    }

    /**
     * Get the statistic name for a specified Statistic and Material.
     *
     * @param stat the Statistic
     * @param material the Material
     * @return the statistic name
     */
    public static String getName(Statistic stat, Material material) {
        checkNotNull(stat, "Statistic cannot be null");
        checkNotNull(material, "Material cannot be null");

        if (MATERIAL_STATISTIC.containsKey(stat)) {
            return MATERIAL_STATISTIC.get(stat).get(material);
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
        checkNotNull(stat, "Statistic cannot be null");
        checkNotNull(entityType, "EntityType cannot be null");

        if (ENTITY_STATISTIC.containsKey(stat)) {
            return ENTITY_STATISTIC.get(stat).get(entityType);
        }

        return null;
    }

    private static <T extends Enum> void set(Map<Statistic, Map<T, String>> map,
                                             Statistic statistic, T data, String key) {
        if (!map.containsKey(statistic)) {
            map.put(statistic, new HashMap<>());
        }
        map.get(statistic).put(data, "stat." + key);
    }


    private static void generateSimpleStatistic() {
        for (Statistic stat : values()) {
            SIMPLE_STATISTIC.put(stat, "stat." + UPPER_UNDERSCORE.to(LOWER_CAMEL, stat.name()));
        }

        // Specific case
        SIMPLE_STATISTIC.put(PLAY_ONE_TICK, "stat.playOneMinute");
    }

    private static void generateMaterialStatistic() {
        for (Material material : Material.values()) {
            String name = material.name().toLowerCase()
                    .replace("spade", "shovel")
                    .replace("wood", "wooden");

            if (material != GOLD_INGOT
                    && material != GOLD_NUGGET
                    && material != GOLD_BLOCK
                    && material != GOLD_ORE) {
                name = name.replace("gold", "golden");
            }

            if (material.isBlock()) {
                set(MATERIAL_STATISTIC, MINE_BLOCK, material, "mineBlock.minecraft." + name);
            }

            if (material.isItem()) {
                set(MATERIAL_STATISTIC, USE_ITEM, material, "useItem.minecraft." + name);
                set(MATERIAL_STATISTIC, CRAFT_ITEM, material, "craftItem.minecraft." + name);

                if (material.getMaxDurability() != 0) {
                    set(MATERIAL_STATISTIC, BREAK_ITEM, material, "breakItem.minecraft." + name);
                }
            }

            set(MATERIAL_STATISTIC, DROP, material, "drop.minecraft." + name);
            set(MATERIAL_STATISTIC, PICKUP, material, "pickup.minecraft." + name);
        }
    }

    private static void generateEntityStatistic() {
        for (EntityType entityType : EntityType.values()) {
            Class<? extends Entity> entityClass = entityType.getEntityClass();

            if (entityClass == null) {
                continue;
            }

            if (Monster.class.isAssignableFrom(entityClass)) {
                set(ENTITY_STATISTIC, ENTITY_KILLED_BY, entityType, "entityKilledBy."
                        + UPPER_UNDERSCORE.to(UPPER_CAMEL, entityType.name()));
            }

            if (Creature.class.isAssignableFrom(entityClass)) {
                set(ENTITY_STATISTIC, KILL_ENTITY, entityType, "killEntity."
                        + UPPER_UNDERSCORE.to(UPPER_CAMEL, entityType.name()));
            }
        }
    }
}
