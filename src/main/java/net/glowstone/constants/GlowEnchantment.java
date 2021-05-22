package net.glowstone.constants;

import io.papermc.paper.enchantments.EnchantmentRarity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.WeightedRandom.Choice;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Definitions of enchantment types.
 */
public final class GlowEnchantment extends Enchantment implements Choice {

    private static final List<String> VANILLA_IDS = new ArrayList<>();
    private static final HashMap<String, Enchantment> BY_VANILLA_ID = new HashMap<>();

    private static final MaterialMatcher MELEE = item ->
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
    private static final MaterialMatcher ALL_EQUIPMENT = material ->
        EnchantmentTarget.TOOL.includes(material)
            || EnchantmentTarget.WEAPON.includes(material)
            || EnchantmentTarget.ARMOR.includes(material)
            || material == Material.FISHING_ROD
            || material == Material.BOW
            || material == Material.CARROT_ON_A_STICK;

    static {
        VANILLA_IDS.addAll(
            Arrays.stream(GlowEnchantment.Impl.values()).map(GlowEnchantment.Impl::getVanillaId)
                .collect(Collectors.toSet()));
    }

    private final Impl impl;

    private GlowEnchantment(Impl impl) {
        super(NbtSerialization.namespacedKeyFromString(impl.getVanillaId()));
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

    public static Enchantment getByVanillaId(String vanillaId) {
        return BY_VANILLA_ID.get(vanillaId);
    }

    @Override
    public String getName() {
        // nb: returns enum name, not text name
        return impl.name();
    }

    public int getId() {
        return impl.id;
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
        return impl.group != Group.NONE && impl.group == ((GlowEnchantment) other).impl.group;
    }

    // TODO: primary and secondary items.
    // primary items can be enchanted using an enchanting table
    // secondary items can only be enchanted using an enchanted book and anvil
    // once that code is implemented, replace the return below
    @Override
    public boolean canEnchantItem(ItemStack item) {
        // TODO: return canEnchantPrimary(item) || canEnchantSecondary(item);
        return impl.matcher.matches(item.getType());
    }

    @Override
    public @NotNull Component displayName(int i) {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public boolean isTradeable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDiscoverable() {
        throw new UnsupportedOperationException();
    }

    /**
     * If this enchantment can be applied to this item using an enchantment table.
     *
     * @return If this item is a primary item for this enchantment
     */
    public boolean canEnchantPrimary(ItemStack item) {
        return false;
    }

    /**
     * If this enchantment cannot be applied using an enchantment table but can be applied using
     * an enchanted book and anvil.
     *
     * @return If this item is a secondary item for this enchantment
     */
    public boolean canEnchantSecondary(ItemStack item) {
        return false;
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
    public EnchantmentRarity getRarity() {
        return impl.rarity;
    }

    @Override
    public float getDamageIncrease(int i, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return null;
    }

    public boolean isInRange(int level, int modifier) {
        return modifier >= impl.getMinRange(level) && modifier <= impl.getMaxRange(level);
    }

    // TODO: GlowEnchantment builder instead of Impl enum init
    @RequiredArgsConstructor
    private enum Impl {
        PROTECTION_ENVIRONMENTAL(0, "Protection", 4, EnchantmentRarity.COMMON, 1, 11, 20,
            EnchantmentTarget.ARMOR, Group.PROTECT, "minecraft:protection"),
        PROTECTION_FIRE(1, "Fire Protection", 4, EnchantmentRarity.UNCOMMON, 10, 8, 12,
            EnchantmentTarget.ARMOR, Group.PROTECT, "minecraft:fire_protection"),
        PROTECTION_FALL(2, "Feather Falling", 4, EnchantmentRarity.UNCOMMON, 5, 6, 10,
            EnchantmentTarget.ARMOR_FEET, Group.PROTECT, "minecraft:feather_falling"),
        PROTECTION_EXPLOSIONS(3, "Blast Protection", 4, EnchantmentRarity.RARE, 5, 8, 12,
            EnchantmentTarget.ARMOR, "minecraft:blast_protection"),
        PROTECTION_PROJECTILE(4, "Projectile Protection", 4, EnchantmentRarity.UNCOMMON, 3, 6, 15,
            EnchantmentTarget.ARMOR, Group.PROTECT, "minecraft:projectile_protection"),
        OXYGEN(5, "Respiration", 3, EnchantmentRarity.RARE, 10, 10, 30,
            EnchantmentTarget.ARMOR_HEAD,
            "minecraft:respiration"),
        WATER_WORKER(6, "Aqua Affinity", 1, EnchantmentRarity.RARE, 1, 0, 40,
            EnchantmentTarget.ARMOR_HEAD,
            "minecraft:aqua_affinity"),
        THORNS(7, "Thorns", 3, EnchantmentRarity.VERY_RARE, 10, 20, 50,
            EnchantmentTarget.ARMOR_TORSO,
            new MatcherAdapter(EnchantmentTarget.ARMOR), "minecraft:thorns"),
        DEPTH_STRIDER(8, "Depth Strider", 3, EnchantmentRarity.RARE, 10, 10, 15,
            EnchantmentTarget.ARMOR_FEET,
            "minecraft:depth_strider"),
        FROST_WALKER(9, "Frost Walker", 2, EnchantmentRarity.RARE, 10, 10, 15,
            EnchantmentTarget.ARMOR_FEET,
            "minecraft:frost_walker"),
        BINDING_CURSE(10, "Curse of Binding", 1, EnchantmentRarity.VERY_RARE, true, 25, 0, 25,
            EnchantmentTarget.ARMOR, new MatcherAdapter(EnchantmentTarget.ARMOR), Group.NONE,
            "minecraft:binding_curse"),
        DAMAGE_ALL(16, "Sharpness", 5, EnchantmentRarity.COMMON, 1, 11, 20,
            EnchantmentTarget.WEAPON,
            MELEE, Group.ATTACK, "minecraft:sharpness"),
        DAMAGE_UNDEAD(17, "Smite", 5, EnchantmentRarity.UNCOMMON, 5, 8, 20,
            EnchantmentTarget.WEAPON,
            MELEE, Group.ATTACK, "minecraft:smite"),
        DAMAGE_ARTHROPODS(18, "Bane of Arthropods", 5, EnchantmentRarity.UNCOMMON, 5, 8, 20,
            EnchantmentTarget.WEAPON, MELEE, Group.ATTACK, "minecraft:bane_of_arthropods"),
        KNOCKBACK(19, "Knockback", 2, EnchantmentRarity.UNCOMMON, 5, 20, 50,
            EnchantmentTarget.WEAPON,
            "minecraft:knockback"),
        FIRE_ASPECT(20, "Fire Aspect", 2, EnchantmentRarity.RARE, 10, 20, 50,
            EnchantmentTarget.WEAPON,
            "minecraft:fire_aspect"),
        LOOT_BONUS_MOBS(21, "Looting", 3, EnchantmentRarity.RARE, 15, 9, 50,
            EnchantmentTarget.WEAPON,
            "minecraft:looting"),
        SWEEPING_EDGE(22, "Sweeping Edge", 3, EnchantmentRarity.RARE, 5, 9, 15,
            EnchantmentTarget.WEAPON,
            "minecraft:sweeping"),
        DIG_SPEED(32, "Efficiency", 5, EnchantmentRarity.COMMON, 1, 10, 50, EnchantmentTarget.TOOL,
            DIGGING_TOOLS, "minecraft:efficiency"),
        SILK_TOUCH(33, "Silk Touch", 1, EnchantmentRarity.VERY_RARE, false, 15, 0, 50,
            EnchantmentTarget.TOOL, BASE_TOOLS, Group.DIG, "minecraft:silk_touch"),
        DURABILITY(34, "Unbreaking", 3, EnchantmentRarity.UNCOMMON, 5, 8, 50,
            EnchantmentTarget.TOOL,
            ALL_EQUIPMENT, "minecraft:unbreaking"),
        LOOT_BONUS_BLOCKS(35, "Fortune", 3, EnchantmentRarity.RARE, 15, 9, 50,
            EnchantmentTarget.TOOL,
            BASE_TOOLS, Group.DIG, "minecraft:fortune"),
        ARROW_DAMAGE(48, "Power", 5, EnchantmentRarity.COMMON, 1, 10, 15, EnchantmentTarget.BOW,
            "minecraft:power"),
        ARROW_KNOCKBACK(49, "Punch", 2, EnchantmentRarity.RARE, 12, 20, 25, EnchantmentTarget.BOW,
            "minecraft:punch"),
        ARROW_FIRE(50, "Flame", 1, EnchantmentRarity.RARE, 20, 0, 30, EnchantmentTarget.BOW,
            "minecraft:flame"),
        ARROW_INFINITE(51, "Infinity", 1, EnchantmentRarity.VERY_RARE, 20, 0, 30,
            EnchantmentTarget.BOW, Group.USAGE,
            "minecraft:infinity"),
        LUCK(61, "Luck of the Sea", 3, EnchantmentRarity.RARE, 15, 9, 50,
            EnchantmentTarget.FISHING_ROD,
            "minecraft:luck_of_the_sea"),
        LURE(62, "Lure", 3, EnchantmentRarity.RARE, 15, 9, 50, EnchantmentTarget.FISHING_ROD,
            "minecraft:lure"),
        LOYALTY(65, "Loyalty", 3, EnchantmentRarity.UNCOMMON, 17, 5, 23, 50,
            EnchantmentTarget.TRIDENT, Group.TRIDENT_THROW, "minecraft:loyalty"),
        IMPALING(66, "Impaling", 5, EnchantmentRarity.RARE, 1, 8, 20, EnchantmentTarget.TRIDENT,
            "minecraft:impaling"),
        RIPTIDE(67, "Riptide", 3, EnchantmentRarity.RARE, 17, 10, 13, 50, EnchantmentTarget.TRIDENT,
            Group.TRIDENT_THROW, "minecraft:riptide"),
        CHANNELING(68, "Channeling", 1, EnchantmentRarity.VERY_RARE, 25, 0, 25,
            EnchantmentTarget.TRIDENT, "minecraft:channeling"),
        MENDING(70, "Mending", 1, EnchantmentRarity.RARE, 25, 0, 50, EnchantmentTarget.ALL,
            Group.USAGE,
            "minecraft:mending"),
        VANISHING_CURSE(71, "Curse of Vanishing", 1, EnchantmentRarity.VERY_RARE, true, 25, 0, 25,
            EnchantmentTarget.ALL, new MatcherAdapter(EnchantmentTarget.ALL), Group.NONE,
            "minecraft:vanishing_curse");


        private final int id;
        private final String name;
        private final int maxLevel;
        private final EnchantmentRarity rarity;
        private final boolean treasure;
        private final boolean cursed;
        private final int minValue; // https://minecraft.gamepedia.com/Enchanting/Levels
        private final int minIncrement;
        private final int maxIncrement;
        private final int maxValue;
        private final EnchantmentTarget target;
        private final MaterialMatcher matcher;
        private final Group group;
        @Getter
        private final String vanillaId;

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc,
             EnchantmentTarget target, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, target,
                new MatcherAdapter(target), Group.NONE, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc,
             EnchantmentTarget target, Group group, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, target,
                new MatcherAdapter(target), group, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc,
             EnchantmentTarget target, MaterialMatcher matcher,
             String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, target,
                matcher, Group.NONE, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc, EnchantmentTarget target, MatcherAdapter matcherAdapter,
             String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, target, matcherAdapter,
                Group.NONE, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc,
             EnchantmentTarget target, MaterialMatcher matcher, Group group, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, target, matcher,
                group, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, boolean treasure, int minValue,
             int minInc, int maxInc, EnchantmentTarget target,
             MaterialMatcher matcher, Group group, String vanillaId) {
            this(id, name, max, rarity, treasure, minValue, minInc, maxInc, Integer.MAX_VALUE,
                target,
                matcher, group, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, boolean treasure, int minValue,
             int minInc, int maxInc, int maxValue, EnchantmentTarget target,
             MaterialMatcher matcher, Group group, String vanillaId) {
            this(id, name, max, rarity, treasure, false, minValue, minInc, maxInc, maxValue, target,
                matcher, group, vanillaId);
        }

        Impl(int id, String name, int max, EnchantmentRarity rarity, int minValue, int minInc,
             int maxInc, int maxValue, EnchantmentTarget target, Group group, String vanillaId) {
            this(id, name, max, rarity, false, minValue, minInc, maxInc, maxValue, target,
                new MatcherAdapter(target), group, vanillaId);
        }

        int getMinRange(int modifier) {
            modifier = modifier - 1; // Formula depends on input 1 being 0 for "no offset"
            return minIncrement * modifier + minValue;
        }

        int getMaxRange(int modifier) {
            return Math.min(getMinRange(modifier) + maxIncrement, maxValue);
        }
    }

    private enum Group {
        NONE,
        PROTECT,
        ATTACK,
        DIG,
        USAGE,
        ARROW,
        TRIDENT_THROW // TODO: riptide incompatible with loyalty and channeling, but loyalty and channeling not incompatible, so group system does not work
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
