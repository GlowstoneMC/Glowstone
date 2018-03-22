package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import com.google.common.collect.ImmutableSortedSet;
import java.util.List;
import java.util.SortedSet;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.UseItemMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class UseItemHandler implements MessageHandler<GlowSession, UseItemMessage> {

    private static final SortedSet<Material> IGNORE_MATS = ImmutableSortedSet.of(
            Material.AIR, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA,
            Material.STATIONARY_WATER);

    @Override
    public void handle(GlowSession session, UseItemMessage message) {
        GlowPlayer player = session.getPlayer();
        ItemStack holding = InventoryUtil
                .itemOrEmpty(player.getInventory().getItem(message.getEquipmentSlot()));

        handleRightClick(player, holding, message.getEquipmentSlot());
    }

    static void handleRightClick(GlowPlayer player, ItemStack holding, EquipmentSlot slot) {
        Action action = Action.RIGHT_CLICK_AIR;

        GlowBlock block = null;
        BlockFace face = BlockFace.SELF;

        List<Block> targetBlocks = player.getLastTwoTargetBlocks(IGNORE_MATS, 5);
        if (targetBlocks != null && targetBlocks.size() > 0) {
            block = (GlowBlock) targetBlocks.get(targetBlocks.size() - 1);
            if (targetBlocks.size() > 1) {
                face = block.getFace(targetBlocks.get(0));
            }
        }

        Vector clickedAt = null;

        if (block != null && !IGNORE_MATS.contains(block.getType())) {
            /* There are two special cases on right click:
             * - When the client doesn't expect a block place to be successful this is sent
             *   instead of a block place packet
             * - When the block is not placeable (e.g. a nether star) this packet is sent
             *   in addition to a block place packet (no need to throw the event twice)
             */
            action = Action.RIGHT_CLICK_BLOCK;
            if (!holding.getType().isBlock()) {
                return; // Action was already handled by BlockPlacementHandler
            } else {
                clickedAt = new Vector(0, 0, 0);
                // TODO: Implement proper raytrace and determine exact click location
            }
        }

        if (action == Action.RIGHT_CLICK_AIR) {
            handleRightClickAir(player, holding, slot);
        } else {
            BlockPlacementHandler.handleRightClickBlock(
                    player, holding, slot, block, face, clickedAt);
        }
    }

    static void handleRightClickAir(GlowPlayer player, ItemStack holding, EquipmentSlot slot) {
        PlayerInteractEvent event = player.getServer().getEventFactory().onPlayerInteract(
                player, Action.RIGHT_CLICK_AIR, slot);

        if (event.useItemInHand() == null || event.useItemInHand() == Event.Result.DENY) {
            return;
        }

        if (!InventoryUtil.isEmpty(holding)) {
            ItemType type = ItemTable.instance().getItem(holding.getType());
            if (type != null) {
                if (type.getContext().isAirApplicable()) {
                    type.rightClickAir(player, holding);
                }
            }

            // Empties the user's inventory when the item is used up
            if (holding.getAmount() <= 0) {
                holding = InventoryUtil.createEmptyStack();
            }
            player.getInventory().setItem(slot, holding);
        }
    }
}
