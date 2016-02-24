package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.constants.GlowTree;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlockSapling extends BlockNeedsAttached implements IBlockGrowable {

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
        return (double) random.nextFloat() < 0.45D;
    }

    @Override
    public void grow(GlowPlayer player, GlowBlock block) {
        growSapling(block, player);
    }

    private void growSapling(GlowBlock block, GlowPlayer player) {

        // get data but filters sapling age
        final int data = block.getData() & 0x7;

        if (data == TreeSpecies.GENERIC.ordinal()) {
            // 1 chance on 10 to grow a big oak tree
            if (random.nextInt(10) > 0) {
                generateTree(TreeType.TREE, block, player);
            } else {
                generateTree(TreeType.BIG_TREE, block, player);
            }
        } else if (data ==  TreeSpecies.REDWOOD.ordinal()) {
            // check saplings around to grow a mega redwood
            final GlowBlock hugeTreeBlock = searchSourceBlockForHugeTree(block);
            if (hugeTreeBlock == null) {
                generateTree(TreeType.REDWOOD, block, player);
            } else {
                generateTree(TreeType.MEGA_REDWOOD, hugeTreeBlock, player);
            }
        } else if (data ==  TreeSpecies.BIRCH.ordinal()) {
            generateTree(TreeType.BIRCH, block, player);
        } else if (data ==  TreeSpecies.JUNGLE.ordinal()) {
            // check saplings around to grow a mega jungle tree
            final GlowBlock hugeTreeBlock = searchSourceBlockForHugeTree(block);
            if (hugeTreeBlock == null) {
                generateTree(TreeType.SMALL_JUNGLE, block, player);
            } else {
                generateTree(TreeType.JUNGLE, hugeTreeBlock, player);
            }
        } else if (data ==  TreeSpecies.ACACIA.ordinal()) {
            generateTree(TreeType.ACACIA, block, player);
        } else if (data ==  TreeSpecies.DARK_OAK.ordinal()) {
            // check saplings around to grow a dark oak tree
            final GlowBlock hugeTreeBlock = searchSourceBlockForHugeTree(block);
            if (hugeTreeBlock != null) {
                generateTree(TreeType.DARK_OAK, hugeTreeBlock, player);
            }
        }
    }

    private void generateTree(TreeType type, GlowBlock block, GlowPlayer player) {

        // get data but filters sapling age
        final int data = block.getData() & 0x7;

        // replaces the sapling block(s)
        block.setType(Material.AIR);
        if (type == TreeType.JUNGLE || type == TreeType.MEGA_REDWOOD || type == TreeType.DARK_OAK) {
            block.getRelative(BlockFace.SOUTH).setType(Material.AIR);
            block.getRelative(BlockFace.EAST).setType(Material.AIR);
            block.getRelative(BlockFace.SOUTH_EAST).setType(Material.AIR);
        }

        // try to generate a tree
        final Location loc = block.getLocation();
        final BlockStateDelegate blockStateDelegate = new BlockStateDelegate();
        boolean canGrow = false;
        if (GlowTree.newInstance(type, random, loc, blockStateDelegate).generate()) {
            final List<BlockState> blockStates = new ArrayList<>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent =
                    new StructureGrowEvent(loc, type, player == null ? false : true, player, blockStates);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                canGrow = true;
                for (BlockState state : blockStates) {
                    state.update(true);
                }
            }
        }

        if (!canGrow) {
            // places the sapling block(s) back if the tree was not generated
            // the sapling ages are overwritten but this is an expected
            // vanilla behavior
            block.setType(Material.SAPLING);
            block.setData((byte) data);
            if (type == TreeType.JUNGLE || type == TreeType.MEGA_REDWOOD || type == TreeType.DARK_OAK) {
                block.getRelative(BlockFace.SOUTH).setType(Material.SAPLING);
                block.getRelative(BlockFace.SOUTH).setData((byte) data);
                block.getRelative(BlockFace.EAST).setType(Material.SAPLING);
                block.getRelative(BlockFace.EAST).setData((byte) data);
                block.getRelative(BlockFace.SOUTH_EAST).setType(Material.SAPLING);
                block.getRelative(BlockFace.SOUTH_EAST).setData((byte) data);
            }
        }
    }

    private GlowBlock searchSourceBlockForHugeTree(GlowBlock block) {

        final GlowWorld world = block.getWorld();
        final int sourceX = block.getX();
        final int sourceY = block.getY();
        final int sourceZ = block.getZ();
        final int data = block.getData();
        for (int x = -1; x <= 0; x++) {
            for (int z = -1; z <= 0; z++) {
                final GlowBlock b = world.getBlockAt(sourceX + x, sourceY, sourceZ + z);
                if (b.getType() == Material.SAPLING && b.getData() == data &&
                        b.getRelative(BlockFace.SOUTH).getType() == Material.SAPLING &&
                        b.getRelative(BlockFace.SOUTH).getData() == data &&
                        b.getRelative(BlockFace.EAST).getType() == Material.SAPLING &&
                        b.getRelative(BlockFace.EAST).getData() == data &&
                        b.getRelative(BlockFace.SOUTH_EAST).getType() == Material.SAPLING &&
                        b.getRelative(BlockFace.SOUTH_EAST).getData() == data) {

                    return b;
                }
            }
        }

        return null;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Arrays.asList(new ItemStack(Material.SAPLING, 1, (short) (block.getData() % 8)));
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (block.getRelative(BlockFace.UP).getLightLevel() >= 9 && random.nextInt(7) == 0) {
            int dataValue = block.getData();
            if ((dataValue & 8) == 0) {
                block.setData((byte) (dataValue | 8));
            } else {
                final MaterialData data = block.getState().getData();
                if (data instanceof Tree) {
                    final Tree tree = (Tree) data;
                    final TreeType type = getTreeType(tree.getSpecies());
                    block.setType(Material.AIR);
                    final int saplingData = block.getData() & 0x7;
                    if (!block.getWorld().generateTree(block.getLocation(), type)) {
                        block.setType(Material.SAPLING);
                        block.setData((byte) saplingData);
                    }
                } else {
                    warnMaterialData(Tree.class, data);
                }
            }
        }
    }

    private TreeType getTreeType(TreeSpecies species) {
        switch (species) {
            case GENERIC:
                return TreeType.TREE;
            case REDWOOD:
                return TreeType.REDWOOD;
            case BIRCH:
                return TreeType.BIRCH;
            case JUNGLE:
                return TreeType.JUNGLE;
            case ACACIA:
                return TreeType.ACACIA;
            case DARK_OAK:
                return TreeType.DARK_OAK;
            default:
                return TreeType.TREE;
        }
    }
}
