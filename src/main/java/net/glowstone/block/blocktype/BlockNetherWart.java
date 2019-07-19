package net.glowstone.block.blocktype;

import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class BlockNetherWart extends BlockNeedsAttached {

    public BlockNetherWart() {
        setDrops(new ItemStack(Material.NETHER_WART, 1));
    }

    @Override
    public boolean canPlaceAt(GlowPlayer player, GlowBlock block, BlockFace against) {
        return block.getWorld().getBlockTypeAt(block.getX(), block.getY() - 1, block.getZ())
            == Material.SOUL_SAND;
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }

    @Override
    public void updateBlock(GlowBlock block) {
        int cropState = block.getData();
        if (cropState < NetherWartsState.RIPE.ordinal()
            && ThreadLocalRandom.current().nextInt(10) == 0) {
            cropState++;
            GlowBlockState state = block.getState();
            state.setRawData((byte) cropState);
            BlockGrowEvent growEvent = new BlockGrowEvent(block, state);
            EventFactory.getInstance().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                state.update(true);
            }
        }
    }
}
