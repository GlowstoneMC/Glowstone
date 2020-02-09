package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import net.glowstone.block.data.state.value.StateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Hopper;
import org.jetbrains.annotations.NotNull;

public class GlowHopperBlockData extends AbstractBlockData implements Hopper, GlowDirectional {

    public GlowHopperBlockData(Material material) {
        super(material, StateGenerator.SIX_FACING_DEFAULT_DOWN, StateGenerator.ENABLED);
    }

    public BooleanStateValue getEnabledStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("enabled").get();
    }

    @Override
    public boolean isEnabled() {
        return this.getEnabledStateValue().getValue();
    }

    @Override
    public void setEnabled(boolean b) {
        this.getEnabledStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowHopperBlockData hopper = new GlowHopperBlockData(this.getMaterial());
        hopper.setFacing(this.getFacing());
        hopper.setEnabled(this.isEnabled());
        return hopper;
    }
}
