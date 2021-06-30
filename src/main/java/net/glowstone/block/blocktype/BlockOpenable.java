package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Openable;
import org.bukkit.util.Vector;

/**
 * Represents blocks that can be opened through a right-click. A block can be opened if its
 * {@link MaterialData} implements {@link Openable}.
 */
public class BlockOpenable extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        GlowBlockState blockState = block.getState();
        MaterialData materialData = blockState.getData();
        if (materialData instanceof Openable) {
            Openable toOpen = (Openable) materialData;
            boolean wasOpen = toOpen.isOpen();
            toOpen.setOpen(!wasOpen);

            if (wasOpen) {
                onClosed(player, block, face, clickedLoc, blockState, materialData);
            } else {
                onOpened(player, block, face, clickedLoc, blockState, materialData);
            }

            blockState.update(true);
            return true;
        } else {
            warnMaterialData(Openable.class, materialData);
            return false;
        }
    }

    protected void onOpened(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc,
                            GlowBlockState state, MaterialData materialData) {
        // Can be overridden
    }

    protected void onClosed(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc,
                            GlowBlockState state, MaterialData materialData) {
        // Can be overridden
    }
}
