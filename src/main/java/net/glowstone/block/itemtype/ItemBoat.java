package net.glowstone.block.itemtype;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.ItemStack;

public class ItemBoat extends ItemType {

    private final TreeSpecies woodType;

    public ItemBoat(TreeSpecies woodType) {
        this.woodType = woodType;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        List<Block> lastTwoTargetBlocks = player.getLastTwoTargetBlocks((HashSet<Material>) null, 5);
        Optional<Block> first = lastTwoTargetBlocks.stream().filter(b -> b.getType() != Material.AIR).findFirst();

        if (first.isPresent()) {
            Block block = first.get();
            Location location = block.getRelative(BlockFace.UP).getLocation();
            // center boat on cursor location
            location.add(0.6875f, 0, 0.6875f);
            location.setYaw(player.getLocation().getYaw());
            Boat boat = block.getWorld().spawn(location, Boat.class);
            boat.setWoodType(woodType);
        }
    }

    @Override
    public Context getContext() {
        return Context.AIR;
    }
}
