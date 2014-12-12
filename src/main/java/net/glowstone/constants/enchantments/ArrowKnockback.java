package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class ArrowKnockback extends GlowEnchantment {
    ArrowKnockback() {
        super(49, "ARROW_KNOCKBACK", "Punch", 2, 2, EnchantmentTarget.BOW);
    }

    @Override
    public int getMinRange(int level) {
        return 12 + (level - 1) * 20;
    }

    @Override
    public int getMaxRange(int level) {
        return getMinRange(level) + 25;
    }
}
