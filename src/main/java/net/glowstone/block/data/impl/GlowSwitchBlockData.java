package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Ladder;
import org.jetbrains.annotations.NotNull;

public class GlowSwitchBlockData extends AbstractBlockData implements GlowDirectional, GlowPowered, GlowWaterlogged, Ladder {

    public GlowSwitchBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.POWERED, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSwitchBlockData switchData = new GlowSwitchBlockData(this.getMaterial());
        switchData.setFacing(this.getFacing());
        switchData.setPowered(this.isPowered());
        switchData.setWaterlogged(this.isWaterlogged());
        return switchData;
    }
}
