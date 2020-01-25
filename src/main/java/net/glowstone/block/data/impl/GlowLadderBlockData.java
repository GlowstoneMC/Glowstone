package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Ladder;
import org.jetbrains.annotations.NotNull;

public class GlowLadderBlockData extends AbstractBlockData implements GlowDirectional, GlowWaterlogged, Ladder {

    public GlowLadderBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.WATER_LOGGED);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowLadderBlockData ladder = new GlowLadderBlockData(this.getMaterial());
        ladder.setFacing(this.getFacing());
        ladder.setWaterlogged(this.isWaterlogged());
        return ladder;
    }
}
