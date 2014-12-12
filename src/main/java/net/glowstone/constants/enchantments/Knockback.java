package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class Knockback extends GlowEnchantment {
    Knockback() {
        super(19, "KNOCKBACK", "Knockback", 2, 5, EnchantmentTarget.WEAPON);
    }

    @Override
    public int getMinRange(int level) {
        return 5 + 20 * (level - 1);
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMinRange(level) + 50;
    }
}
