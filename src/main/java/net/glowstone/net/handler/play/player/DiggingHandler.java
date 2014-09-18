package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.DiggingMessage;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class DiggingHandler implements MessageHandler<GlowSession, DiggingMessage> {
    @Override
    public void handle(GlowSession session, DiggingMessage message) {
        final GlowPlayer player = session.getPlayer();
        GlowWorld world = player.getWorld();
        GlowBlock block = world.getBlockAt(message.getX(), message.getY(), message.getZ());
        BlockFace face = BlockPlacementHandler.convertFace(message.getFace());
        ItemStack holding = player.getItemInHand();

        boolean blockBroken = false;
        boolean revert = false;
        if (message.getState() == DiggingMessage.START_DIGGING) {
            // call interact event
            Action action = Action.LEFT_CLICK_BLOCK;
            Block eventBlock = block;
            if (player.getLocation().distanceSquared(block.getLocation()) > 36 || block.getTypeId() == 0) {
                action = Action.LEFT_CLICK_AIR;
                eventBlock = null;
            }
            PlayerInteractEvent interactEvent = EventFactory.onPlayerInteract(player, action, eventBlock, face);

            // blocks don't get interacted with on left click, so ignore that
            // attempt to use item in hand, that is, dig up the block
            if (!BlockPlacementHandler.selectResult(interactEvent.useItemInHand(), true)) {
                // the event was cancelled, get out of here
                revert = true;
            } else {
                // emit damage event - cancel by default if holding a sword
                boolean instaBreak = player.getGameMode() == GameMode.CREATIVE;
                BlockDamageEvent damageEvent = new BlockDamageEvent(player, block, player.getItemInHand(), instaBreak);
                if (holding != null && EnchantmentTarget.WEAPON.includes(holding.getType())) {
                    damageEvent.setCancelled(true);
                }
                EventFactory.callEvent(damageEvent);

                // follow orders
                if (damageEvent.isCancelled()) {
                    revert = true;
                } else {
                    // in creative, break even if denied in the event, or the block
                    // can never be broken (client does not send DONE_DIGGING).
                    blockBroken = damageEvent.getInstaBreak() || instaBreak;
                }
            }
        } else if (message.getState() == DiggingMessage.FINISH_DIGGING) {
            // shouldn't happen in creative mode

            // todo: verification against malicious clients
            // also, if the block dig was denied, this break might still happen
            // because a player's digging status isn't yet tracked. this is bad.
            blockBroken = true;
        } else {
            return;
        }

        if (blockBroken) {
            // fire the block break event
            BlockBreakEvent breakEvent = EventFactory.callEvent(new BlockBreakEvent(block, player));
            if (breakEvent.isCancelled()) {
                BlockPlacementHandler.revert(player, block);
                return;
            }

            // destroy the block
            if (!block.isEmpty() && !block.isLiquid() && player.getGameMode() != GameMode.CREATIVE) {
                for (ItemStack drop : block.getDrops(holding)) {
                    player.getInventory().addItem(drop);
                }
            }
            // STEP_SOUND actually is the block break particles
            world.playEffectExceptTo(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 64, player);
            block.setType(Material.AIR);
        } else if (revert) {
            // replace the block that wasn't really dug
            BlockPlacementHandler.revert(player, block);
        }
    }
}
