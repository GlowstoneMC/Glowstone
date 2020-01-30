package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowRail;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowRailBlockData extends AbstractBlockData implements GlowRail {

    public GlowRailBlockData(Material material) {
        super(material, StateGenerator.RAIL_SHAPE);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRailBlockData rail = new GlowRailBlockData(this.getMaterial());
        rail.setShape(this.getShape());
        return rail;
    }
}
