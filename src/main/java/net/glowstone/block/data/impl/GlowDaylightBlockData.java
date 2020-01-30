package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowAnalogPowered;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.DaylightDetector;
import org.jetbrains.annotations.NotNull;

public class GlowDaylightBlockData extends AbstractBlockData implements GlowAnalogPowered, DaylightDetector {

    public GlowDaylightBlockData(Material material) {
        super(material, StateGenerator.INVERTED, StateGenerator.REDSTONE_POWER);
    }

    public BooleanStateValue getInvertedStateValue(){
        return (BooleanStateValue) this.getStateValue("inverted");
    }

    @Override
    public boolean isInverted() {
        return this.getInvertedStateValue().getValue();
    }

    @Override
    public void setInverted(boolean b) {
        this.getInvertedStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowDaylightBlockData daylight = new GlowDaylightBlockData(this.getMaterial());
        daylight.setInverted(this.isInverted());
        daylight.setPower(this.getPower());
        return daylight;
    }
}
