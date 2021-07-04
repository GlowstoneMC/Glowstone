package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.constants.GameRules;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

import java.util.concurrent.ThreadLocalRandom;

public class BlockLava extends BlockLiquid {

    public BlockLava() {
        super(Material.LAVA_BUCKET);
    }

    @Override
    public boolean isCollectible(GlowBlockState target) {
        return (target.getType() == Material.LAVA || target.getType() == Material.STATIONARY_LAVA)
            &&
            (target.getRawData() == 0 || target.getRawData() == 8); // 8 for backwards compatibility
    }

    @Override
    public void updateBlock(GlowBlock block) {
        super.updateBlock(block);
        if (!block.getWorld().getGameRuleMap().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        int n = ThreadLocalRandom.current().nextInt(3);
        if (n == 0) {
            for (int i = 0; i < 3; i++) {
                GlowBlock b = (GlowBlock) block.getLocation()
                    .add(-1 + ThreadLocalRandom.current().nextInt(3), 0,
                        -1 + ThreadLocalRandom.current().nextInt(3)).getBlock();
                GlowBlock aboveB = b.getRelative(BlockFace.UP);
                if (aboveB.isEmpty() && b.isFlammable()) {
                    BlockIgniteEvent igniteEvent = new BlockIgniteEvent(aboveB, IgniteCause.LAVA,
                        block);
                    EventFactory.getInstance().callEvent(igniteEvent);
                    if (!igniteEvent.isCancelled()) {
                        GlowBlockState state = aboveB.getState();
                        state.setType(Material.FIRE);
                        state.update(true);
                    }
                }
            }
        } else {
            for (int i = 0; i < n; i++) {
                GlowBlock b = (GlowBlock) block.getLocation()
                    .add(-1 + ThreadLocalRandom.current().nextInt(3), 1,
                        -1 + ThreadLocalRandom.current().nextInt(3)).getBlock();
                if (b.isEmpty()) {
                    if (hasNearFlammableBlock(b)) {
                        BlockIgniteEvent igniteEvent = new BlockIgniteEvent(b, IgniteCause.LAVA,
                            block);
                        EventFactory.getInstance().callEvent(igniteEvent);
                        if (!igniteEvent.isCancelled()) {
                            GlowBlockState state = b.getState();
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
        for (BlockFace face : ADJACENT) {
            if (block.getRelative(face).isFlammable()) {
                return true;
            }
        }
        return false;
    }
}
