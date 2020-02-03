package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.TurtleEgg;
import org.jetbrains.annotations.NotNull;

public class GlowTurtleEggBlockData extends AbstractBlockData implements TurtleEgg {

    public GlowTurtleEggBlockData(Material material) {
        super(material, StateGenerator.EGGS, StateGenerator.HATCH);
    }

    public IntegerStateValue.Ranged getEggsStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("eggs").get();
    }

    public IntegerStateValue.Ranged getHatchedStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("hatch").get();
    }

    @Override
    public int getEggs() {
        return this.getEggsStateValue().getValue();
    }

    @Override
    public void setEggs(int i) {
        this.getEggsStateValue().setValue(i);
    }

    @Override
    public int getMinimumEggs() {
        return this.getEggsStateValue().getGenerator().getMinimum();
    }

    @Override
    public int getMaximumEggs() {
        return this.getEggsStateValue().getGenerator().getMaximum();
    }

    @Override
    public int getHatch() {
        return this.getHatchedStateValue().getValue();
    }

    @Override
    public void setHatch(int i) {
        this.getHatchedStateValue().setValue(i);
    }

    @Override
    public int getMaximumHatch() {
        return this.getHatchedStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowTurtleEggBlockData egg = new GlowTurtleEggBlockData(this.getMaterial());
        egg.setEggs(this.getEggs());
        egg.setHatch(this.getHatch());
        return egg;
    }
}
