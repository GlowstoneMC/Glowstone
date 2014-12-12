package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class ArrowInfinity extends GlowEnchantment {
    ArrowInfinity() {
        super(51, "ARROW_INFINITE", "Infinity", 1, 1, EnchantmentTarget.BOW);
    }

    @Override
    public int getMinRange(int level) {
        return 20;
    }

    @Override
    public int getMaxRange(int level) {
        return 50;
    }
}
