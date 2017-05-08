package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class BlockPiston extends BlockDirectional {
    private final boolean sticky;

    public BlockPiston() {
        this(false);
    }

    public BlockPiston(boolean sticky) {
        super(false);
        this.sticky = sticky;

        if (sticky) {
            setDrops(new ItemStack(Material.PISTON_STICKY_BASE));
        } else {
            setDrops(new ItemStack(Material.PISTON_BASE));
        }
    }

    /**
     * The piston is either non-sticky (default), or has a sticky behavior
     *
     * @return true if the piston has a sticky base
     */
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public void onRedstoneUpdate(GlowBlock block) {
        BlockFace face = BlockDirectional.getFace(block.getData());
        if (face == null) {
            return;
        }
        if (block.isBlockPowered() || block.isBlockIndirectlyPowered()) {
            // spawn some smoke because we won't be working on pistons until 1.13
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.3F, 1.0F);
            for (int i = 0; i < 3; i++) {
                block.getWorld().spawnParticle(Particle.SMOKE_LARGE, block.getLocation().clone().add(0.5, 0.8, 0.5), 0);
            }
        }
    }
}
