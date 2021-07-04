package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockEnderPortalFrame extends BlockDropless {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        state.setType(Material.ENDER_PORTAL_FRAME);
        switch (getOppositeBlockFace(player.getLocation(), false).getOppositeFace()) {
            case NORTH:
                state.setRawData((byte) 0);
                break;
            case EAST:
                state.setRawData((byte) 1);
                break;
            case SOUTH:
                state.setRawData((byte) 2);
                break;
            case WEST:
                state.setRawData((byte) 3);
                break;
            default:
                state.setRawData((byte) 0);
                break;
        }
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        ItemStack item = player.getItemInHand();
        if (item != null && item.getType() == Material.EYE_OF_ENDER) {
            if ((block.getData() & 0x4) != 0) {
                return true;
            }
            if (player.getGameMode() != GameMode.CREATIVE) {
                item.setAmount(item.getAmount() - 1);
            }

            block.setData((byte) (block.getData() | 0x4));
            if (block.getWorld().getEnvironment() != Environment.THE_END) {
                searchForCompletedPortal(player, block);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks for a completed portal at all relevant positions.
     */
    private void searchForCompletedPortal(GlowPlayer player, GlowBlock changed) {
        for (int i = 0; i < 4; i++) {
            for (int j = -1; j <= 1; j++) {
                GlowBlock center = changed.getRelative(SIDES[i], 2)
                    .getRelative(SIDES[(i + 1) % 4], j);
                if (isCompletedPortal(center)) {
                    createPortal(player, center);
                    return;
                }
            }
        }
    }

    /**
     * Check whether there is a completed portal with the specified center.
     */
    private boolean isCompletedPortal(GlowBlock center) {
        for (int i = 0; i < 4; i++) {
            for (int j = -1; j <= 1; j++) {
                GlowBlock block = center.getRelative(SIDES[i], 2)
                    .getRelative(SIDES[(i + 1) % 4], j);
                if (block.getType() != Material.ENDER_PORTAL_FRAME
                    || (block.getData() & 0x4) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Spawn the portal and call the {@link EntityCreatePortalEvent}.
     */
    private void createPortal(GlowPlayer player, GlowBlock center) {
        List<BlockState> blocks = new ArrayList<>(9);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                BlockState state = center.getRelative(i, 0, j).getState();
                state.setType(Material.ENDER_PORTAL);
                blocks.add(state);
            }
        }
        if (!EventFactory.getInstance()
                .callEvent(new EntityCreatePortalEvent(player, blocks, PortalType.ENDER))
                .isCancelled()) {
            for (BlockState state : blocks) {
                state.update(true);
            }
        }
    }
}
