package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SeaPickle;
import org.jetbrains.annotations.NotNull;

public class GlowSeaPickleBlockData extends AbstractBlockData implements GlowWaterlogged, SeaPickle {

    public GlowSeaPickleBlockData(Material material) {
        super(material, StateGenerator.PICKLES, StateGenerator.WATER_LOGGED);
    }

    public IntegerStateValue.Ranged getPicklesStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("pickles").get();
    }

    @Override
    public int getPickles() {
        return this.getPicklesStateValue().getValue();
    }

    @Override
    public void setPickles(int i) {
        this.getPicklesStateValue().setValue(i);
    }

    @Override
    public int getMinimumPickles() {
        return this.getPicklesStateValue().getGenerator().getMinimum();
    }

    @Override
    public int getMaximumPickles() {
        return this.getPicklesStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSeaPickleBlockData pickles = new GlowSeaPickleBlockData(this.getMaterial());
        pickles.setPickles(this.getPickles());
        pickles.setWaterlogged(this.isWaterlogged());
        return pickles;
    }
}
