package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;
import org.jetbrains.annotations.NotNull;

public class GlowSoilBlockData extends AbstractBlockData implements Farmland {

    public GlowSoilBlockData(Material material) {
        super(material, StateGenerator.MOISTURE);
    }

    public IntegerStateValue.Ranged getMoistureStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("moisture").get();
    }

    @Override
    public int getMoisture() {
        return this.getMoistureStateValue().getValue();
    }

    @Override
    public void setMoisture(int i) {
        this.getMoistureStateValue().setValue(i);
    }

    @Override
    public int getMaximumMoisture() {
        return this.getMoistureStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSoilBlockData soil = new GlowSoilBlockData(this.getMaterial());
        soil.setMoisture(this.getMoisture());
        return soil;
    }
}
