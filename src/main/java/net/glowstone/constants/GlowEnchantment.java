package net.glowstone.constants;

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

    private static final MaterialMatcher SWORD_OR_AXE = item -> EnchantmentTarget.WEAPON.includes(item)
            || item.equals(Material.WOOD_AXE)
            || item.equals(Material.STONE_AXE)
            || item.equals(Material.IRON_AXE)
            || item.equals(Material.DIAMOND_AXE)
            || item.equals(Material.GOLD_AXE);
    private static final MaterialMatcher BASE_TOOLS = item -> item.equals(Material.WOOD_SPADE)
            || item.equals(Material.STONE_SPADE)
            || item.equals(Material.IRON_SPADE)
            || item.equals(Material.DIAMOND_SPADE)
            || item.equals(Material.GOLD_SPADE)
            || item.equals(Material.WOOD_PICKAXE)
            || item.equals(Material.STONE_PICKAXE)
            || item.equals(Material.IRON_PICKAXE)
            || item.equals(Material.DIAMOND_PICKAXE)
            || item.equals(Material.GOLD_PICKAXE)
            || item.equals(Material.WOOD_AXE)
            || item.equals(Material.STONE_AXE)
            || item.equals(Material.IRON_AXE)
            || item.equals(Material.DIAMOND_AXE)
            || item.equals(Material.GOLD_AXE);
    private static final MaterialMatcher DIGGING_TOOLS = material -> BASE_TOOLS.matches(material)
            || material == Material.SHEARS;
    private static final MaterialMatcher ALL_THINGS = material -> EnchantmentTarget.TOOL.includes(material)
            || EnchantmentTarget.WEAPON.includes(material)
            || EnchantmentTarget.ARMOR.includes(material)
            || material == Material.FISHING_ROD
            || material == Material.BOW
            || material == Material.CARROT_STICK;
    private static final int GROUP_NONE = 0;
    private static final int GROUP_PROTECT = 1;
    private static final int GROUP_ATTACK = 2;
    private static final int GROUP_DIG = 3;
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
            registerEnchantment(new GlowEnchantment(impl));
        }
        stopAcceptingRegistrations();
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
     * Treasure enchantments can only be obtained from chest loot, fishing, or trading for enchanted books.
     *
     * @return true if the enchantment is a treasure, otherwise false
     */
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

    private enum Impl {
        PROTECTION_ENVIRONMENTAL(0, "Protection", 4, Rarity.COMMON, 1, 11, 20, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        PROTECTION_FIRE(1, "Fire Protection", 4, Rarity.UNCOMMON, 10, 8, 12, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        PROTECTION_FALL(2, "Feather Falling", 4, Rarity.UNCOMMON, 5, 6, 10, EnchantmentTarget.ARMOR_FEET, GROUP_PROTECT),
        PROTECTION_EXPLOSIONS(3, "Blast Protection", 4, Rarity.RARE, 5, 8, 12, EnchantmentTarget.ARMOR),
        PROTECTION_PROJECTILE(4, "Projectile Protection", 4, Rarity.UNCOMMON, 3, 6, 15, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        OXYGEN(5, "Respiration", 3, Rarity.RARE, 10, 10, 30, EnchantmentTarget.ARMOR_HEAD),
        WATER_WORKER(6, "Aqua Affinity", 1, Rarity.RARE, 1, 0, 40, EnchantmentTarget.ARMOR_HEAD),
        THORNS(7, "Thorns", 3, Rarity.VERY_RARE, 10, 20, 50, false, EnchantmentTarget.ARMOR_TORSO, new MatcherAdapter(EnchantmentTarget.ARMOR)),
        DEPTH_STRIDER(8, "Depth Strider", 3, Rarity.RARE, 10, 10, 15, EnchantmentTarget.ARMOR_FEET),
        FROST_WALKER(9, "Frost Walker", 2, Rarity.RARE, 10, 10, 15, EnchantmentTarget.ARMOR_FEET),
        BINDING_CURSE(10, "Curse of Binding", 1, Rarity.VERY_RARE, true, true, 25, 25, 50, false, EnchantmentTarget.ARMOR, new MatcherAdapter(EnchantmentTarget.ARMOR), GROUP_NONE),
        DAMAGE_ALL(16, "Sharpness", 5, Rarity.COMMON, 1, 11, 20, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        DAMAGE_UNDEAD(17, "Smite", 5, Rarity.UNCOMMON, 5, 8, 20, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        DAMAGE_ARTHROPODS(18, "Bane of Arthropods", 5, Rarity.UNCOMMON, 5, 8, 20, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        KNOCKBACK(19, "Knockback", 2, Rarity.UNCOMMON, 5, 20, 50, false, EnchantmentTarget.WEAPON),
        FIRE_ASPECT(20, "Fire Aspect", 2, Rarity.RARE, 10, 20, 50, false, EnchantmentTarget.WEAPON),
        LOOT_BONUS_MOBS(21, "Looting", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.WEAPON),
        DIG_SPEED(32, "Efficiency", 5, Rarity.COMMON, 1, 10, 50, false, EnchantmentTarget.TOOL, DIGGING_TOOLS),
        SILK_TOUCH(33, "Silk Touch", 1, Rarity.VERY_RARE, false, 15, 0, 50, false, EnchantmentTarget.TOOL, DIGGING_TOOLS, GROUP_DIG),
        DURABILITY(34, "Unbreaking", 3, Rarity.UNCOMMON, 5, 8, 50, false, EnchantmentTarget.TOOL, ALL_THINGS),
        LOOT_BONUS_BLOCKS(35, "Fortune", 3, Rarity.RARE, 15, 9, 50, EnchantmentTarget.TOOL, BASE_TOOLS, GROUP_DIG),
        ARROW_DAMAGE(48, "Power", 5, Rarity.COMMON, 1, 10, 15, EnchantmentTarget.BOW),
        ARROW_KNOCKBACK(49, "Punch", 2, Rarity.RARE, 12, 20, 25, EnchantmentTarget.BOW),
        ARROW_FIRE(50, "Flame", 1, Rarity.RARE, 20, 0, 30, EnchantmentTarget.BOW),
        ARROW_INFINITE(51, "Infinity", 1, Rarity.VERY_RARE, 20, 0, 30, EnchantmentTarget.BOW),
        LUCK(61, "Luck of the Sea", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.FISHING_ROD),
        LURE(62, "Lure", 3, Rarity.RARE, 15, 9, 50, false, EnchantmentTarget.FISHING_ROD),
        MENDING(70, "Mending", 1, Rarity.RARE, 25, 25, 50, EnchantmentTarget.ALL),
        VANISHING_CURSE(71, "Curse of Vanishing", 1, Rarity.VERY_RARE, true, true, 25, 25, 50, false, EnchantmentTarget.ALL, new MatcherAdapter(EnchantmentTarget.ALL), GROUP_NONE);

        private final int id;
        private final String name;
        private final int maxLevel;
        private final EnchantmentTarget target;
        private final MaterialMatcher matcher;
        private final int group;
        private final Rarity rarity;
        private final int minValue, minIncrement;
        private final int maxIncrement;
        private final boolean treasure;
        private final boolean simpleRange;
        private final boolean cursed;

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, EnchantmentTarget target) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target, new MatcherAdapter(target), GROUP_NONE);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target, new MatcherAdapter(target), GROUP_NONE);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, EnchantmentTarget target, int group) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target, new MatcherAdapter(target), group);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target, MaterialMatcher matcher) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target, matcher, GROUP_NONE);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target, MatcherAdapter matcher) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, simpleRange, target, matcher, GROUP_NONE);
        }

        Impl(int id, String name, int max, Rarity rarity, int minValue, int minInc, int maxInc, EnchantmentTarget target, MaterialMatcher matcher, int group) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, true, target, matcher, group);
        }

        Impl(int id, String name, int max, Rarity rarity, boolean treasure, int minValue, int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target, MaterialMatcher matcher, int group) {
            this(id, name, max, rarity, treasure, false, minValue, minInc, maxInc, true, target, matcher, group);
        }

        Impl(int id, String name, int max, Rarity rarity, boolean treasure, boolean cursed, int minValue, int minInc, int maxInc, boolean simpleRange, EnchantmentTarget target, MaterialMatcher matcher, int group) {
            this.id = id;
            this.name = name;
            maxLevel = max;
            this.rarity = rarity;
            this.target = target;
            this.matcher = matcher;
            this.group = group;
            this.minValue = minValue;
            minIncrement = minInc;
            maxIncrement = maxInc;
            this.simpleRange = simpleRange;
            this.treasure = treasure;
            this.cursed = cursed;
        }

        int getMinRange(int modifier) {
            modifier = modifier - 1; // Formula depends on input 1 being 0 for "no offset"
            return minIncrement * modifier + minValue;
        }

        int getMaxRange(int modifier) {
            return (simpleRange ? getMinRange(modifier) : 1 + modifier * 10) + maxIncrement;
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
}
