package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

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

        // todo: handle items which become blocks when placed
        if (holding == null || !holding.getType().isBlock()) {
            revert(player, target);
            return;
        }

        // todo: handle placing inside grass
        if (!target.isEmpty() && !target.isLiquid()) {
            revert(player, target);
            return;
        }

        // call canBuild event
        if (!EventFactory.onBlockCanBuild(target, holding.getTypeId(), face).isBuildable()) {
            revert(player, target);
            return;
        }

        // todo: per-block-type handling
        GlowBlockState newState = target.getState();
        newState.setType(holding.getType());
        newState.setRawData((byte) holding.getDurability());  // hacky

        // call blockPlace event
        BlockPlaceEvent event = EventFactory.onBlockPlace(target, newState, against, player);
        if (event.isCancelled() || !event.canBuild()) {
            revert(player, target);
            return;
        }

        // perform the block change
        newState.update(true);

        // deduct from stack if not in creative mode
        if (player.getGameMode() != GameMode.CREATIVE) {
            holding.setAmount(holding.getAmount() - 1);
            if (holding.getAmount() == 0) {
                player.setItemInHand(null);
            } else {
                player.setItemInHand(holding);
            }
        }
    }

    private void revert(GlowPlayer player, GlowBlock target) {
        player.sendBlockChange(target.getLocation(), target.getType(), target.getData());
    }

    static final BlockFace[] faces = {
            BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST
    };
}
