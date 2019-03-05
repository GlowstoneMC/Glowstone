package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class BlockPlacementHandler implements
    MessageHandler<GlowSession, BlockPlacementMessage> {

    private static final BlockFace[] faces = {
        BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
        BlockFace.EAST
    };

    static boolean selectResult(Result result, boolean def) {
        return result == Result.DEFAULT ? def : result == Result.ALLOW;
    }

    static void revert(GlowPlayer player, GlowBlock target) {
        player.sendBlockChange(target.getLocation(), target.getType(), target.getData());
        BlockEntity entity = target.getBlockEntity();
        if (entity != null) {
            entity.update(player);
        }
    }

    static BlockFace convertFace(int direction) {
        if (direction >= 0 && direction < faces.length) {
            return faces[direction];
        } else {
            return BlockFace.SELF;
        }
    }

    private static boolean bothHandsEmpty(GlowPlayer player) {
        return InventoryUtil.isEmpty(player.getInventory().getItem(EquipmentSlot.HAND))
                && InventoryUtil.isEmpty(player.getInventory().getItem(EquipmentSlot.OFF_HAND));
    }

    @Override
    public void handle(GlowSession session, BlockPlacementMessage message) {
        GlowPlayer player = session.getPlayer();
        if (player == null) {
            return;
        }
        /*
         * The client sends this packet when
         * - it expects a block place to happen
         * - the player clicks on a block with an empty hand
         *
         * When the client doesn't expect the block place to be successful,
         * it sends a Use item packet instead (See UseItemHandler).
         *
         * Client will expect a block placement to result from blocks and from
         * certain items (e.g. sugarcane, sign). We *could* opt to trust the
         * client on this, but the server's view of events (particularly under
         * the Bukkit API, or custom ItemTypes) may differ from the client's.
         */
        GlowBlock clicked = player.getWorld()
            .getBlockAt(message.getX(), message.getY(), message.getZ());

        // Get values from the message
        Vector clickedLoc = new Vector(message.getCursorX(), message.getCursorY(),
            message.getCursorZ());
        BlockFace face = convertFace(message.getDirection());
        ItemStack holding = InventoryUtil
            .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

        // check that a block-click wasn't against air
        if (clicked.getType() == Material.AIR) {
            if (holding.getType().isBlock()) {
                // inform the player their perception of reality is wrong
                revert(player, clicked);
                // revert newly placed block
                revert(player, clicked.getRelative(face));
            } else {
                // There may be a block behind, if there isn't we perform a right click air
                UseItemHandler.handleRightClick(player, holding, message.getHandSlot());
            }
            return;
        }

        handleRightClickBlock(player, holding, message.getHandSlot(), clicked, face, clickedLoc);
    }

    static void handleRightClickBlock(
            GlowPlayer player, ItemStack holding, EquipmentSlot slot, GlowBlock clicked,
            BlockFace face, Vector clickedLoc) {
        // call interact event
        PlayerInteractEvent event = EventFactory.getInstance().onPlayerInteract(
                player, Action.RIGHT_CLICK_BLOCK, slot, clicked, face);

        // attempt to use interacted block
        // DEFAULT is treated as ALLOW, and sneaking is always considered
        boolean useInteractedBlock = event.useInteractedBlock() != Result.DENY;
        if (useInteractedBlock && (!player.isSneaking() || bothHandsEmpty(player))) {
            BlockType blockType = ItemTable.instance().getBlock(clicked.getType());
            if (blockType != null) {
                useInteractedBlock = blockType.blockInteract(player, clicked, face, clickedLoc);
            } else {
                ConsoleMessages.Info.Block.UNKNOWN_CLICKED.log(clicked.getType());
            }
        } else {
            useInteractedBlock = false;
        }

        // attempt to use item in hand
        // follows ALLOW/DENY: default to if no block was interacted with
        if (selectResult(event.useItemInHand(), !useInteractedBlock)) {
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (holding.getType() != Material.AIR
                && type.getContext().isBlockApplicable()) {
                type.rightClickBlock(player, clicked, face, holding, clickedLoc, slot);
            }
        }

        // make sure the player's up to date
        // in case something is unimplemented or otherwise screwy on our side
        revert(player, clicked);
        revert(player, clicked.getRelative(face));

        // if there's been a change in the held item, make it valid again
        if (!InventoryUtil.isEmpty(holding) && holding.getType().getMaxDurability() > 0
            && holding.getDurability() > holding.getType().getMaxDurability()) {
            holding.setAmount(holding.getAmount() - 1);
            holding.setDurability((short) 0);
        }

        if (holding.getAmount() <= 0) {
            player.getInventory().setItem(slot, InventoryUtil.createEmptyStack());
        } else {
            // Set the item in `slot` to `holding`, as it was cloned before its amount was decremented.
            player.getInventory().setItem(slot, holding);
        }
    }
}
