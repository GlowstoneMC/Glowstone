package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class BlockSnowBlock extends BlockType {

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (tool != null && ToolType.SPADE.matches(tool.getType())) {
            return Arrays.asList(new ItemStack(Material.SNOW_BALL, 4));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (block.getLightFromBlocks() > 11) {
            GlowBlockState state = block.getState();
            state.setType(Material.AIR);
            state.setData(new MaterialData(Material.AIR));
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.getInstance().callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        }
    }
}
