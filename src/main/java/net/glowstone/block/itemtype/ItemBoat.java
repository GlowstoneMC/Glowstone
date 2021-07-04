package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Set;

public class ItemBoat extends ItemType {

    private final TreeSpecies woodType;

    public ItemBoat(TreeSpecies woodType) {
        this.woodType = woodType;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        // Executed when the player clicks on water that doesn't have a block beneath
        placeBoat(player, holding);
    }

    @Override
    public void rightClickBlock(
            GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding,
            Vector clickedLoc, EquipmentSlot hand) {
        // Two cases are handled here: Either the player clicked on a block on the land or beneath
        // water
        placeBoat(player, holding);
    }

    private void placeBoat(GlowPlayer player, ItemStack holding) {
        Block targetBlock = player.getTargetBlock((Set<Material>) null, 5);

        if (targetBlock != null && !targetBlock.isEmpty()
                && targetBlock.getRelative(BlockFace.UP).isEmpty()) {
            Location location = targetBlock.getRelative(BlockFace.UP).getLocation();
            // center boat on cursor location
            location.add(0.6875f, 0, 0.6875f);
            location.setYaw(player.getLocation().getYaw());
            Boat boat = targetBlock.getWorld().spawn(location, Boat.class);
            boat.setWoodType(woodType);
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getInventory().removeItem(holding);
            }
        }
    }

    @Override
    public Context getContext() {
        return Context.ANY;
    }
}
