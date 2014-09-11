package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public final class BlockPlacementHandler implements MessageHandler<GlowSession, BlockPlacementMessage> {
    @Override
    public void handle(GlowSession session, BlockPlacementMessage message) {
        final GlowPlayer player = session.getPlayer();
        if (player == null)
            return;

        //GlowServer.logger.info(session + ": " + message);

        /**
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
        GlowBlock clicked = player.getWorld().getBlockAt(message.getX(), message.getY(), message.getZ());

        /**
         * Check if the message is a -1. If we *just* got a message with the
         * values filled, discard it, otherwise perform right-click-air.
         */
        if (message.getDirection() == -1) {
            BlockPlacementMessage previous = session.getPreviousPlacement();
            if (previous == null || !previous.getHeldItem().equals(message.getHeldItem())) {
                // perform normal right-click-air actions
                action = Action.RIGHT_CLICK_AIR;
                clicked = null;
            } else {
                // terminate processing of this event
                session.setPreviousPlacement(null);
                return;
            }
        }

        // Set previous placement message
        session.setPreviousPlacement(message);

        // Get values from the message
        Vector clickedLoc = new Vector(message.getCursorX(), message.getCursorY(), message.getCursorZ());
        BlockFace face = convertFace(message.getDirection());
        ItemStack holding = player.getItemInHand();

        // check that held item matches
        if (!Objects.equals(holding, message.getHeldItem())) {
            // above handles cases where holding and/or message's item are null
            // todo: inform player their item is wrong
            return;
        }

        // check that a block-click wasn't against air
        if (clicked != null && clicked.getType() == Material.AIR) {
            // inform the player their perception of reality is wrong
            player.sendBlockChange(clicked.getLocation(), Material.AIR, (byte) 0);
            return;
        }

        // call interact event
        PlayerInteractEvent event = EventFactory.onPlayerInteract(player, action, clicked, face);
        //GlowServer.logger.info("Interact: " + action + " " + clicked + " " + face);

        // attempt to use interacted block
        // DEFAULT is treated as ALLOW, and sneaking is always considered
        boolean useInteractedBlock = event.useInteractedBlock() != Event.Result.DENY;
        if (useInteractedBlock && clicked != null && (!player.isSneaking() || holding == null)) {
            BlockType blockType = ItemTable.instance().getBlock(clicked.getType());
            useInteractedBlock = blockType.blockInteract(player, clicked, face, clickedLoc);
        } else {
            useInteractedBlock = false;
        }

        // attempt to use item in hand
        // follows ALLOW/DENY: default to if no block was interacted with
        if (selectResult(event.useItemInHand(), !useInteractedBlock) && holding != null) {
            // call out to the item type to determine the appropriate right-click action
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (clicked == null) {
                type.rightClickAir(player, holding);
            } else {
                type.rightClickBlock(player, clicked, face, holding, clickedLoc);
            }
        }

        // if anything was actually clicked, make sure the player's up to date
        // in case something is unimplemented or otherwise screwy on our side
        if (clicked != null) {
            revert(player, clicked);
            revert(player, clicked.getRelative(face));
        }

        // if there's been a change in the held item, make it valid again
        if (holding != null) {
            if (holding.getType().getMaxDurability() > 0 && holding.getDurability() > holding.getType().getMaxDurability()) {
                holding.setAmount(holding.getAmount() - 1);
                holding.setDurability((short) 0);
            }
            if (holding.getAmount() <= 0) {
                holding = null;
            }
        }
        player.setItemInHand(holding);
    }

    static boolean selectResult(Event.Result result, boolean def) {
        return result == Event.Result.DEFAULT ? def : result == Event.Result.ALLOW;
    }

    static void revert(GlowPlayer player, GlowBlock target) {
        player.sendBlockChange(target.getLocation(), target.getType(), target.getData());
        TileEntity entity = target.getTileEntity();
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

    private static final BlockFace[] faces = {
            BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST
    };
}
