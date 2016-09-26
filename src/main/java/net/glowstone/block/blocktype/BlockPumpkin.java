package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Pumpkin;
import org.bukkit.util.Vector;

public class BlockPumpkin extends BlockDirectDrops {

    public BlockPumpkin() {
        super(Material.PUMPKIN, ToolType.AXE);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Pumpkin) {
            ((Pumpkin) data).setFacingDirection(player.getDirection());
            state.setData(data);
        } else {
            warnMaterialData(Pumpkin.class, data);
        }
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        super.afterPlace(player, block, holding, oldState);
        // Golems
        Location location = block.getLocation();
        if (!spawnIronGolem(location.clone())) {
            spawnSnowman(location.clone());
        }
    }

    private boolean spawnIronGolem(Location location) {
        Location[] blocks = new Location[]{location.clone().subtract(0, 1, 0), location.clone().subtract(0, 2, 0), null, null};
        if (blocks[0].getBlock().getType() != Material.IRON_BLOCK || blocks[1].getBlock().getType() != Material.IRON_BLOCK) {
            return false;
        }
        if ((location.clone().add(1, -1, 0).getBlock().getType() == Material.IRON_BLOCK && location.clone().add(-1, -1, 0).getBlock().getType() == Material.IRON_BLOCK)) {
            blocks[2] = location.clone().add(1, -1, 0);
            blocks[3] = location.clone().add(-1, -1, 0);
        } else if ((location.clone().add(0, -1, 1).getBlock().getType() == Material.IRON_BLOCK && location.clone().add(0, -1, -1).getBlock().getType() == Material.IRON_BLOCK)) {
            blocks[2] = location.clone().add(0, -1, 1);
            blocks[3] = location.clone().add(0, -1, -1);
        } else {
            return false;
        }
        for (Location b : blocks) {
            b.getBlock().setType(Material.AIR);
        }
        location.getBlock().setType(Material.AIR);
        location.getWorld().spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.IRON_GOLEM);
        return true;
    }

    private boolean spawnSnowman(Location location) {
        Location[] blocks = new Location[] {location, location.clone().subtract(0, 1, 0), location.clone().subtract(0, 2, 0)};
        if (blocks[1].getBlock().getType() != Material.SNOW_BLOCK || blocks[2].getBlock().getType() != Material.SNOW_BLOCK) {
            return false;
        }
        for (Location b : blocks) {
            b.getBlock().setType(Material.AIR);
        }
        location.getWorld().spawnEntity(location.clone().subtract(-0.5, 2, -0.5), EntityType.SNOWMAN);
        return true;
    }
}
