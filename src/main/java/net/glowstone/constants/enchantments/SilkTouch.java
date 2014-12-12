package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class SilkTouch extends GlowEnchantment {
    SilkTouch() {
        super(33, "SILK_TOUCH", "Silk Touch", 1, 1, EnchantmentTarget.TOOL, DIGGING_TOOLS, Group.DIG);
    }

    @Override
    public int getMinRange(int level) {
        return 15;
    }

    @Override
    public int getMaxRange(int level) {
        return super.getMinRange(level) + 50;
    }
}
