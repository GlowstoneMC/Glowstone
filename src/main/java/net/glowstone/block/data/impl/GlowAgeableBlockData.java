package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class GlowAgeableBlockData extends AbstractBlockData implements Ageable {

    public GlowAgeableBlockData(Material material, IntegerStateGenerator.Ranged array) {
        super(material, array);
    }

    public GlowAgeableBlockData(Material material, IntegerStateValue.Ranged... array) {
        super(material, array);
    }

    public GlowAgeableBlockData(Material material, Collection<StateValue<?>> collection) {
        super(material, collection);
    }

    public IntegerStateValue.Ranged getAgeStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("age");
    }

    @Override
    public int getAge() {
        return this.getAgeStateValue().getValue();
    }

    @Override
    public void setAge(int i) {
        this.getAgeStateValue().setValue(i);
    }

    @Override
    public int getMaximumAge() {
        return this.getAgeStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowAgeableBlockData(this.getMaterial(), this.getAgeStateValue().clone());
    }
}
