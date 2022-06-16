package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.JukeboxEntity;
import net.glowstone.block.entity.state.GlowJukebox;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class BlockJukebox extends BlockType {

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new JukeboxEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
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

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(@NotNull GlowBlock block, ItemStack tool) {
        ItemStack disk = ((GlowJukebox) block.getState()).getRecord();
        if (disk == null) {
            return Arrays.asList(new ItemStack(block.getType()));
        } else {
            return Arrays.asList(new ItemStack(block.getType()), disk);
        }
    }
}
