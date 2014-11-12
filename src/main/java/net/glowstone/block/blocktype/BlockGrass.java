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

public class BlockGrass extends BlockType {

    public BlockGrass() {
        setDrops(new ItemStack(Material.DIRT, 1));
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (block.getLightLevel() < 4 ||
                block.getRelative(BlockFace.UP).getType().isOccluding()) { // temp fix for light level
            // grass block turns into dirt block
            final GlowBlockState state = block.getState();
            state.setType(Material.DIRT);
            BlockFadeEvent fadeEvent = new BlockFadeEvent(block, state);
            EventFactory.callEvent(fadeEvent);
            if (!fadeEvent.isCancelled()) {
                state.update(true);
            }
        } else if (block.getLightLevel() >= 9) {
            final GlowWorld world = block.getWorld();
            int sourceX = block.getX();
            int sourceY = block.getY();
            int sourceZ = block.getZ();

            // grass spread randomly around
            for (int i = 0; i < 4; i++) {
                int x = sourceX + random.nextInt(3) - 1;
                int z = sourceZ + random.nextInt(3) - 1;
                int y = sourceY + random.nextInt(5) - 3;

                final GlowBlock targetBlock = world.getBlockAt(x, y, z);
                if (targetBlock.getType() == Material.DIRT &&
                        targetBlock.getData() == 0 && // only spread on normal dirt
                        !targetBlock.getRelative(BlockFace.UP).getType().isOccluding() && // temp fix for light level
                        targetBlock.getRelative(BlockFace.UP).getType() != Material.WATER && // temp fix for light level
                        targetBlock.getRelative(BlockFace.UP).getType() != Material.STATIONARY_WATER && // temp fix for light level
                        targetBlock.getRelative(BlockFace.UP).getLightLevel() >= 4) {
                    final GlowBlockState state = targetBlock.getState();
                    state.setType(Material.GRASS);
                    state.setRawData((byte) 0);
                    BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                    EventFactory.callEvent(spreadEvent);
                    if (!spreadEvent.isCancelled()) {
                        state.update(true);
                    }
                }
            }
        }
    }
}
