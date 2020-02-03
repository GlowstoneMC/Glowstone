package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.BooleanStateValue;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.jetbrains.annotations.NotNull;

public class GlowLeavesBlockData extends AbstractBlockData implements Leaves {

    public GlowLeavesBlockData(Material material) {
        super(material, StateGenerator.PERSISTENT, StateGenerator.EIGHT_DISTANCE);
    }

    public BooleanStateValue getPersistentStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("persistent").get();
    }

    public IntegerStateValue.Ranged getDistanceStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("distance").get();
    }

    @Override
    public boolean isPersistent() {
        return this.getPersistentStateValue().getValue();
    }

    @Override
    public void setPersistent(boolean b) {
        this.getPersistentStateValue().setValue(b);
    }

    @Override
    public int getDistance() {
        return this.getDistanceStateValue().getValue();
    }

    @Override
    public void setDistance(int i) {
this.getDistanceStateValue().setValue(i);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowLeavesBlockData data = new GlowLeavesBlockData(this.getMaterial());
        data.setDistance(this.getDistance());
        data.setPersistent(this.isPersistent());
        return data;
    }
}
