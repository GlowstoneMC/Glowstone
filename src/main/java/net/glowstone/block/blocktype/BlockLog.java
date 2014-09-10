package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

public class BlockLog extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Tree) {
            ((Tree) data).setDirection(face);
            ((Tree) data).setSpecies(TreeSpecies.getByData((byte) holding.getDurability()));
        } else {
            warnMaterialData(Tree.class, data);
        }
        state.setData(data);
    }
}
