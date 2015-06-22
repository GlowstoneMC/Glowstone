package net.glowstone.block.blocktype;

import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
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
    public boolean canTickRandomly() {
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
            int j = 0;

            while (true) {
                // if there's available space
                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    final GlowBlock b = block.getRelative(BlockFace.UP);
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
                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();
                    x += random.nextInt(3) - 1;
                    y += (random.nextInt(3)) * random.nextInt(3) / 2;
                    z += random.nextInt(3) - 1;
                    if (world.getBlockAt(x, y, z).getType() == Material.GRASS) {
                        j++;
                        continue;
                    }
                }
                i++;
                break;
            }
        } while (i < 128);
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (block.getLightLevel() < 4 ||
                block.getRelative(BlockFace.UP).getMaterialValues().getLightOpacity() > 2) {
            // grass block turns into dirt block
            final GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        } else if (block.getLightLevel() >= 9) {
            final GlowWorld world = block.getWorld();
            int sourceX = block.getX();
            int sourceY = block.getY();
            int sourceZ = block.getZ();

            // grass spread randomly around
            for (int i = 0; i < 4; i++) {
                int x = sourceX + random.nextInt(3) - 1;
                int z = sourceZ + random.nextInt(3) - 1;
                int y = sourceY + random.nextInt(5) - 3;

                final GlowBlock targetBlock = world.getBlockAt(x, y, z);
                if (targetBlock.getType() == Material.DIRT &&
                        targetBlock.getData() == 0 && // only spread on normal dirt
                        targetBlock.getRelative(BlockFace.UP).getMaterialValues().getLightOpacity() <= 2 &&
                        targetBlock.getRelative(BlockFace.UP).getLightLevel() >= 4) {
                    final GlowBlockState state = targetBlock.getState();
                    state.setType(Material.GRASS);
                    state.setRawData((byte) 0);
                    BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                    EventFactory.callEvent(spreadEvent);
                    if (!spreadEvent.isCancelled()) {
                        state.update(true);
                    }
                }
            }
        }
    }
}
