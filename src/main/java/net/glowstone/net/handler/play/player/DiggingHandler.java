package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
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

        if (block.getRelative(face).getType() == Material.FIRE) {
            block.getRelative(face).breakNaturally();
            return; // returns to avoid breaking block in creative
        }

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
            } else if (player.getGameMode() != GameMode.SPECTATOR) {
                // emit damage event - cancel by default if holding a sword
                boolean instaBreak = player.getGameMode() == GameMode.CREATIVE || block.getMaterialValues().getHardness() == 0;
                BlockDamageEvent damageEvent = new BlockDamageEvent(player, block, player.getItemInHand(), instaBreak);
                if (player.getGameMode() != GameMode.CREATIVE && player.getDigging() != null || player.getGameMode() == GameMode.CREATIVE && holding != null && EnchantmentTarget.WEAPON.includes(holding.getType())) {
                    damageEvent.setCancelled(true);
                }
                EventFactory.callEvent(damageEvent);

                // follow orders
                if (damageEvent.isCancelled()) {
                    revert = true;
                } else {
                    // in creative, break even if denied in the event, or the block
                    // can never be broken (client does not send DONE_DIGGING).
                    blockBroken = damageEvent.getInstaBreak();
                    if (!blockBroken) {
                        /// TODO: add a delay here based on hardness
                        player.setDigging(block);
                    }
                }
            }
        } else if (message.getState() == DiggingMessage.FINISH_DIGGING) {
            // shouldn't happen in creative mode

            // todo: verification against malicious clients
            if (player.getDigging() != null) {
                //block state could be different
                blockBroken = block.getLocation().equals(player.getDigging().getLocation());
            }
        } else if (message.getState() == DiggingMessage.STATE_DROP_ITEM) {
            player.dropItemInHand(false);
            return;
        } else if (message.getState() == DiggingMessage.STATE_DROP_ITEMSTACK) {
            player.dropItemInHand(true);
            return;
        } else {
            return;
        }

        if (blockBroken && !revert) {
            // fire the block break event
            BlockBreakEvent breakEvent = EventFactory.callEvent(new BlockBreakEvent(block, player));
            if (breakEvent.isCancelled()) {
                BlockPlacementHandler.revert(player, block);
                return;
            }

            BlockType blockType = ItemTable.instance().getBlock(block.getType());
            if (blockType != null) {
                blockType.blockDestroy(player, block, face);
            }

            // destroy the block
            if (!block.isEmpty() && !block.isLiquid() && player.getGameMode() != GameMode.CREATIVE && world.getGameRuleMap().getBoolean("doTileDrops")) {
                for (ItemStack drop : block.getDrops(holding)) {
                    GlowItem item = world.dropItemNaturally(block.getLocation(), drop);
                    item.setPickupDelay(30);
                    item.setBias(player);
                }
            }
            // STEP_SOUND actually is the block break particles
            world.playEffectExceptTo(block.getLocation(), Effect.STEP_SOUND, block.getTypeId(), 64, player);
            GlowBlockState state = block.getState();
            block.setType(Material.AIR);
            if (blockType != null) {
                blockType.afterDestroy(player, block, face, state);
            }
        } else if (revert) {
            // replace the block that wasn't really dug
            BlockPlacementHandler.revert(player, block);
        } else {
            BlockType blockType = ItemTable.instance().getBlock(block.getType());
            blockType.leftClickBlock(player, block, holding);
        }
        player.setDigging(null);
    }
}
