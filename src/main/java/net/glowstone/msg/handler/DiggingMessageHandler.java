package net.glowstone.msg.handler;

import net.glowstone.block.BlockID;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.DiggingMessage;
import net.glowstone.net.Session;

/**
 * A {@link MessageHandler} which processes digging messages.
 */
public final class DiggingMessageHandler extends MessageHandler<DiggingMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, DiggingMessage message) {
        if (player == null)
            return;

        boolean blockBroken = false;

        GlowWorld world = player.getWorld();

        int x = message.getX();
        int y = message.getY();
        int z = message.getZ();

        Block block = world.getBlockAt(x, y, z);

        // Need to have some sort of verification to deal with malicious clients.
        if (message.getState() == DiggingMessage.STATE_START_DIGGING) {
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
                if ((!player.getInventory().contains(block.getType()) || player.getGameMode() != GameMode.CREATIVE)) {
                    player.getInventory().addItem(new ItemStack(block.getType(), 1, block.getData()));
                }
            }
            world.playEffectExceptTo(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 64, player);
            block.setTypeId(BlockID.AIR);
        }
    }

}
