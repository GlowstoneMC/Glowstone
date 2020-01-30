package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.jetbrains.annotations.NotNull;

public class GlowPistonBlockData extends AbstractBlockData implements GlowDirectional, Piston {

    public GlowPistonBlockData(Material material) {
        super(material, StateGenerator.EXTENDED, StateGenerator.SIX_FACING);
    }

    public BooleanStateValue getExtendedStateValue(){
        return (BooleanStateValue) this.getStateValue("extended");
    }

    @Override
    public boolean isExtended() {
        return this.getExtendedStateValue().getValue();
    }

    @Override
    public void setExtended(boolean b) {
        this.getExtendedStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowPistonBlockData piston = new GlowPistonBlockData(this.getMaterial());
        piston.setExtended(this.isExtended());
        piston.setFacing(this.getFacing());
        return piston;
    }
}
