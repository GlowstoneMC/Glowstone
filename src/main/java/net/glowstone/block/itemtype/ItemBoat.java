package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemBoat extends ItemType {

    private final TreeSpecies woodType;

    public ItemBoat(TreeSpecies woodType) {
        this.woodType = woodType;
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (target == null) {
            return;
        }
        Location location = target.getRelative(BlockFace.UP).getLocation().clone();
        location.setYaw(player.getLocation().getYaw());
        Boat spawn = target.getWorld().spawn(location, Boat.class);
        spawn.setWoodType(woodType);
        super.rightClickBlock(player, target, face, holding, clickedLoc, hand);
    }
}
