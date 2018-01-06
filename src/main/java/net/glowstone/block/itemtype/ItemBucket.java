package net.glowstone.block.itemtype;

import java.util.Iterator;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockLiquid;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class ItemBucket extends ItemType {

    public ItemBucket() {
        setMaxStackSize(16);
    }

    @Override
    public Context getContext() {
        return Context.ANY;
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        clickBucket(player, holding);
    }

    @Override
    public void rightClickBlock(
            GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding,
            Vector clickedLoc, EquipmentSlot hand) {
        clickBucket(player, holding);
    }

    private void clickBucket(GlowPlayer player, ItemStack holding) {
        Iterator<Block> itr = new BlockIterator(player, 5);
        Block target = null;
        // Used to determine the side the block was clicked on:
        Block previousTarget = null;
        BlockType targetBlockType = null;
        boolean validTarget = false;

        // Find the next available non-air liquid block type which is collectible in a radius of 5
        // blocks
        while (itr.hasNext()) {
            previousTarget = target;
            target = itr.next();
            targetBlockType = ItemTable.instance().getBlock(target.getType());
            if (targetBlockType instanceof BlockLiquid) {
                if (((BlockLiquid) targetBlockType)
                    .isCollectible((GlowBlockState) target.getState())) {
                    validTarget = true;
                    break;
                }
            } else if (!target.isEmpty()) {
                break;
            }
        }

        if (target != null && validTarget) {
            // Get the direction of the bucket fill
            BlockFace face;
            if (previousTarget != null) {
                face = target.getFace(previousTarget);
            } else {
                face = BlockFace.SELF;
            }

            Material replaceWith = ((BlockLiquid) targetBlockType).getBucketType();

            PlayerBucketFillEvent event = EventFactory.callEvent(
                new PlayerBucketFillEvent(player, target, face, holding.getType(), holding));
            if (event.isCancelled()) {
                return;
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (holding.getAmount() == 1) {
                    holding.setType(replaceWith);
                } else {
                    holding.setAmount(holding.getAmount() - 1);
                    player.getInventory().addItem(new ItemStack(replaceWith));
                }
            }

            target.setType(Material.AIR);
        }
    }
}
