package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.state.GlowChest;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowChestInventory;
import net.glowstone.net.message.play.game.BlockActionMessage;
import net.glowstone.util.SoundUtil;
import org.bukkit.Sound;

/**
 * Block entity for Chests.
 */
public class ChestEntity extends ContainerEntity {

    private int viewers;

    public ChestEntity(GlowBlock block) {
        super(block, new GlowChestInventory(new GlowChest(block)));
        setSaveId("minecraft:chest");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowChest(block);
    }

    public void addViewer() {
        viewers++;
        if (viewers == 1) {
            updateInRange();
            SoundUtil
                .playSoundPitchRange(block.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.5F, 0.9F, 0.1F);
        }
    }

    public void removeViewer() {
        viewers--;
        if (viewers == 0) {
            updateInRange();
            SoundUtil.playSoundPitchRange(block.getLocation(), Sound.BLOCK_CHEST_CLOSE, 0.5F, 0.9F,
                0.1F);
        }
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);

        player.getSession().send(new BlockActionMessage(block.getX(), block.getY(), block.getZ(), 1,
            viewers == 0 ? 0 : 1, block.getTypeId()));
    }
}
