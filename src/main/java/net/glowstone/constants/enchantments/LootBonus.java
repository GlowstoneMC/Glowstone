package net.glowstone.constants.enchantments;

import net.glowstone.inventory.MaterialMatcher;
import org.bukkit.enchantments.EnchantmentTarget;

abstract class LootBonus extends GlowEnchantment {
    protected LootBonus(int id, String name, String userName, EnchantmentTarget target) {
        super(id, name, userName, 3, 2, target);
    }

    protected LootBonus(int id, String name, String userName, EnchantmentTarget target, MaterialMatcher matcher, Group group) {
        super(id, name, userName, 3, 2, target, matcher, group);
    }

    @Override
    public int getMinRange(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMinRange(level) + 50;
    }
}
