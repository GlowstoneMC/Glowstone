package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEFlowerPot;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowFlowerPot;
import net.glowstone.entity.GlowPlayer;

public class BlockFlowerPot extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block) {
        List<ItemStack> drops = Arrays.asList(new ItemStack(Material.FLOWER_POT));
        GlowBlockState state = block.getState();

        if (state instanceof GlowFlowerPot) {
            MaterialData contents = ((GlowFlowerPot) state).getContents();

            if (contents != null) {
                drops.add(new ItemStack(contents.getItemType(), 1, contents.getData()));
            }
        }
        return Collections.unmodifiableList(drops);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEFlowerPot(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (!(data instanceof FlowerPot)) {
            warnMaterialData(FlowerPot.class, data);
            return false;
        }
        if (state instanceof GlowFlowerPot) {
            GlowFlowerPot pot = (GlowFlowerPot) state;
            // Only change contents if there is none.
            if (pot.getContents() == null) {
                // Todo: check if the item is valid (null-check too) as flower pot contents.
                pot.setContents(player.getItemInHand().getData().clone());
                pot.update();
                return true;
            }
        }
        return false;
    }
}
