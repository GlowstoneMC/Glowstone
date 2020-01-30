package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.impl.inter.GlowRail;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneRail;
import org.jetbrains.annotations.NotNull;

public class GlowRedstoneRailBlockData extends AbstractBlockData implements GlowRail, GlowPowered, RedstoneRail {

    public GlowRedstoneRailBlockData(Material material) {
        super(material, StateGenerator.POWERED, StateGenerator.RAIL_SHAPE);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRedstoneRailBlockData rail = new GlowRedstoneRailBlockData(this.getMaterial());
        rail.setShape(this.getShape());
        rail.setPowered(this.isPowered());
        return rail;
    }
}
