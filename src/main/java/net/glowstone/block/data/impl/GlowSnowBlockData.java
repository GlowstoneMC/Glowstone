package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowLevelled;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
import org.jetbrains.annotations.NotNull;

public class GlowSnowBlockData extends AbstractBlockData implements Snow {

    public GlowSnowBlockData(Material material) {
        super(material, StateGenerator.EIGHT_LAYERS);
    }

    public IntegerStateValue.Ranged getLayerStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("layers").get();
    }

    @Override
    public int getLayers() {
        return this.getLayerStateValue().getValue();
    }

    @Override
    public void setLayers(int i) {
        this.getLayerStateValue().setValue(i);
    }

    @Override
    public int getMinimumLayers() {
        return this.getLayerStateValue().getGenerator().getMinimum();
    }

    @Override
    public int getMaximumLayers() {
        return this.getLayerStateValue().getGenerator().getMaximum();
    }

    @Override
    public @NotNull BlockData clone() {
        GlowSnowBlockData data = new GlowSnowBlockData(this.getMaterial());
        data.setLayers(this.getLayers());
        return data;
    }
}
