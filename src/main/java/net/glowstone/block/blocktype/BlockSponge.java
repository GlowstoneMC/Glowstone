package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TaxicabBlockIterator;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockSponge extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
                           ItemStack holding, Vector clickedLoc) {
        // TODO: Move this to a new method when physics works and run this on neighbour change too.

        MaterialData data = holding.getData();

        boolean absorbedWater = false;

        if (data.getItemType() == Material.SPONGE) {
            GlowBlock block = state.getBlock();

            TaxicabBlockIterator iterator = new TaxicabBlockIterator(block);
            iterator.setMaxDistance(7);
            iterator.setMaxBlocks(66);
            iterator.setPredicate(b -> b.getType() == Material.WATER);

            if (iterator.hasNext()) {
                absorbedWater = true;
                do {
                    iterator.next().setType(Material.AIR);
                } while (iterator.hasNext());
            }
        }

        state.setType(absorbedWater ? Material.WET_SPONGE : Material.SPONGE);
    }
}
