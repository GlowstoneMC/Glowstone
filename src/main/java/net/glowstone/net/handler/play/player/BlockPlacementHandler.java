package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Objects;

public final class BlockPlacementHandler implements MessageHandler<GlowSession, BlockPlacementMessage> {
    public void handle(GlowSession session, BlockPlacementMessage message) {
        final GlowPlayer player = session.getPlayer();
        if (player == null)
            return;

        /**
         * The notch client's packet sending is weird. Here's how it works:
         * If the client is clicking a block not in range, sends a packet with x=-1,y=255,z=-1
         * If the client is clicking a block in range with an item in hand (id > 255)
         * Sends both the normal block placement packet and a (-1,255,-1) one
         * If the client is placing a block in range with a block in hand, only one normal packet is sent
         * That is how it usually happens. Sometimes it doesn't happen like that.
         * Therefore, a hacky workaround.
         */
        if (message.getDirection() == 255) {
            // Right-clicked air. Note that the client doesn't send this if they are holding nothing.
            BlockPlacementMessage previous = session.getPreviousPlacement();
            if (previous == null
                    || !previous.getHeldItem().equals(message.getHeldItem())) {
                EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR);
            }
            session.setPreviousPlacement(null);
            return;
        }
        session.setPreviousPlacement(message);

        GlowBlock against = player.getWorld().getBlockAt(message.getX(), message.getY(), message.getZ());
        if (message.getDirection() < 0 || message.getDirection() >= faces.length) return;
        BlockFace face = faces[message.getDirection()];

        GlowBlock target = against.getRelative(face);
        ItemStack holding = player.getItemInHand();

        GlowServer.logger.info(session + ": " + message);

        if (!Objects.equals(holding, message.getHeldItem())) {
            // above handles cases where holding and/or message's item are null
            // todo: inform player their item is wrong
            revert(player, target);
            return;
        }

        if (EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, against, face).isCancelled()) {
            revert(player, target);
            return;
        }

        if (holding == null) {
            revert(player, target);
            return;
        }

        // call out to the item type to determine the appropriate right-click action
        ItemType type = ItemTable.instance().getItem(holding.getType());
        Vector clickedLoc = new Vector(message.getCursorX(), message.getCursorY(), message.getCursorZ()).multiply(1.0 / 16.0);
        type.rightClicked(player, against, face, holding, clickedLoc);

        // if there's been a change in the held item, make it valid again
        if (holding.getDurability() > holding.getType().getMaxDurability()) {
            holding.setAmount(holding.getAmount() - 1);
            holding.setDurability((short) 0);
        }
        if (holding.getAmount() <= 0) {
            holding = null;
        }
        player.setItemInHand(holding);
    }

    private void revert(GlowPlayer player, GlowBlock target) {
        player.sendBlockChange(target.getLocation(), target.getType(), target.getData());
    }

    static final BlockFace[] faces = {
            BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST
    };
}
