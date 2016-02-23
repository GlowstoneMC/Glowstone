package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.constants.GlowTree;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.DirtType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Dirt;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class BlockMushroom extends BlockNeedsAttached implements IBlockGrowable {

    private final Material mushroomType;

    public BlockMushroom(Material mushroomType) {
        this.mushroomType = mushroomType;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        final GlowBlock belowBlock = block.getRelative(BlockFace.DOWN);
        final Material type = belowBlock.getType();
        final MaterialData data = belowBlock.getState().getData();
        if (type == Material.GRASS || (data instanceof Dirt && ((Dirt) data).getType() != DirtType.PODZOL)) {
            if (block.getLightLevel() < 13) { // checking light level for dirt, coarse dirt and grass
                return true;
            }
        } else if (type == Material.MYCEL || (data instanceof Dirt && ((Dirt) data).getType() == DirtType.PODZOL)) {
            // not checking light level if mycel or podzol
            return true;
        }
        return false;
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
        return (double) random.nextFloat() < 0.4D;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        TreeType type;
        if (mushroomType == Material.BROWN_MUSHROOM) {
            type = TreeType.BROWN_MUSHROOM;
        } else if (mushroomType == Material.RED_MUSHROOM) {
            type = TreeType.RED_MUSHROOM;
        } else {
            return;
        }
        final Location loc = block.getLocation();
        final BlockStateDelegate blockStateDelegate = new BlockStateDelegate();
        if (GlowTree.newInstance(type, random, loc, blockStateDelegate).generate()) {
            final List<BlockState> blockStates = new ArrayList<>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent = new StructureGrowEvent(loc, type, true, player, blockStates);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                for (BlockState state : blockStates) {
                    state.update(true);
                }
            }
        }
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (random.nextInt(25) == 0) {
            final GlowWorld world = block.getWorld();
            int x, y, z;
            int i = 0;
            for (x = block.getX() - 4; x <= block.getX() + 4; x++) {
                for (z = block.getZ() - 4; z <= block.getZ() + 4; z++) {
                    for (y = block.getY() - 1; y <= block.getY() + 1; y++) {
                        if (world.getBlockAt(x, y, z).getType() == mushroomType) {
                            if (++i > 4) {
                                return;
                            }
                        }
                    }
                }
            }

            int nX, nY, nZ;
            nX = block.getX() + random.nextInt(3) - 1;
            nY = block.getY() + random.nextInt(2) - random.nextInt(2);
            nZ = block.getZ() + random.nextInt(3) - 1;

            x = block.getX(); y = block.getY(); z = block.getZ();
            for (i = 0; i < 4; i++) {
                if (world.getBlockAt(nX, nY, nZ).getType() == Material.AIR
                        && canPlaceAt(world.getBlockAt(nX, nY, nZ), BlockFace.DOWN)) {
                    x = nX; y = nY; z = nZ;
                }
                nX = x + random.nextInt(3) - 1;
                nY = y + random.nextInt(2) - random.nextInt(2);
                nZ = z + random.nextInt(3) - 1;
            }

            if (world.getBlockAt(nX, nY, nZ).getType() == Material.AIR
                    && canPlaceAt(world.getBlockAt(nX, nY, nZ), BlockFace.DOWN)) {
                final GlowBlockState state = world.getBlockAt(nX, nY, nZ).getState();
                state.setType(mushroomType);
                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                EventFactory.callEvent(spreadEvent);
                if (!spreadEvent.isCancelled()) {
                    state.update(true);
                }
            }
        }

        // mushroom does not uproot in vanilla due to a bug, but it should uproot as
        // it is stated in the wiki
        if (!canPlaceAt(block, BlockFace.DOWN)) {
            block.breakNaturally();
        }
    }
}
