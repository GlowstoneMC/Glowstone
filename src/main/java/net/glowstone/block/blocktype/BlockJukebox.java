package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TEJukebox;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowJukebox;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockJukebox extends BlockType {

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEJukebox(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        Jukebox jukebox = (Jukebox) block.getState();
        if (jukebox.isPlaying()) {
            jukebox.eject();
            jukebox.update();
            return true;
        }
        ItemStack handItem = player.getItemInHand();
        if (handItem != null && handItem.getType().isRecord()) {
            jukebox.setPlaying(handItem.getType());
            jukebox.update();
            if (player.getGameMode() != GameMode.CREATIVE) {
                handItem.setAmount(handItem.getAmount() - 1);
                player.setItemInHand(handItem);
            }
            return true;
        }
        return false;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        Jukebox jukebox = (Jukebox) block.getState();
        if (jukebox.eject()) {
            jukebox.update();
        }
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        ItemStack disk = ((GlowJukebox) block.getState()).getPlayingItem();
        if (disk == null) {
            return Collections.singletonList(new ItemStack(block.getType()));
        } else {
            return Arrays.asList(new ItemStack(block.getType()), disk);
        }
    }
}
