package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.constants.GlowTree;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.material.Dirt;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DirtType;

public class BlockMushroom extends BlockNeedsAttached implements IBlockGrowable {

    private final Material mushroomType;

    public BlockMushroom(Material mushroomType) {
        this.mushroomType = mushroomType;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        GlowBlock belowBlock = block.getRelative(BlockFace.DOWN);
        Material type = belowBlock.getType();
        MaterialData data = belowBlock.getState().getData();
        if (type == Material.GRASS
            || data instanceof Dirt && ((Dirt) data).getType() != DirtType.PODZOL) {
            if (block.getLightLevel()
                < 13) { // checking light level for dirt, coarse dirt and grass
                return true;
            }
        } else if (type == Material.MYCEL
            || data instanceof Dirt && ((Dirt) data).getType() == DirtType.PODZOL) {
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
        return ThreadLocalRandom.current().nextFloat() < 0.4D;
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
        Location loc = block.getLocation();
        BlockStateDelegate blockStateDelegate = new BlockStateDelegate();
        if (GlowTree.newInstance(type, ThreadLocalRandom.current(), blockStateDelegate)
            .generate(loc)) {
            List<BlockState> blockStates = new ArrayList<>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent = new StructureGrowEvent(loc, type, true, player,
                blockStates);
            block.getEventFactory().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                for (BlockState state : blockStates) {
                    state.update(true);
                }
            }
        }
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (ThreadLocalRandom.current().nextInt(25) == 0) {
            GlowWorld world = block.getWorld();
            int x;
            int y;
            int z;
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

            int nx;
            int ny;
            int nz;
            nx = block.getX() + ThreadLocalRandom.current().nextInt(3) - 1;
            ny = block.getY() + ThreadLocalRandom.current().nextInt(2)
                    - ThreadLocalRandom.current().nextInt(2);
            nz = block.getZ() + ThreadLocalRandom.current().nextInt(3) - 1;

            x = block.getX();
            y = block.getY();
            z = block.getZ();
            for (i = 0; i < 4; i++) {
                if (world.getBlockAt(nx, ny, nz).getType() == Material.AIR
                    && canPlaceAt(world.getBlockAt(nx, ny, nz), BlockFace.DOWN)) {
                    x = nx;
                    y = ny;
                    z = nz;
                }
                nx = x + ThreadLocalRandom.current().nextInt(3) - 1;
                ny = y + ThreadLocalRandom.current().nextInt(2) - ThreadLocalRandom.current()
                    .nextInt(2);
                nz = z + ThreadLocalRandom.current().nextInt(3) - 1;
            }

            if (world.getBlockAt(nx, ny, nz).getType() == Material.AIR
                && canPlaceAt(world.getBlockAt(nx, ny, nz), BlockFace.DOWN)) {
                GlowBlockState state = world.getBlockAt(nx, ny, nz).getState();
                state.setType(mushroomType);
                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                block.getEventFactory().callEvent(spreadEvent);
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
