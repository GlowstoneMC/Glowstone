package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Dispenser;
import org.jetbrains.annotations.NotNull;

public class GlowDispenserBlockData extends AbstractBlockData implements GlowDirectional, Dispenser {

    public GlowDispenserBlockData(Material material) {
        super(material, StateGenerator.TRIGGERED, StateGenerator.SIX_FACING);
    }

    public BooleanStateValue getTriggeredStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("triggered").get();
    }

    @Override
    public boolean isTriggered() {
        return this.getTriggeredStateValue().getValue();
    }

    @Override
    public void setTriggered(boolean b) {
        this.getTriggeredStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowDispenserBlockData dispenser = new GlowDispenserBlockData(this.getMaterial());
        dispenser.setTriggered(this.isTriggered());
        dispenser.setFacing(this.getFacing());
        return dispenser;
    }
}
