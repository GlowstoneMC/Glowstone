package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

abstract class Protection extends GlowEnchantment {
    protected Protection(int id, String name, String userName, int weight) {
        this(id, name, userName, weight, EnchantmentTarget.ARMOR);
    }

    protected Protection(int id, String name, String userName, int weight, EnchantmentTarget target) {
        super(id, name, userName, 4, weight, target, Group.PROTECT);
    }
}
