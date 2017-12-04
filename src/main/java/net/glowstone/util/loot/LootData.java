package net.glowstone.util.loot;

import java.util.Collection;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class LootData {

    private final Collection<ItemStack> items;
    private final int experience;

}
