package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class FallProtection extends Protection {
    FallProtection() {
        super(2, "PROTECTION_FALL", "Feather Falling", 5, EnchantmentTarget.ARMOR_FEET);
    }

    @Override
    protected int getMinRange(int level) {
        return 5 + (level - 1) * 6;
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 10;
    }
}
