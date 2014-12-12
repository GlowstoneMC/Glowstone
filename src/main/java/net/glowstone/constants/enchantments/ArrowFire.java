package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class ArrowFire extends GlowEnchantment {
    ArrowFire() {
        super(50, "ARROW_FIRE", "Flame", 1, 2, EnchantmentTarget.BOW);
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
