package net.glowstone.block.blocktype;

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
        if (block.getLightLevel() < 4 ||
                block.getRelative(BlockFace.UP).getMaterialValues().getLightOpacity() > 2) {
            // mycel block turns into dirt block
            GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        } else if (block.getLightLevel() >= 9) {
            GlowWorld world = block.getWorld();
            int sourceX = block.getX();
            int sourceY = block.getY();
            int sourceZ = block.getZ();

            // mycel spread randomly around
            for (int i = 0; i < 4; i++) {
                int x = sourceX + random.nextInt(3) - 1;
                int z = sourceZ + random.nextInt(3) - 1;
                int y = sourceY + random.nextInt(5) - 3;

                GlowBlock targetBlock = world.getBlockAt(x, y, z);
                if (targetBlock.getType() == Material.DIRT &&
                        targetBlock.getData() == 0 && // only spread on normal dirt
                        targetBlock.getRelative(BlockFace.UP).getMaterialValues().getLightOpacity() <= 2 &&
                        targetBlock.getRelative(BlockFace.UP).getLightLevel() >= 4) {
                    GlowBlockState state = targetBlock.getState();
                    state.setType(Material.MYCEL);
                    state.setRawData((byte) 0);
                    BlockSpreadEvent spreadEvent = new BlockSpreadEvent(targetBlock, block, state);
                    EventFactory.callEvent(spreadEvent);
                    if (!spreadEvent.isCancelled()) {
                        state.update(true);
                    }
                }
            }
        }
    }
}
