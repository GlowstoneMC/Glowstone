package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.JukeboxEntity;
import net.glowstone.block.entity.state.GlowJukebox;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class BlockJukebox extends BlockType {

    /**
     * Creates a block type with jukebox functionality.
     */
    public BlockJukebox() {
        super();
        addFunction(Functions.Interact.JUKEBOX);
        addFunction(Functions.Destroy.JUKEBOX);
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new JukeboxEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        ItemStack disk = ((GlowJukebox) block.getState()).getPlayingItem();
        if (disk == null) {
            return Arrays.asList(new ItemStack(block.getType()));
        } else {
            return Arrays.asList(new ItemStack(block.getType()), disk);
        }
    }
}
