package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class Oxygen extends GlowEnchantment {
    Oxygen() {
        super(5, "OXYGEN", "Respiration", 3, 2, EnchantmentTarget.ARMOR_HEAD);
    }

    @Override
    public int getMinRange(int level) {
        return 10 * level;
    }

    @Override
    public int getMaxRange(int level) {
        return getMinRange(level) + 30;
    }
}
