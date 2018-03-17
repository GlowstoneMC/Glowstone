package net.glowstone.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

public class GlowLlamaInventory extends GlowInventory implements LlamaInventory {
    @Getter
    @Setter
    protected ItemStack decor;
    @Getter
    @Setter
    protected ItemStack saddle;
}
