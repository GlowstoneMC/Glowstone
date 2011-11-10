package net.glowstone.msg.handler;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.MaterialData;

import net.glowstone.EventFactory;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.net.Session;
import org.omg.SendingContext.RunTime;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class BlockPlacementMessageHandler extends MessageHandler<BlockPlacementMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, BlockPlacementMessage message) {
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
            BlockPlacementMessage previous = player.getPreviousPlacement();
            if (previous == null
                    || previous.getCount() == message.getCount()
                    || previous.getId() == message.getId()
                    || previous.getDamage() == message.getDamage()) {
                EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR);
            }
            player.setPreviousPlacement(null);
            return;
        }

        Block against = player.getWorld().getBlockAt(message.getX(), message.getY(), message.getZ());
        BlockFace face = MessageHandlerUtils.messageToBlockFace(message.getDirection());
        if (face == BlockFace.SELF) return;
        
        Block target = against.getRelative(face);
        ItemStack holding = player.getItemInHand();
        if (EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, target, face).isCancelled()) return;
        if (holding != null && holding.getTypeId() < 256) {
            if (target.isEmpty() || target.isLiquid()) {
                BlockState newState = target.getState();
                newState.setType(player.getItemInHand().getType());
                newState.setData(new MaterialData(newState.getType(), (byte) holding.getDurability()));
                
                BlockPlaceEvent event = EventFactory.onBlockPlace(target, newState, against, player);
                if (!event.isCancelled() && event.canBuild()) {
                    newState.update(true);
                    if (player.getGameMode() != GameMode.CREATIVE) {
                    holding.setAmount(holding.getAmount() - 1);
                        if (holding.getAmount() == 0) {
                            player.setItemInHand(null);
                        } else {
                            player.setItemInHand(holding);
                        }
                    }
                }
            }
        }
    }

}
