package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.jetbrains.annotations.NotNull;

public class GlowSaplingBlockData extends AbstractBlockData implements Sapling {

    public GlowSaplingBlockData(Material material) {
        super(material, StateGenerator.TWO_STAGE);
    }

    public IntegerStateValue.Ranged getStagedStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("stage").get();
    }

    @Override
    public int getStage() {
        return this.getStagedStateValue().getValue();
    }

    @Override
    public void setStage(int i) {
        this.getStagedStateValue().setValue(i);
    }

    @Override
    public int getMaximumStage() {
        return this.getStagedStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSaplingBlockData sapling = new GlowSaplingBlockData(this.getMaterial());
        sapling.setStage(this.getStage());
        return sapling;
    }
}
