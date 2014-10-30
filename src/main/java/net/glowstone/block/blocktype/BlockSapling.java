package net.glowstone.block.blocktype;

import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;

import net.glowstone.block.GlowBlock;

public class BlockSapling extends BlockType {

    @Override
    public boolean canTickRandomly() {
        return true;
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
