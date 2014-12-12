package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class FireAspect extends GlowEnchantment {
    FireAspect() {
        super(20, "FIRE_ASPECT", "Fire Aspect", 2, 2, EnchantmentTarget.WEAPON);
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
