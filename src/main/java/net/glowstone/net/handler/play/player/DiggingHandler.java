package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.DiggingMessage;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class DiggingHandler implements MessageHandler<GlowSession, DiggingMessage> {
    public void handle(GlowSession session, DiggingMessage message) {
        final GlowPlayer player = session.getPlayer();

        boolean blockBroken = false;

        GlowWorld world = player.getWorld();

        int x = message.getX();
        int y = message.getY();
        int z = message.getZ();

        Block block = world.getBlockAt(x, y, z);

        if (message.getFace() < 0 || message.getFace() >= BlockPlacementHandler.faces.length) return;
        BlockFace face = BlockPlacementHandler.faces[message.getFace()];

        // Need to have some sort of verification to deal with malicious clients.
        if (message.getState() == DiggingMessage.STATE_START_DIGGING) {
            Action act = Action.LEFT_CLICK_BLOCK;
            if (player.getLocation().distanceSquared(block.getLocation()) > 36 || block.getTypeId() == 0) {
                act = Action.LEFT_CLICK_AIR;
            }
            PlayerInteractEvent interactEvent = EventFactory.onPlayerInteract(player, act, block, face);
            if (interactEvent.isCancelled()) return;
            if (interactEvent.useItemInHand() == Event.Result.DENY) return;
            // TODO: Item interactions
            BlockDamageEvent event = EventFactory.onBlockDamage(player, block);
            if (!event.isCancelled()) {
                blockBroken = event.getInstaBreak() || player.getGameMode() == GameMode.CREATIVE;
            }
        } else if (message.getState() == DiggingMessage.STATE_DONE_DIGGING) {
            BlockBreakEvent event = EventFactory.onBlockBreak(block, player);
            if (!event.isCancelled()) {
                blockBroken = true;
            }
        }

        if (blockBroken) {
            if (!block.isEmpty() && !block.isLiquid()) {
                if (player.getGameMode() != GameMode.CREATIVE) {
                    for (ItemStack drop : block.getDrops(player.getItemInHand())) {
                        player.getInventory().addItem(drop);
                    }
                }
            }
            world.playEffectExceptTo(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 64, player);
            block.setTypeId(0);
        }
    }
}
