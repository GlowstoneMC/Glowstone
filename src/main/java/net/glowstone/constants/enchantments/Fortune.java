package net.glowstone.constants.enchantments;

import org.bukkit.enchantments.EnchantmentTarget;

final class Fortune extends LootBonus {
    Fortune() {
        super(35, "LOOT_BONUS_BLOCKS", "Fortune", EnchantmentTarget.TOOL, BASE_TOOLS, Group.DIG);
    }
}
