package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

class Durability extends GlowEnchantment {
    Durability() {
        super(34, "DURABILITY", "Unbreaking", 3, 5, EnchantmentTarget.TOOL, ALL_THINGS);
    }

    @Override
    public int getMinRange(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMaxRange(level) + 50;
    }
}
