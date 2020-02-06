package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowOpenable;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Gate;
import org.jetbrains.annotations.NotNull;

public class GlowGateBlockData extends AbstractBlockData implements Gate, GlowDirectional, GlowOpenable, GlowPowered {

    public GlowGateBlockData(Material material) {
        super(material, StateGenerator.FOUR_FACING, StateGenerator.IN_WALL, StateGenerator.OPEN, StateGenerator.POWERED);
    }

    public BooleanStateValue getInWallStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("in_wall").get();
    }

    @Override
    public boolean isInWall() {
        return this.getInWallStateValue().getValue();
    }

    @Override
    public void setInWall(boolean b) {
        this.getInWallStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowGateBlockData data = new GlowGateBlockData(this.getMaterial());
        data.setInWall(this.isInWall());
        data.setFacing(this.getFacing());
        data.setOpen(this.isOpen());
        data.setPowered(this.isPowered());
        return data;
    }
}
