package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowLightable;
import net.glowstone.block.data.state.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.RedstoneWallTorch;
import org.jetbrains.annotations.NotNull;

public class GlowTorchBlockData extends AbstractBlockData implements GlowDirectional, GlowLightable, RedstoneWallTorch {

    public GlowTorchBlockData(Material material) {
        super(material, StateGenerator.LIT, StateGenerator.FOUR_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTorchBlockData data = new GlowTorchBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setLit(this.isLit());
        return data;
    }
}
