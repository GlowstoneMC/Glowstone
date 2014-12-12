package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class WaterWorker extends GlowEnchantment {
    WaterWorker() {
        super(6, "WATER_WORKER", "Aqua Affinity", 1, 2, EnchantmentTarget.ARMOR_HEAD);
    }

    @Override
    public int getMinRange(int level) {
        return 1;
    }

    @Override
    public int getMaxRange(int level) {
        return 41;
    }
}
