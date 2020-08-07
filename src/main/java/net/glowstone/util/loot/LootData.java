package net.glowstone.util.loot;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

@Data
public class LootData {

    private final Collection<ItemStack> items;
    private final int experience;

}
