package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPistonNeck;
import net.glowstone.block.data.state.generator.StateGenerator;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import javax.swing.plaf.nimbus.State;

public class GlowPistonMovingBlockData extends AbstractBlockData implements GlowDirectional, GlowPistonNeck {

    public GlowPistonMovingBlockData(Material material) {
        super(material, StateGenerator.PISTON_NECK_TYPE, StateGenerator.SIX_FACING_DEFAULT_NORTH);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowPistonMovingBlockData moving = new GlowPistonMovingBlockData(this.getMaterial());
        moving.setType(this.getType());
        moving.setFacing(this.getFacing());
        return moving;
    }
}
