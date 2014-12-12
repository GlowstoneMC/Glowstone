package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class Thorns extends GlowEnchantment {
    Thorns() {
        super(7, "THORNS", "Thorns", 3, 1, EnchantmentTarget.ARMOR_TORSO, new MatcherAdapter(EnchantmentTarget.ARMOR));
    }

    @Override
    public int getMinRange(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMinRange(level) + 50;
    }
}
