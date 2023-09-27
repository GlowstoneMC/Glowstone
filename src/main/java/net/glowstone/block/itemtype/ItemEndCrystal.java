package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemEndCrystal extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (target == null || (target.getType() != Material.BEDROCK
            && target.getType() != Material.OBSIDIAN)) {
            return;
        }

        Location location = target.getRelative(BlockFace.UP).getLocation();
        // Spawn the crystal in the center of the block
        location.add(0.5, 0, 0.5);
        EnderCrystal crystal = player.getWorld().spawn(location, EnderCrystal.class);
        // "Defaults to false when placing by hand [..]
        // http://minecraft.wiki/w/End_Crystal#Data_values
        crystal.setShowingBottom(false);

        super.rightClickBlock(player, target, face, holding, clickedLoc, hand);
    }
}
