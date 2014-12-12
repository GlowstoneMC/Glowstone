package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class ArrowDamage extends GlowEnchantment {
    ArrowDamage() {
        super(48, "ARROW_DAMAGE", "Power", 5, 10, EnchantmentTarget.BOW);
    }

    @Override
    public int getMinRange(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxRange(int level) {
        return getMinRange(level) + 15;
    }
}
