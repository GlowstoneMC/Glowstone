package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.scheduler.PulseTask;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BlockObserver extends BlockDirectional {

    private static final int POWERED_MASK = 0x3;

    public BlockObserver() {
        super(true);
        setDrops(new ItemStack(Material.OBSERVER));
    }

    public static boolean isPowered(GlowBlock block) {
        return block.getType() == Material.OBSERVER && ((block.getData() >> POWERED_MASK) & 1) == 1;
    }

    /**
     * Returns the direction the given block is facing, if it's an observer, or null otherwise.
     *
     * @param block a block
     * @return the direction the block is facing, or null if the block isn't an observer
     */
    public static BlockFace getFace(GlowBlock block) {
        if (block.getType() != Material.OBSERVER) {
            return null;
        }
        byte data = (byte) (block.getData() & ~(1 << POWERED_MASK));
        return getFace(data);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
                                   Material oldType, byte oldData, Material newType, byte newData) {
        byte data = (byte) (block.getData() & ~(1 << POWERED_MASK));
        if (data != block.getData()) {
            return;
        }
        BlockFace direction = getFace(data);
        if (face != direction) {
            return;
        }
        block.setData((byte) (block.getData() | (1 << POWERED_MASK)));
        block.getWorld().requestPulse(block);
        new PulseTask(block, true, 4, true).startPulseTask();
    }

    @Override
    public void receivePulse(GlowBlock block) {
        byte data = (byte) (block.getData() & ~(1 << POWERED_MASK));
        block.setData(data);
    }
}
