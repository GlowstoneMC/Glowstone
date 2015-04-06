package net.glowstone.block.blocktype;

import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.LongGrass;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;

public class BlockGrass extends BlockType implements IBlockGrowable {

    public BlockGrass() {
        setDrops(new ItemStack(Material.DIRT, 1));
    }

    @Override
    public boolean isFertilizable(GlowBlock block) {
        return true;
    }

    @Override
    public boolean canGrowWithChance(GlowBlock block) {
        return true;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        final GlowWorld world = block.getWorld();

        int i = 0;
        do {
            int x = block.getX();
            int y = block.getY() + 1;
            int z = block.getZ();
            int j = 0;

            while (true) {
                // if there's available space
                if (world.getBlockAt(x, y, z).getType() == Material.AIR) {
                    final GlowBlock b = world.getBlockAt(x, y, z);
                    final GlowBlockState blockState = b.getState();
                    if (random.nextFloat() < 0.125D) {
                        // sometimes grow random flower
                        // would be better to call a method that choose a random 
                        // flower depending on the biome
                        Material flower;
                        if (random.nextInt(2) == 0) {
                            flower = Material.RED_ROSE;
                        } else {
                            flower = Material.YELLOW_FLOWER;
                        }
                        if (ItemTable.instance().getBlock(flower).canPlaceAt(b, BlockFace.DOWN)) {
                            blockState.setType(flower);
                        }
                    } else {
                        final Material tallGrass = Material.LONG_GRASS;
                        if (ItemTable.instance().getBlock(tallGrass).canPlaceAt(b, BlockFace.DOWN)) {
                            // grow tall grass if possible
                            blockState.setType(tallGrass);
                            blockState.setData(new LongGrass(GrassSpecies.NORMAL));
                        }
                    }
                    BlockGrowEvent growEvent = new BlockGrowEvent(b, blockState);
                    EventFactory.callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        blockState.update(true);
                    }
                } else if (j < i / 16) { // look around for grass block
                    x += random.nextInt(3) - 1;
                    y += (random.nextInt(3) - 1) * random.nextInt(3) / 2;
                    z += random.nextInt(3) - 1;
                    if (world.getBlockAt(x, y - 1, z).getType() == Material.GRASS) {
                        j++;
                        continue;
                    }
                }
                i++;
                break;
            }
        } while (i < 128);
    }
}
