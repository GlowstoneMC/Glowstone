package net.glowstone.block.itemtype;
import org.bukkit.Location;

import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowEnderPearl;

public class ItemEnderPearl extends ItemType {
    
    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        throwEnderPearl(player);
    }
    
    @Override
    public Context getContext() {
        return Context.ANY;
    }
    
    private void throwEnderPearl(GlowPlayer player) {
        Location throwLoc = player.getLocation().clone();
        new GlowEnderPearl(throwLoc).setShooter(player);
    }
}
