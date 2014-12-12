package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

abstract class WeaponDamage extends GlowEnchantment {
    protected WeaponDamage(int id, String name, String userName, int weight) {
        super(id, name, userName, 5, weight, EnchantmentTarget.WEAPON, SWORD_OR_AXE, Group.ATTACK);
    }

    @Override
    protected int getMaxRange(int level) {
        return getMinRange(level) + 20;
    }
}
