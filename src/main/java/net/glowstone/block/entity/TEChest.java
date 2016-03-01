package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowChest;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowChestInventory;
import net.glowstone.net.message.play.game.BlockActionMessage;
import org.bukkit.Sound;

/**
 * Tile entity for Chests.
 */
public class TEChest extends TEContainer {

    private int viewers = 0;

    public TEChest(GlowBlock block) {
        super(block, new GlowChestInventory(new GlowChest(block)));
        setSaveId("Chest");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowChest(block);
    }

    public void addViewer() {
        viewers++;
        if (viewers == 1) {
            updateInRange();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_CHEST_OPEN, 5f, 2f);
        }
    }

    public void removeViewer() {
        viewers--;
        if (viewers == 0) {
            updateInRange();
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_CHEST_CLOSE, 5f, 2f);
        }
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);

        player.getSession().send(new BlockActionMessage(block.getX(), block.getY(), block.getZ(), 1, viewers == 0 ? 0 : 1, block.getTypeId()));
    }
}
