package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class Lure extends GlowEnchantment {
    Lure() {
        super(62, "LURE", "Lure", 3, 2, EnchantmentTarget.FISHING_ROD);
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
