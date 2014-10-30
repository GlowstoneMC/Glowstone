package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.generator.TreeGenerator;
import net.glowstone.util.BlockStateDelegate;

public class BlockMushroom extends BlockType implements IBlockGrowable {

    private final Material mushroomType;

    public BlockMushroom(Material mushroomType) {
        this.mushroomType = mushroomType;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        final GlowBlock belowBlock = block.getRelative(BlockFace.DOWN);
        final Material type = belowBlock.getType();
        if (type == Material.GRASS ||
                (type == Material.DIRT && belowBlock.getData() != 2)) {
            if (block.getLightLevel() < 13) { // checking light level for dirt, coarse dirt and grass
                return true;
            }
        } else if (type == Material.MYCEL ||
                (type == Material.DIRT && belowBlock.getData() == 2)) {
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
        final TreeGenerator generator = new TreeGenerator(blockStateDelegate);
        if (generator.generate(random, loc, type)) {
            final List<BlockState> blockStates = new ArrayList<BlockState>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent = new StructureGrowEvent(loc, type, true, player, blockStates);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                for (BlockState state : blockStates) {
                    state.update(true);
                }
            }
        }
    }
}
