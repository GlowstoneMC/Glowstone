package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

public class BlockLava extends BlockLiquid {

    private static final BlockFace[] FLAMMABLE_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};

    public BlockLava() {
        super(Material.LAVA_BUCKET);
    }

    @Override
    public boolean isCollectible(GlowBlockState target) {
        return (target.getType() == Material.LAVA || target.getType() == Material.STATIONARY_LAVA) &&
                (target.getRawData() == 0 || target.getRawData() == 8); // 8 for backwards compatibility
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        if (!block.getWorld().getGameRuleMap().getBoolean("doFireTick")) {
            return;
        }
        final int n = random.nextInt(3);
        if (n == 0) {
            for (int i = 0; i < 3; i++) {
                final GlowBlock b = (GlowBlock) block.getLocation().add(-1 + random.nextInt(3), 0, -1 + random.nextInt(3)).getBlock();
                final GlowBlock bAbove = b.getRelative(BlockFace.UP);
                if (bAbove.isEmpty() && b.isFlammable()) {
                    BlockIgniteEvent igniteEvent = new BlockIgniteEvent(bAbove, IgniteCause.LAVA, block);
                    EventFactory.callEvent(igniteEvent);
                    if (!igniteEvent.isCancelled()) {
                        final GlowBlockState state = bAbove.getState();
                        state.setType(Material.FIRE);
                        state.update(true);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                final GlowBlock b = (GlowBlock) block.getLocation().add(-1 + random.nextInt(3), 1, -1 + random.nextInt(3)).getBlock();
                if (b.isEmpty()) {
                    if (hasNearFlammableBlock(b)) {
                        BlockIgniteEvent igniteEvent = new BlockIgniteEvent(b, IgniteCause.LAVA, block);
                        EventFactory.callEvent(igniteEvent);
                        if (!igniteEvent.isCancelled()) {
                            final GlowBlockState state = b.getState();
                            state.setType(Material.FIRE);
                            state.update(true);
                        }
                        break;
                    }
                } else if (b.getType().isSolid()) {
                    break;
                }
            }
        }
    }

    private boolean hasNearFlammableBlock(GlowBlock block) {
        // check there's at least a flammable block around
        for (BlockFace face : FLAMMABLE_FACES) {
            if (block.getRelative(face).isFlammable()) {
                return true;
            }
        }
        return false;
    }
}
