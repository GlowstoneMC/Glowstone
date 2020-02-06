package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowBisected;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.type.Slab;
import org.jetbrains.annotations.NotNull;

public class GlowSlabBlockData extends AbstractBlockData implements GlowWaterlogged, GlowBisected, Slab {

    public GlowSlabBlockData(Material material) {
        super(material, StateGenerator.HALF, StateGenerator.WATER_LOGGED, StateGenerator.SLAB_TYPE);
    }

    public EnumStateValue<Slab.Type> getTypeStateValue(){
        return (EnumStateValue<Type>) this.<Type>getStateValue("type").get();
    }

    @Override
    public @NotNull Type getType() {
        return this.getTypeStateValue().getValue();
    }

    @Override
    public void setType(@NotNull Type type) {
        this.getTypeStateValue().setValue(type);
    }

    @Override
    public GlowSlabBlockData clone(){
        GlowSlabBlockData data = new GlowSlabBlockData(this.getMaterial());
        data.setHalf(this.getHalf());
        data.setWaterlogged(this.isWaterlogged());
        return data;
    }
}
