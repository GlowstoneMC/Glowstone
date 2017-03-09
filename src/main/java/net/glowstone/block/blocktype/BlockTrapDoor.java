package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.TrapDoor;
import org.bukkit.util.Vector;

/**
 * Helper for trapdoor blocks.
 */
public class BlockTrapDoor {
    private BlockType parent;

    public BlockTrapDoor(BlockType parent) {
        this.parent = parent;
    }

    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        MaterialData materialData = state.getData();
        if (materialData instanceof TrapDoor) {
            TrapDoor trapDoor = (TrapDoor) materialData;
            trapDoor.setFacingDirection(face);
            if (clickedLoc.getY() >= 0.5) {
                trapDoor.setInverted(true);
            } else {
                trapDoor.setInverted(false);
            }
            state.update(true);
        } else {
            parent.warnMaterialData(TrapDoor.class, materialData);
        }
    }

    public void onRedstoneUpdate(GlowBlock block) {
        GlowBlockState state = block.getState();
        TrapDoor trapdoor = (TrapDoor) state.getData();
        boolean powered = block.isBlockIndirectlyPowered();
        if (powered != trapdoor.isOpen()) {
            trapdoor.setOpen(powered);
            state.update();
        }
    }
}
