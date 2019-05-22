package net.glowstone.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.util.WeightedRandom.Choice;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * Definitions of enchantment types.
 */
public final class GlowEnchantment extends Enchantment implements Choice {

    private static final List<String> VANILLA_IDS = new ArrayList<>();
    private static final HashMap<String, Enchantment> BY_VANILLA_ID = new HashMap<>();

    private static final MaterialMatcher SWORD_OR_AXE = item ->
        EnchantmentTarget.WEAPON.includes(item)
            || item.equals(Material.WOODEN_AXE)
            || item.equals(Material.STONE_AXE)
            || item.equals(Material.IRON_AXE)
            || item.equals(Material.DIAMOND_AXE)
            || item.equals(Material.GOLDEN_AXE);
    private static final MaterialMatcher BASE_TOOLS = item -> item.equals(Material.WOODEN_SHOVEL)
        || item.equals(Material.STONE_SHOVEL)
        || item.equals(Material.IRON_SHOVEL)
        || item.equals(Material.DIAMOND_SHOVEL)
        || item.equals(Material.GOLDEN_SHOVEL)
        || item.equals(Material.WOODEN_PICKAXE)
        || item.equals(Material.STONE_PICKAXE)
        || item.equals(Material.IRON_PICKAXE)
        || item.equals(Material.DIAMOND_PICKAXE)
        || item.equals(Material.GOLDEN_PICKAXE)
        || item.equals(Material.WOODEN_AXE)
        || item.equals(Material.STONE_AXE)
        || item.equals(Material.IRON_AXE)
        || item.equals(Material.DIAMOND_AXE)
        || item.equals(Material.GOLDEN_AXE);
    private static final MaterialMatcher DIGGING_TOOLS = material -> BASE_TOOLS.matches(material)
        || material == Material.SHEARS;
    private static final MaterialMatcher ALL_THINGS = material ->
        EnchantmentTarget.TOOL.includes(material)
            || EnchantmentTarget.WEAPON.includes(material)
            || EnchantmentTarget.ARMOR.includes(material)
            || material == Material.FISHING_ROD
            || material == Material.BOW
            || material == Material.CARROT_ON_A_STICK;
    private static final int GROUP_NONE = 0;
    private static final int GROUP_PROTECT = 1;
    private static final int GROUP_ATTACK = 2;
    private static final int GROUP_DIG = 3;

    static {
        VANILLA_IDS.addAll(
            Arrays.stream(GlowEnchantment.Impl.values()).map(GlowEnchantment.Impl::getVanillaId)
                .collect(Collectors.toSet()));
    }

    private final Impl impl;

    private GlowEnchantment(Impl impl) {
        super(impl.id);
        this.impl = impl;
    }

    /**
     * Register all enchantment types with Enchantment.
     */
    public static void register() {
        for (Impl impl : Impl.values()) {
            GlowEnchantment enchantment = new GlowEnchantment(impl);
            BY_VANILLA_ID.put(impl.getVanillaId(), enchantment);
            registerEnchantment(enchantment);
        }
        stopAcceptingRegistrations();
    }

    public static List<String> getVanillaIds() {
        return VANILLA_IDS;
    }

    /**
     * Parses a PotionEffect id or name if possible.
     *
     * @param enchantmentName The Enchantment name.
     * @return The associated Enchantment, or null.
     */
    public static Enchantment parseEnchantment(String enchantmentName) {
        try {
            int vanillaId = Integer.parseInt(enchantmentName);
            Enchantment enchantment = Enchantment.getById(vanillaId);

            if (enchantment == null) {
                return null;
            } else {
                return enchantment;
            }
        } catch (NumberFormatException exc) {
            if (enchantmentName.startsWith("minecraft:")) {
                Enchantment enchantment = GlowEnchantment.getByVanillaId(enchantmentName);

                if (enchantment == null) {
                    return null;
                } else {
                    return enchantment;
                }
            } else {
                Enchantment enchantment = Enchantment.getByName(enchantmentName);

                if (enchantment == null) {
                    return null;
                } else {
                    return enchantment;
                }
            }
        }
    }

    public static Enchantment getByVanillaId(String vanillaId) {
        return BY_VANILLA_ID.get(vanillaId);
    }

    @Override
    public String getName() {
        // nb: returns enum name, not text name
        return impl.name();
    }

    @Override
    public int getMaxLevel() {
        return impl.maxLevel;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return impl.target;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return impl.group != GROUP_NONE && impl.group == ((GlowEnchantment) other).impl.group;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return impl.matcher.matches(item.getType());
    }

    /**
     * The rarity of the enchantment, kept for compatibility with Bukkit.
     *
     * @see #getRarity()
     */
    @Override
    public int getWeight() {
        return getRarity().getWeight();
    }

    /**
     * Treasure enchantments can only be obtained from chest loot, fishing, or trading for enchanted
     * books.
     *
     * @return true if the enchantment is a treasure, otherwise false
     */
    @Override
    public boolean isTreasure() {
        return impl.treasure;
    }

    @Override
    public boolean isCursed() {
        return impl.cursed;
    }

    /**
     * Represents the rarity of obtaining an enchantment.
     *
     * @return the rarity of the enchantment
     */
    public Rarity getRarity() {
        return impl.rarity;
    }

    public boolean isInRange(int level, int modifier) {
        return modifier >= impl.getMinRange(level) && modifier <= impl.getMaxRange(level);
    }

    @RequiredArgsConstructor
    private enum Impl {
        PROTECTION_ENVIRONMENTAL(0, "Protection", 4, Rarity.COMMON, 1, 11, 20,
            EnchantmentTarget.ARMOR, GROUP_PROTECT, "minecraft:protection"),
        PROTECTION_FIRE(1, "Fire Protection", 4, Rarity.UNCOMMON, 10, 8, 12,
            EnchantmentTarget.ARMOR, GROUP_PROTECT, "minecraft:fire_protection"),
        PROTECTION_FALL(2, "Feather Falling", 4, Rarity.UNCOMMON, 5, 6, 10,
            EnchantmentTarget.ARMOR_FEET, GROUP_PROTECT, "minecraft:feather_falling"),
        PROTECTION_EXPLOSIONS(3, "Blast Protection", 4, Rarity.RARE, 5, 8, 12,
            EnchantmentTarget.ARMOR, "minecraft:blast_protection"),
        PROTECTION_PROJECTILE(4, "Projectile Protection", 4, Rarity.UNCOMMON, 3, 6, 15,
            EnchantmentTarget.ARMOR, GROUP_PROTECT, "minecraft:projectile_projection"),
        OXYGEN(5, "Respiration", 3, Rarity.RARE, 10, 10, 30, EnchantmentTarget.ARMOR_HEAD,
            "minecraft:respiration"),
        WATER_WORKER(6, "Aqua Affinity", 1, Rarity.RARE, 1, 0, 40, EnchantmentTarget.ARMOR_HEAD,
            "minecraft:aqua_affinity"),
        THORNS(7, "Thorns", 3, Rarity.VERY_RARE, 10, 20, 50, false, EnchantmentTarget.ARMOR_TORSO,
            new MatcherAdapter(EnchantmentTarget.ARMOR), "minecraft:thorns"),
        DEPTH_STRIDER(8, "Depth Strider", 3, Rarity.RARE, 10, 10, 15, EnchantmentTarget.ARMOR_FEET,
            "minecraft:depth_strider"),
        FROST_WALKER(9, "Frost Walker", 2, Rarity.RARE, 10, 10, 15, EnchantmentTarget.ARMOR_FEET,
            "minecraft:frost_walker"),
        BINDING_CURSE(10, "Curse of Binding", 1, Rarity.VERY_RARE, true, true, 25, 25, 50, false,
            EnchantmentTarget.ARMOR, new MatcherAdapter(EnchantmentTarget.ARMOR), GROUP_NONE,
            "minecraft:curse_of_binding"),
        DAMAGE_ALL(16, "Sharpness", 5, Rarity.COMMON, 1, 11, 20, EnchantmentTarget.WEAPON,
            SWORD_OR_AXE, GROUP_ATTACK, "minecraft:sharpness"),
        DAMAGE_UNDEAD(17, "Smite", 5, Rarity.UNCOMMON, 5, 8, 20, EnchantmentTarget.WEAPON,
            SWORD_OR_AXE, GROUP_ATTACK, "minecraft:smite"),
        DAMAGE_ARTHROPODS(18, "Bane of Arthropods", 5, Rarity.UNCOMMON, 5, 8, 20,
            EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK, "minecraft:bane_of_arthropods"),
        KNOCKBACK(19, "Knockback", 2, Rarity.UNCOMMON, 5, 20, 50, false, EnchantmentTarget.WEAPON,
            "minecraft:knockback"),
        FIRE_ASPECT(20, "Fire Aspect", 2, Rarity.RARE, 10, 20, 50, false, EnchantmentTarget.WEAPON,
            "minecraft:fire_aspect"),
        LOOT_BONUS_MOBS(21, "Looting", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.WEAPON,
            "minecraft:looting"),
        SWEEPING_EDGE(22, "Sweeping Edge", 3, Rarity.RARE, 25, 25, 50, EnchantmentTarget.WEAPON,
            "minecraft:sweeping_edge"), // TODO: correct range values
        DIG_SPEED(32, "Efficiency", 5, Rarity.COMMON, 1, 10, 50, false, EnchantmentTarget.TOOL,
            DIGGING_TOOLS, "minecraft:efficiency"),
        SILK_TOUCH(33, "Silk Touch", 1, Rarity.VERY_RARE, false, 15, 0, 50, false,
            EnchantmentTarget.TOOL, DIGGING_TOOLS, GROUP_DIG, "minecraft:silk_touch"),
        DURABILITY(34, "Unbreaking", 3, Rarity.UNCOMMON, 5, 8, 50, false, EnchantmentTarget.TOOL,
            ALL_THINGS, "minecraft:unbreaking"),
        LOOT_BONUS_BLOCKS(35, "Fortune", 3, Rarity.RARE, 15, 9, 50, EnchantmentTarget.TOOL,
            BASE_TOOLS, GROUP_DIG, "minecraft:fortune"),
        ARROW_DAMAGE(48, "Power", 5, Rarity.COMMON, 1, 10, 15, EnchantmentTarget.BOW,
            "minecraft:power"),
        ARROW_KNOCKBACK(49, "Punch", 2, Rarity.RARE, 12, 20, 25, EnchantmentTarget.BOW,
            "minecraft:punch"),
        ARROW_FIRE(50, "Flame", 1, Rarity.RARE, 20, 0, 30, EnchantmentTarget.BOW,
            "minecraft:flame"),
        ARROW_INFINITE(51, "Infinity", 1, Rarity.VERY_RARE, 20, 0, 30, EnchantmentTarget.BOW,
            "minecraft:infinity"),
        LUCK(61, "Luck of the Sea", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.FISHING_ROD,
            "minecraft:luck_of_the_sea"),
        LURE(62, "Lure", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.FISHING_ROD,
            "minecraft:lure"),
        MENDING(70, "Mending", 1, Rarity.RARE, 25, 25, 50, EnchantmentTarget.ALL,
            "minecraft:mending"),
        VANISHING_CURSE(71, "Curse of Vanishing", 1, Rarity.VERY_RARE, true, true, 25, 25, 50,
            false, EnchantmentTarget.ALL, new MatcherAdapter(EnchantmentTarget.ALL), GROUP_NONE,
            "minecraft:curse_of_vanishing");

        private final int id;
        private final String name;
        private final int maxLevel;
        private final Rarity rarity;
        private final boolean treasure;
        private final boolean cursed;
        private final int minValue;
        private final int minIncrement;
        private final int maxIncrement;
        private final boolean simpleRange;
        private final EnchantmentTarget target;
        private final MaterialMatcher matcher;
        private final int group;
        @Getter private final String vanillaId;

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            EnchantmentTarget target, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target,
                new MatcherAdapter(target), GROUP_NONE, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            boolean simpleRange, EnchantmentTarget target, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target,
                new MatcherAdapter(target), GROUP_NONE, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            EnchantmentTarget target, int group, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target,
                new MatcherAdapter(target), group, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            boolean simpleRange, EnchantmentTarget target, MaterialMatcher matcher,
            String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target,
                matcher, GROUP_NONE, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            boolean simpleRange, EnchantmentTarget target, MatcherAdapter matcher,
            String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target,
                matcher, GROUP_NONE, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc,
            EnchantmentTarget target, MaterialMatcher matcher, int group, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target, matcher,
                group, vanillaId);
        }

        Impl(int id, String name, int max, Rarity rarity, boolean treasure, int minValue,
            int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target,
            MaterialMatcher matcher, int group, String vanillaId) {
            this(id, name, max, rarity, treasure, false, minValue, minInc, maxInc, true, target,
                matcher, group, vanillaId);
        }

        int getMinRange(int modifier) {
            modifier = modifier - 1; // Formula depends on input 1 being 0 for "no offset"
            return minIncrement * modifier + minValue;
        }

        int getMaxRange(int modifier) {
            return (simpleRange ? getMinRange(modifier) : 1 + modifier * 10) + maxIncrement;
        }
    }

    private enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        Rarity(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }
    }

    private static class MatcherAdapter implements MaterialMatcher {

        private final EnchantmentTarget target;

        public MatcherAdapter(EnchantmentTarget target) {
            this.target = target;
        }

        @Override
        public boolean matches(Material material) {
            return target.includes(material);
        }
    }
}
