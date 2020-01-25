package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowLightable;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Furnace;
import org.jetbrains.annotations.NotNull;

public class GlowFurnaceBlockData extends AbstractBlockData implements GlowLightable, GlowDirectional, Furnace {

    public GlowFurnaceBlockData(Material material) {
        super(material, StateGenerator.LIT, StateGenerator.FOUR_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowFurnaceBlockData furnace = new GlowFurnaceBlockData(this.getMaterial());
        furnace.setLit(this.isLit());
        furnace.setFacing(this.getFacing());
        return furnace;
    }
}
