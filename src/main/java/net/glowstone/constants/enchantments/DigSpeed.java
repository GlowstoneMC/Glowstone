package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class DigSpeed extends GlowEnchantment {
    DigSpeed() {
        super(32, "DIG_SPEED", "Efficiency", 5, 10, EnchantmentTarget.TOOL, DIGGING_TOOLS);
    }

    @Override
    public int getMinRange(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMinRange(level) + 50;
    }
}
