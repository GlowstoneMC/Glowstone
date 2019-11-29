package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class BlockSnow extends BlockNeedsAttached {

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        // can absorb snow layers if non-full, or all blocks if single layer
        return holding.getType() == Material.SNOW && block.getData() < 7 || block.getData() == 0;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        // can always be overridden by more snow or any other block
        return true;
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        // note: does not emulate certain weird broken Vanilla behaviors,
        // such as placing snow an extra block away from where it should

        if (state.getType() == Material.SNOW) {
            byte data = state.getRawData();

            // add another snow layer if possible
            if (data < 6) {
                state.setRawData((byte) (data + 1));

            // set to snow block if high enough
            } else {
                state.setType(Material.SNOW_BLOCK);
            }
        } else {
            // place first snow layer
            state.setType(Material.SNOW);
        }
    }

    @NotNull
    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        if (tool != null && ToolType.SHOVEL.matches(tool.getType())) {
            return Arrays.asList(new ItemStack(Material.SNOWBALL, block.getData() + 1));
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
