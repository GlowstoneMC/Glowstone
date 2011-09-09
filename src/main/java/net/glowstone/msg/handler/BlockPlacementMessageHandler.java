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

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class BlockPlacementMessageHandler extends MessageHandler<BlockPlacementMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, BlockPlacementMessage message) {
        if (player == null)
            return;
        
        if (message.getY() < 0) {
            // Right-clicked air. Note that the client doesn't send this if they are holding nothing.
            EventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR);
            return;
        }

        Block against = player.getWorld().getBlockAt(message.getX(), message.getY(), message.getZ());
        BlockFace face;
        switch (message.getDirection()) {
            case 0: face = BlockFace.DOWN; break;
            case 1: face = BlockFace.UP; break;
            case 2: face = BlockFace.EAST; break;
            case 3: face = BlockFace.WEST; break;
            case 4: face = BlockFace.NORTH; break;
            case 5: face = BlockFace.SOUTH; break;
            default: return;
        }
        
        Block target = against.getRelative(face);
        ItemStack holding = player.getItemInHand();
        
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
