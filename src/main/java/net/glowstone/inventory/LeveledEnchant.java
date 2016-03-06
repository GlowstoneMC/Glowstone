package net.glowstone.inventory;

import lombok.Data;
import net.glowstone.constants.GlowEnchantment;
import net.glowstone.util.WeightedRandom;
import net.glowstone.util.WeightedRandom.Choice;
import org.bukkit.enchantments.Enchantment;

@Data
public class LeveledEnchant implements Choice {
    private final Enchantment enchantment;
    private final int level;

    @Override
    public int getWeight() {
        return ((GlowEnchantment) enchantment).getWeight();
    }
}
