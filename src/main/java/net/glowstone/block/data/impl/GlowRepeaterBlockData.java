package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowPowered;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Repeater;
import org.jetbrains.annotations.NotNull;

public class GlowRepeaterBlockData extends AbstractBlockData implements GlowPowered, GlowDirectional, Repeater {

    public GlowRepeaterBlockData(Material material) {
        super(material, StateGenerator.POWERED, StateGenerator.FOUR_FACING);
    }

    public IntegerStateValue.Ranged getDelayStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("delay");
    }

    public BooleanStateValue getLockedStateValue(){
        return (BooleanStateValue) this.getStateValue("locked");
    }

    @Override
    public int getDelay() {
        return this.getDelayStateValue().getValue();
    }

    @Override
    public void setDelay(int i) {
        this.getDelayStateValue().setValue(i);
    }

    @Override
    public int getMinimumDelay() {
        return this.getDelayStateValue().getGenerator().getMinimum();
    }

    @Override
    public int getMaximumDelay() {
        return this.getDelayStateValue().getGenerator().getMaximum();
    }

    @Override
    public boolean isLocked() {
        return this.getLockedStateValue().getValue();
    }

    @Override
    public void setLocked(boolean b) {
        this.getLockedStateValue().setValue(b);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowRepeaterBlockData repeater = new GlowRepeaterBlockData(this.getMaterial());
        repeater.setDelay(this.getDelay());
        repeater.setLocked(this.isLocked());
        repeater.setFacing(this.getFacing());
        repeater.setPowered(this.isPowered());
        return repeater;
    }

}
