package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemTimedUsage extends ItemType {

    @Override
    public Context getContext() {
        return Context.ANY;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        startUse(player, holding);
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        startUse(player, holding);
    }

    public void startUse(GlowPlayer player, ItemStack item) {

    }

    public void endUse(GlowPlayer player, ItemStack item) {

    }
}
