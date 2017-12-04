package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.block.itemtype.ItemType.Context;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @Override
    public void handle(GlowSession session, BlockPlacementMessage message) {
        GlowPlayer player = session.getPlayer();
        if (player == null) {
            return;
        }

        //GlowServer.logger.info(session + ": " + message);

        /*
         * The client sends this packet for the following cases:
         * Right click air:
         * - Send direction=-1 packet for any non-null item
         * Right click block:
         * - Send packet with all values filled
         * - If client DOES NOT expect a block placement to result:
         *   - Send direction=-1 packet (unless item is null)
         *
         * Client will expect a block placement to result from blocks and from
         * certain items (e.g. sugarcane, sign). We *could* opt to trust the
         * client on this, but the server's view of events (particularly under
         * the Bukkit API, or custom ItemTypes) may differ from the client's.
         *
         * In order to avoid firing two events for one interact, the two
         * packet case must be handled here. Care must also be taken that a
         * right-click air of an expected-place item immediately after is
         * not considered part of the same action.
         */

        Action action = Action.RIGHT_CLICK_BLOCK;
        GlowBlock clicked = player.getWorld()
            .getBlockAt(message.getX(), message.getY(), message.getZ());

        /*
         * Check if the message is a -1. If we *just* got a message with the
         * values filled, discard it, otherwise perform right-click-air.
         */
        if (message.getDirection() == -1) {
            BlockPlacementMessage previous = session.getPreviousPlacement();
            //if (previous == null || !previous.getHeldItem().equals(message.getHeldItem())) {
            // perform normal right-click-air actions
            //   action = Action.RIGHT_CLICK_AIR;
            //   clicked = null;
            //} else {
            // terminate processing of this event
            //   session.setPreviousPlacement(null);
            return;
            // }
        }

        // Set previous placement message
        session.setPreviousPlacement(message);

        // Get values from the message
        Vector clickedLoc = new Vector(message.getCursorX(), message.getCursorY(),
            message.getCursorZ());
        BlockFace face = convertFace(message.getDirection());
        ItemStack holding = InventoryUtil
            .itemOrEmpty(player.getInventory().getItem(message.getHandSlot()));

        boolean rightClickedAir = false;
        // check that a block-click wasn't against air
        if (clicked == null || clicked.getType() == Material.AIR) {
            action = Action.RIGHT_CLICK_AIR;
            // inform the player their perception of reality is wrong
            if (holding.getType().isBlock()) {
                player.sendBlockChange(clicked.getLocation(), Material.AIR, (byte) 0);
                return;
            } else {
                rightClickedAir = true;
            }
        }

        // call interact event
        PlayerInteractEvent event = EventFactory
            .onPlayerInteract(player, action, rightClickedAir ? null : clicked, face);
        //GlowServer.logger.info("Interact: " + action + " " + clicked + " " + face);

        // attempt to use interacted block
        // DEFAULT is treated as ALLOW, and sneaking is always considered
        boolean useInteractedBlock = event.useInteractedBlock() != Result.DENY;
        if (useInteractedBlock && !rightClickedAir && (!player.isSneaking() || InventoryUtil
            .isEmpty(holding))) {
            BlockType blockType = ItemTable.instance().getBlock(clicked.getType());
            if (blockType != null) {
                useInteractedBlock = blockType.blockInteract(player, clicked, face, clickedLoc);
            } else {
                GlowServer.logger.info("Unknown clicked block, " + clicked.getType());
            }
        } else {
            useInteractedBlock = false;
        }

        // attempt to use item in hand
        // follows ALLOW/DENY: default to if no block was interacted with
        if (selectResult(event.useItemInHand(), !useInteractedBlock) && holding != null) {
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (!rightClickedAir && holding.getType() != Material.AIR && (
                type.getContext() == Context.BLOCK || type.getContext() == Context.ANY)) {
                type.rightClickBlock(player, clicked, face, holding, clickedLoc,
                    message.getHandSlot());
            }
        }

        // if anything was actually clicked, make sure the player's up to date
        // in case something is unimplemented or otherwise screwy on our side
        if (!rightClickedAir) {
            revert(player, clicked);
            revert(player, clicked.getRelative(face));
        }

        // if there's been a change in the held item, make it valid again
        if (!InventoryUtil.isEmpty(holding)) {
            if (holding.getType().getMaxDurability() > 0 && holding.getDurability() > holding
                .getType().getMaxDurability()) {
                holding.setAmount(holding.getAmount() - 1);
                holding.setDurability((short) 0);
            }
            if (holding.getAmount() <= 0) {
                holding = InventoryUtil.createEmptyStack();
            }
        }
        player.getInventory().setItem(message.getHandSlot(), holding);
    }
}
