package net.glowstone.block.blocktype;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;

public class BlockMycel extends BlockType {

    public BlockMycel() {
        setDrops(new ItemStack(Material.DIRT, 1));
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        GlowBlock blockAbove = block.getRelative(BlockFace.UP);
        if (blockAbove.getLightLevel() < 4
            && blockAbove.getMaterialValues().getLightOpacity() > 2) {
            // mycel block turns into dirt block
            GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.getInstance().callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        } else if (blockAbove.getLightLevel() >= 9) {
            GlowWorld world = block.getWorld();
            int sourceX = block.getX();
            int sourceY = block.getY();
            int sourceZ = block.getZ();

            // mycel spread randomly around
            for (int i = 0; i < 4; i++) {
                int x = sourceX + ThreadLocalRandom.current().nextInt(3) - 1;
                int z = sourceZ + ThreadLocalRandom.current().nextInt(3) - 1;
                int y = sourceY + ThreadLocalRandom.current().nextInt(5) - 3;

                GlowBlock targetBlock = world.getBlockAt(x, y, z);
                GlowBlock targetAbove = targetBlock.getRelative(BlockFace.UP);
                if (targetBlock.getType() == Material.DIRT
                    && targetBlock.getData() == 0 // only spread on normal dirt
                    && targetAbove.getMaterialValues().getLightOpacity() <= 2
                    && targetAbove.getLightLevel() >= 4) {
                    GlowBlockState state = targetBlock.getState();
                    state.setType(Material.MYCEL);
                    state.setRawData((byte) 0);
                    BlockSpreadEvent spreadEvent = new BlockSpreadEvent(targetBlock, block, state);
                    EventFactory.getInstance().callEvent(spreadEvent);
                    if (!spreadEvent.isCancelled()) {
                        state.update(true);
                    }
                }
            }
        }
    }
}
