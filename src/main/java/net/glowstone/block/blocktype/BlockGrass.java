package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.generator.objects.FlowerType;
import net.glowstone.generator.populators.overworld.FlowerForestPopulator;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.LongGrass;

import java.util.concurrent.ThreadLocalRandom;

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
        GlowWorld world = block.getWorld();

        int i = 0;
        do {
            int j = 0;

            while (true) {
                // if there's available space
                if (block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                    GlowBlock b = block.getRelative(BlockFace.UP);
                    GlowBlockState blockState = b.getState();
                    if (ThreadLocalRandom.current().nextFloat() < 0.125D) {
                        // sometimes grow random flower
                        // would be better to call a method that choose a random
                        // flower depending on the biome
                        FlowerType[] flowers = FlowerForestPopulator.FLOWERS;
                        Material flower = flowers[ThreadLocalRandom.current()
                            .nextInt(flowers.length)].getType();
                        if (ItemTable.instance().getBlock(flower)
                            .canPlaceAt(null, b, BlockFace.DOWN)) {
                            blockState.setType(flower);
                        }
                    } else {
                        Material tallGrass = Material.TALL_GRASS;
                        if (ItemTable.instance().getBlock(tallGrass)
                            .canPlaceAt(null, b, BlockFace.DOWN)) {
                            // grow tall grass if possible
                            blockState.setType(tallGrass);
                            blockState.setData(new LongGrass(GrassSpecies.NORMAL));
                        }
                    }
                    BlockGrowEvent growEvent = new BlockGrowEvent(b, blockState);
                    EventFactory.getInstance().callEvent(growEvent);
                    if (!growEvent.isCancelled()) {
                        blockState.update(true);
                    }
                } else if (j < i / 16) { // look around for grass block
                    int x = block.getX();
                    int y = block.getY();
                    int z = block.getZ();
                    x += ThreadLocalRandom.current().nextInt(3) - 1;
                    y += ThreadLocalRandom.current().nextInt(3) * ThreadLocalRandom.current()
                        .nextInt(3) / 2;
                    z += ThreadLocalRandom.current().nextInt(3) - 1;
                    if (world.getBlockAt(x, y, z).getType() == Material.GRASS_BLOCK) {
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
        GlowBlock blockAbove = block.getRelative(BlockFace.UP);
        if (blockAbove.getLightLevel() < 4
            && blockAbove.getMaterialValues().getLightOpacity() > 2) {
            // grass block turns into dirt block
            GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.getInstance().callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        } else if (blockAbove.getLightLevel() >= 9) {
            GlowWorld world = block.getWorld();
            int sourceX = block.getX();
            int sourceY = block.getY();
            int sourceZ = block.getZ();

            // grass spread randomly around
            for (int i = 0; i < 4; i++) {
                int x = sourceX + ThreadLocalRandom.current().nextInt(3) - 1;
                int z = sourceZ + ThreadLocalRandom.current().nextInt(3) - 1;
                int y = sourceY + ThreadLocalRandom.current().nextInt(5) - 3;

                GlowBlock targetBlock = world.getBlockAt(x, y, z);
                GlowBlock targetAbove = targetBlock.getRelative(BlockFace.UP);
                if (targetBlock.getChunk().isLoaded() && targetAbove.getChunk().isLoaded()
                    && targetBlock.getType() == Material.DIRT
                    && targetBlock.getData() == 0 // only spread on normal dirt
                    && targetAbove.getMaterialValues().getLightOpacity() <= 2
                    && targetAbove.getLightLevel() >= 4) {
                    GlowBlockState state = targetBlock.getState();
                    state.setType(Material.GRASS_BLOCK);
                    state.setRawData((byte) 0);
                    BlockSpreadEvent spreadEvent = new BlockSpreadEvent(targetBlock, block, state);
                    EventFactory.getInstance().callEvent(spreadEvent);
                    if (!spreadEvent.isCancelled()) {
                        state.update(true);
                    }
                }
            }
        }
    }
}
