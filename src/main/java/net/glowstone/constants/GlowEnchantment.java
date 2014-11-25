package net.glowstone.constants;

import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * Definitions of enchantment types.
 */
public final class GlowEnchantment extends Enchantment {

    private final Impl impl;

    private GlowEnchantment(Impl impl) {
        super(impl.id);
        this.impl = impl;
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
     * Register all enchantment types with Enchantment.
     */
    public static void register() {
        for (Impl impl : Impl.values()) {
            registerEnchantment(new GlowEnchantment(impl));
        }
        stopAcceptingRegistrations();
    }

    private static final MaterialMatcher SWORD_OR_AXE = new MaterialMatcher() {
        @Override
        public boolean matches(Material item) {
            return EnchantmentTarget.WEAPON.includes(item)
                    || item.equals(Material.WOOD_AXE)
                    || item.equals(Material.STONE_AXE)
                    || item.equals(Material.IRON_AXE)
                    || item.equals(Material.DIAMOND_AXE)
                    || item.equals(Material.GOLD_AXE);
        }
    };

    private static final MaterialMatcher BASE_TOOLS = new MaterialMatcher() {
        @Override
        public boolean matches(Material item) {
            return item.equals(Material.WOOD_SPADE)
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
        }
    };

    private static final MaterialMatcher DIGGING_TOOLS = new MaterialMatcher() {
        @Override
        public boolean matches(Material material) {
            return BASE_TOOLS.matches(material)
                    || material == Material.SHEARS;
        }
    };

    private static final MaterialMatcher ALL_THINGS = new MaterialMatcher() {
        @Override
        public boolean matches(Material material) {
            return EnchantmentTarget.TOOL.includes(material)
                    || EnchantmentTarget.WEAPON.includes(material)
                    || EnchantmentTarget.ARMOR.includes(material)
                    || material == Material.FISHING_ROD
                    || material == Material.BOW
                    || material == Material.CARROT_STICK;
        }
    };

    private static final int GROUP_NONE = 0;
    private static final int GROUP_PROTECT = 1;
    private static final int GROUP_ATTACK = 2;
    private static final int GROUP_DIG = 3;

    private static enum Impl {
        PROTECTION_ENVIRONMENTAL(0, "Protection", 4, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        PROTECTION_FIRE(1, "Fire Protection", 4, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        PROTECTION_FALL(2, "Feather Falling", 4, EnchantmentTarget.ARMOR_FEET, GROUP_PROTECT),
        PROTECTION_EXPLOSIONS(3, "Blast Protection", 4, EnchantmentTarget.ARMOR),
        PROTECTION_PROJECTILE(4, "Projectile Protection", 4, EnchantmentTarget.ARMOR, GROUP_PROTECT),
        OXYGEN(5, "Respiration", 3, EnchantmentTarget.ARMOR_HEAD),
        WATER_WORKER(6, "Aqua Affinity", 1, EnchantmentTarget.ARMOR_HEAD),
        THORNS(7, "Thorns", 3, EnchantmentTarget.ARMOR_TORSO, new MatcherAdapter(EnchantmentTarget.ARMOR)),
        DEPTH_STRIDER(8, "Depth Strider", 3, EnchantmentTarget.ARMOR_FEET),
        DAMAGE_ALL(16, "Sharpness", 5, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        DAMAGE_UNDEAD(17, "Smite", 5, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        DAMAGE_ARTHROPODS(18, "Bane of Arthropods", 5, EnchantmentTarget.WEAPON, SWORD_OR_AXE, GROUP_ATTACK),
        KNOCKBACK(19, "Knockback", 2, EnchantmentTarget.WEAPON),
        FIRE_ASPECT(20, "Fire Aspect", 2, EnchantmentTarget.WEAPON),
        LOOT_BONUS_MOBS(21, "Looting", 3, EnchantmentTarget.WEAPON),
        DIG_SPEED(32, "Efficiency", 5, EnchantmentTarget.TOOL, DIGGING_TOOLS),
        SILK_TOUCH(33, "Silk Touch", 1, EnchantmentTarget.TOOL, DIGGING_TOOLS, GROUP_DIG),
        DURABILITY(34, "Unbreaking", 3, EnchantmentTarget.TOOL, ALL_THINGS),
        LOOT_BONUS_BLOCKS(35, "Fortune", 3, EnchantmentTarget.TOOL, BASE_TOOLS, GROUP_DIG),
        ARROW_DAMAGE(48, "Power", 5, EnchantmentTarget.BOW),
        ARROW_KNOCKBACK(49, "Punch", 2, EnchantmentTarget.BOW),
        ARROW_FIRE(50, "Flame", 1, EnchantmentTarget.BOW),
        ARROW_INFINITE(51, "Infinity", 1, EnchantmentTarget.BOW),
        LUCK(61, "Luck of the Sea", 3, EnchantmentTarget.FISHING_ROD),
        LURE(62, "Lure", 3, EnchantmentTarget.FISHING_ROD);

        private final int id;
        private final String name;
        private final int maxLevel;
        private final EnchantmentTarget target;
        private final MaterialMatcher matcher;
        private final int group;

        Impl(int id, String name, int max, EnchantmentTarget target) {
            this(id, name, max, target, new MatcherAdapter(target), GROUP_NONE);
        }

        Impl(int id, String name, int max, EnchantmentTarget target, int group) {
            this(id, name, max, target, new MatcherAdapter(target), group);
        }

        Impl(int id, String name, int max, EnchantmentTarget target, MaterialMatcher matcher) {
            this(id, name, max, target, matcher, GROUP_NONE);
        }

        Impl(int id, String name, int max, EnchantmentTarget target, MaterialMatcher matcher, int group) {
            this.id = id;
            this.name = name;
            this.maxLevel = max;
            this.target = target;
            this.matcher = matcher;
            this.group = group;
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
