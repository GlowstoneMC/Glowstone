package net.glowstone.block.blocktype;

import net.glowstone.inventory.ToolType;
import org.bukkit.Material;

public class BlockMagma extends BlockDirectDrops {

    public BlockMagma() {
        super(Material.MAGMA, ToolType.PICKAXE);
        addFunction(Functions.Step.MAGMA);
    }
}
