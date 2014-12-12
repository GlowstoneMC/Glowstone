package net.glowstone.constants.enchantments;

import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import net.glowstone.util.WeightedRandom;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * Definitions of enchantment types.
 */
public class GlowEnchantment extends Enchantment implements WeightedRandom.Choice {
    protected static final MaterialMatcher SWORD_OR_AXE = new MaterialMatcher() {
        @Override
        public boolean matches(Material item) {
            return ToolType.SWORD.matches(item) || ToolType.AXE.matches(item);
        }
    };
    protected static final MaterialMatcher BASE_TOOLS = new MaterialMatcher() {
        @Override
        public boolean matches(Material item) {
            return ToolType.SPADE.matches(item) || ToolType.PICKAXE.matches(item) || ToolType.AXE.matches(item);
        }
    };
    protected static final MaterialMatcher DIGGING_TOOLS = new MaterialMatcher() {
        @Override
        public boolean matches(Material material) {
            return BASE_TOOLS.matches(material)
                    || material == Material.SHEARS;
        }
    };
    protected static final MaterialMatcher ALL_THINGS = new MaterialMatcher() {
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


    public final int weight;
    private final String name;
    private final String userName;
    private final int maxLevel;
    private final EnchantmentTarget target;
    private final MaterialMatcher matcher;
    private final Group group;

    GlowEnchantment(int id, String name, String userName, int max, int weight, EnchantmentTarget target) {
        this(id, name, userName, max, weight, target, new MatcherAdapter(target), Group.NONE);
    }


    GlowEnchantment(int id, String name, String userName, int max, int weight, EnchantmentTarget target, Group group) {
        this(id, name, userName, max, weight, target, new MatcherAdapter(target), group);
    }

    GlowEnchantment(int id, String name, String userName, int max, int weight, EnchantmentTarget target, MaterialMatcher matcher) {
        this(id, name, userName, max, weight, target, matcher, Group.NONE);
    }

    GlowEnchantment(int id, String name, String userName, int max, int weight, EnchantmentTarget target, MaterialMatcher matcher, Group group) {
        super(id);
        this.name = name;
        this.userName = userName;
        this.maxLevel = max;
        this.weight = weight;
        this.target = target;
        this.matcher = matcher;
        this.group = group;
    }

    /**
     * Register all enchantment types with Enchantment.
     */
    public static void register() {
        for (VanillaEnchantment enchantment : VanillaEnchantment.values()) {
            registerEnchantment(enchantment.getGlowEnchantment());
        }
        stopAcceptingRegistrations();
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return target;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return group != Group.NONE && group == ((GlowEnchantment) other).group;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return matcher.matches(item.getType());
    }

    @Override
    public int getWeight() {
        return weight;
    }

    protected int getMinRange(int level) {
        return 1 + level * 10;
    }

    protected int getMaxRange(int level) {
        return getMinRange(level) + 5;
    }

    public boolean isInRange(int level, int modifier) {
        return modifier >= getMinRange(level) && modifier <= getMaxRange(level);
    }

    protected enum Group {
        NONE,
        PROTECT,
        ATTACK,
        DIG;
    }

    protected static class MatcherAdapter implements MaterialMatcher {
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
