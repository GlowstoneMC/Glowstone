package net.glowstone.block.blocktype;

import org.bukkit.Material;
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
}
