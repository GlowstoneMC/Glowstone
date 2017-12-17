package net.glowstone.inventory;

import net.glowstone.constants.GlowEnchantment;
import net.glowstone.util.WeightedRandom.Choice;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;

public class LeveledEnchant extends EnchantmentOffer implements Choice {

    public LeveledEnchant(Enchantment enchantment, int enchantmentLevel, int cost) {
        super(enchantment, enchantmentLevel, cost);
    }

    @Override
    public int getWeight() {
        return ((GlowEnchantment) getEnchantment()).getWeight();
    }
}
