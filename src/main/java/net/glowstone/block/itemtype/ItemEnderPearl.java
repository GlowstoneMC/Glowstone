package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemEnderPearl extends ItemType {
    private static final int ENDER_PEARL_COOLDOWN_TICKS = 20;

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        throwEnderPearl(player, holding);
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        throwEnderPearl(player, holding);
    }

    @Override
    public Context getContext() {
        return Context.ANY;
    }

    private void throwEnderPearl(GlowPlayer player, ItemStack holding) {
        if (player.getEnderPearlCooldown() == 0) {
            if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                holding.setAmount(holding.getAmount() - 1);
            }
            throwEnderPearl(player);
        }
    }

    private void throwEnderPearl(GlowPlayer player) {
        Location throwLoc = player.getLocation();
        throwLoc.setY(throwLoc.getY() + 1.5);
        player.launchProjectile(EnderPearl.class);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERPEARL_THROW, 3, 1);
        player.setEnderPearlCooldown(ENDER_PEARL_COOLDOWN_TICKS);
    }
}
