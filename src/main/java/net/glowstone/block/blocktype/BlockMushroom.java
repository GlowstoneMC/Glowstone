package net.glowstone.block.blocktype;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockSpreadEvent;

import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;

public class BlockMushroom extends BlockNeedsAttached {

    private final Material mushroomType;

    public BlockMushroom(Material mushroomType) {
        this.mushroomType = mushroomType;
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (random.nextInt(25) == 0) {
            final GlowWorld world = block.getWorld();
            int x, y, z;
            int i = 0;
            for (x = block.getX() - 4; x <= block.getX() + 4; x++) {
                for (z = block.getZ() - 4; z <= block.getZ() + 4; z++) {
                    for (y = block.getY() - 1; y <= block.getY() + 1; y++) {
                        if (world.getBlockAt(x, y, z).getType() == mushroomType) {
                            if (++i > 4) {
                                return;
                            }
                        }
                    }
                }
            }

            int nX, nY, nZ;
            nX = block.getX() + random.nextInt(3) - 1;
            nY = block.getY() + random.nextInt(2) - random.nextInt(2);
            nZ = block.getZ() + random.nextInt(3) - 1;

            x = block.getX(); y = block.getY(); z = block.getZ();
            for (i = 0; i < 4; i++) {
                if (world.getBlockAt(nX, nY, nZ).getType() == Material.AIR
                        && canPlaceAt(world.getBlockAt(nX, nY, nZ), BlockFace.DOWN)) {
                    x = nX; y = nY; z = nZ;
                }
                nX = x + random.nextInt(3) - 1;
                nY = y + random.nextInt(2) - random.nextInt(2);
                nZ = z + random.nextInt(3) - 1;
            }

            if (world.getBlockAt(nX, nY, nZ).getType() == Material.AIR
                    && canPlaceAt(world.getBlockAt(nX, nY, nZ), BlockFace.DOWN)) {
                final GlowBlockState state = world.getBlockAt(nX, nY, nZ).getState();
                state.setType(mushroomType);
                BlockSpreadEvent spreadEvent = new BlockSpreadEvent(state.getBlock(), block, state);
                EventFactory.callEvent(spreadEvent);
                if (!spreadEvent.isCancelled()) {
                    state.update(true);
                }
            }
        }
    }
}
