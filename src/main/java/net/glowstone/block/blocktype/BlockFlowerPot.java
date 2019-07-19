package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.FlowerPotEntity;
import net.glowstone.block.entity.state.GlowFlowerPot;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BlockFlowerPot extends BlockType {

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        List<ItemStack> drops = new LinkedList<>(Arrays.asList(new ItemStack(Material.FLOWER_POT)));
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
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new FlowerPotEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        GlowBlockState state = block.getState();
        MaterialData data = state.getData();

        if (!(data instanceof FlowerPot)) {
            warnMaterialData(FlowerPot.class, data);
            return false;
        }
        if (state instanceof GlowFlowerPot) {
            GlowFlowerPot pot = (GlowFlowerPot) state;
            ItemStack heldItem = player.getItemInHand();
            // Only change contents if there is none and if the held item is valid pot contents.
            if (pot.getContents() == null && heldItem != null && isValidContents(
                heldItem.getData())) {
                pot.setContents(heldItem.getData().clone()); // Null-check in isValidContents.
                return pot.update();
            }
        }
        return false;
    }

    private boolean isValidContents(MaterialData contents) {
        if (contents == null) {
            return false;
        }

        switch (contents.getItemType()) {
            case DEAD_BUSH:
            case AZURE_BLUET:
            case DANDELION:
            case RED_MUSHROOM:
            case BROWN_MUSHROOM:
            case CACTUS:
            // TODO: sapling types
            case LEGACY_SAPLING:
            case LARGE_FERN:
                return true;
            default:
                return false;
        }
    }
}
