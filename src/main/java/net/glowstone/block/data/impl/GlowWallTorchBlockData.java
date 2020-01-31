package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowLightable;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

public class GlowWallTorchBlockData extends AbstractBlockData implements GlowDirectional, GlowLightable {

    public GlowWallTorchBlockData(Material material) {
        super(material, StateGenerator.LIT, StateGenerator.FOUR_FACING);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowWallTorchBlockData data = new GlowWallTorchBlockData(this.getMaterial());
        data.setFacing(this.getFacing());
        data.setLit(this.isLit());
        return data;
    }
}
