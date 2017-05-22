package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockContainer;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemTimedUsage;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.DiggingMessage;
import org.bukkit.material.types.DoublePlantSpecies;
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
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;

import java.util.Collection;
import java.util.Objects;

public final class DiggingHandler implements MessageHandler<GlowSession, DiggingMessage> {
    @Override
    public void handle(GlowSession session, DiggingMessage message) {
        //Todo: Implement SHOOT_ARROW_FINISH_EATING
        //Todo: Implement SWAP_ITEM_IN_HAND
        GlowPlayer player = session.getPlayer();
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
                player.setDigging(null);
                // emit damage event - cancel by default if holding a sword
                boolean instaBreak = player.getGameMode() == GameMode.CREATIVE || block.getMaterialValues().getHardness() == 0;
                BlockDamageEvent damageEvent = new BlockDamageEvent(player, block, player.getItemInHand(), instaBreak);
                if (player.getGameMode() == GameMode.CREATIVE && holding != null && EnchantmentTarget.WEAPON.includes(holding.getType())) {
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
        } else if (message.getState() == DiggingMessage.CANCEL_DIGGING) {
            player.setDigging(null);
        } else if (message.getState() == DiggingMessage.FINISH_DIGGING) {
            // shouldn't happen in creative mode

            // todo: verification against malicious clients
            blockBroken = block.equals(player.getDigging());

            if (blockBroken && holding.getType().getMaxDurability() != 0 && holding.getType() != Material.AIR && holding.getDurability() != holding.getType().getMaxDurability()) {
                switch (block.getType()) {
                    case GRASS:
                    case DIRT:
                    case SAND:
                    case GRAVEL:
                    case MYCEL:
                    case SOUL_SAND:
                        switch (holding.getType()) {
                            case WOOD_SPADE:
                            case STONE_SPADE:
                            case IRON_SPADE:
                            case GOLD_SPADE:
                            case DIAMOND_SPADE:
                                holding.setDurability((short) (holding.getDurability() + 1));
                                break;
                            default:
                                holding.setDurability((short) (holding.getDurability() + 2));
                                break;
                        }
                        break;
                    case LOG:
                    case LOG_2:
                    case WOOD:
                    case CHEST:
                        switch (holding.getType()) {
                            case WOOD_AXE:
                            case STONE_AXE:
                            case IRON_AXE:
                            case GOLD_AXE:
                            case DIAMOND_AXE:
                                holding.setDurability((short) (holding.getDurability() + 1));
                                break;
                            default:
                                holding.setDurability((short) (holding.getDurability() + 2));
                                break;
                        }
                        break;
                    case STONE:
                    case COBBLESTONE:
                        switch (holding.getType()) {
                            case WOOD_PICKAXE:
                            case STONE_PICKAXE:
                            case IRON_PICKAXE:
                            case GOLD_PICKAXE:
                            case DIAMOND_PICKAXE:
                                holding.setDurability((short) (holding.getDurability() + 1));
                                break;
                            default:
                                holding.setDurability((short) (holding.getDurability() + 2));
                                break;
                        }
                        break;
                    default:
                        holding.setDurability((short) (holding.getDurability() + 2));
                        break;
                }
                if (holding.getType().getMaxDurability() != 0 && holding.getDurability() >= holding.getType().getMaxDurability()) {
                    player.getItemInHand().setType(Material.AIR);
                }
            }
            player.setDigging(null);
        } else if (message.getState() == DiggingMessage.STATE_DROP_ITEM) {
            player.dropItemInHand(false);
            return;
        } else if (message.getState() == DiggingMessage.STATE_DROP_ITEMSTACK) {
            player.dropItemInHand(true);
            return;
        } else if (message.getState() == DiggingMessage.STATE_SHOT_ARROW_FINISH_EATING && player.getUsageItem() != null) {
            if (Objects.equals(player.getUsageItem(), holding)) {
                ItemType type = ItemTable.instance().getItem(player.getUsageItem().getType());
                if (type != null && type instanceof ItemTimedUsage) {
                    ((ItemTimedUsage) type).endUse(player, player.getUsageItem());
                } else {
                    // todo: inform the player that this item cannot be consumed/used
                }
            } else {
                // todo: verification against malicious clients
                // todo: inform player their item is wrong
            }
            return;
        } else if (message.getState() == DiggingMessage.SWAP_ITEM_IN_HAND) {
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            player.getInventory().setItemInOffHand(main);
            player.getInventory().setItemInMainHand(off);
            player.updateInventory();
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

            MaterialData data = block.getState().getData();
            if (data instanceof DoublePlant) {
                if (((DoublePlant) data).getSpecies() == DoublePlantSpecies.PLANT_APEX && block.getRelative(BlockFace.DOWN).getState().getData() instanceof DoublePlant) {
                    block = block.getRelative(BlockFace.DOWN);
                }
            }

            BlockType blockType = ItemTable.instance().getBlock(block.getType());
            if (blockType != null) {
                blockType.blockDestroy(player, block, face);
            }

            // destroy the block
            if (!block.isEmpty() && !block.isLiquid() && (player.getGameMode() != GameMode.CREATIVE || blockType instanceof BlockContainer) && world.getGameRuleMap().getBoolean("doTileDrops")) {
                Collection<ItemStack> drops = blockType.getDrops(block, holding);
                if (blockType instanceof BlockContainer && player.getGameMode() == GameMode.CREATIVE) {
                    drops = ((BlockContainer) blockType).getContentDrops(block);
                }
                for (ItemStack drop : drops) {
                    GlowItem item = world.dropItemNaturally(block.getLocation(), drop);
                    item.setPickupDelay(30);
                    item.setBias(player);
                }
            }

            player.addExhaustion(0.005f);

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
        } else if (block.getType() != Material.AIR) {
            BlockType blockType = ItemTable.instance().getBlock(block.getType());
            blockType.leftClickBlock(player, block, holding);
        }
    }
}
