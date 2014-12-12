package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class DepthStrider extends GlowEnchantment {
    DepthStrider() {
        super(8, "DEPTH_STRIDER", "Depth Strider", 3, 2, EnchantmentTarget.ARMOR_FEET);
    }

    @Override
    public int getMinRange(int level) {
        return level * 10;
    }

    @Override
    public int getMaxRange(int level) {
        return getMinRange(level) + 15;
    }
}
