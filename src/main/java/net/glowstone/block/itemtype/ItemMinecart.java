package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowMinecart;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Rails;
import org.bukkit.util.Vector;

public class ItemMinecart extends ItemType {

    private final GlowMinecart.MinecartType minecartType;

    public ItemMinecart(GlowMinecart.MinecartType minecartType) {
        this.minecartType = minecartType;
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        // TODO: 1.13: catch all rail types
        if (target == null || target.getType() != Material.RAIL) {
            return;
        }
        if (minecartType.getMinecartClass() == null) {
            player.sendMessage(
                ChatColor.RED + "Minecart type '" + minecartType.getEntityType().getName()
                    + "' is not implemented.");
            return;
        }
        Rails rails = (Rails) target.getState().getData();
        Location location = target.getLocation().clone()
            .add(Math.abs(rails.getDirection().getModX()) * 0.5, 0.1,
                Math.abs(rails.getDirection().getModZ()) * 0.5);
        location.setYaw(getYaw(rails.getDirection()));
        target.getWorld().spawn(location, minecartType.getEntityClass());
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.getInventory().remove(holding);
        }
        super.rightClickBlock(player, target, face, holding, clickedLoc, hand);
    }

    private float getYaw(BlockFace face) {
        switch (face) {
            case EAST:
                return -90f;
            case NORTH:
                return -180f;
            case WEST:
                return 90f;
            case SOUTH:
            default:
                return 0f;
        }
    }
}
