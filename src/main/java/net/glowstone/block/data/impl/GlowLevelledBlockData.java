package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.jetbrains.annotations.NotNull;

public class GlowLevelledBlockData extends AbstractBlockData implements Levelled {

    public GlowLevelledBlockData(Material material, IntegerStateGenerator.Ranged level) {
        super(material, level);
    }

    public GlowLevelledBlockData(Material material, IntegerStateValue.Ranged level){
        super(material, level);
    }

    public IntegerStateValue.Ranged getLevelStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("level");
    }

    @Override
    public int getLevel() {
        return this.getLevelStateValue().getValue();
    }

    @Override
    public void setLevel(int i) {
        this.getLevelStateValue().setValue(i);
    }

    @Override
    public int getMaximumLevel() {
        return this.getLevelStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        return new GlowLevelledBlockData(this.getMaterial(), this.getLevelStateValue());
    }
}
