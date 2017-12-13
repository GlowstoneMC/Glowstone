package net.glowstone.block.itemtype;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

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
        throwLoc.setY(throwLoc.getY() + 1.5);
        new GlowEnderPearl(throwLoc, 2).setShooter(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERPEARL_THROW, 3, 1);
    }
}