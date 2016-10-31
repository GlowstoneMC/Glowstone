package net.glowstone.util.loot;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class LootData {

    private final ItemStack[] items;
    private final int experience;

}
