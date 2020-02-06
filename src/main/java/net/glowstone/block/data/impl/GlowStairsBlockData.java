package net.glowstone.block.data.impl;

import net.glowstone.block.data.AbstractBlockData;
import net.glowstone.block.data.impl.inter.GlowBisected;
import net.glowstone.block.data.impl.inter.GlowDirectional;
import net.glowstone.block.data.impl.inter.GlowWaterlogged;
import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.jetbrains.annotations.NotNull;

public class GlowStairsBlockData extends AbstractBlockData implements GlowBisected, GlowDirectional, GlowWaterlogged, Stairs {

    public GlowStairsBlockData(Material material) {
        super(material, StateGenerator.WATER_LOGGED, StateGenerator.FOUR_FACING, StateGenerator.HALF, StateGenerator.STAIRS_SHAPE);
    }

    public EnumStateValue<Stairs.Shape> getShapeStateValue(){
        return (EnumStateValue<Shape>) this.<Shape>getStateValue("shape").get();
    }

    @Override
    public @NotNull Shape getShape() {
        return this.getShapeStateValue().getValue();
    }

    @Override
    public void setShape(@NotNull Shape shape) {
        this.getShapeStateValue().setValue(shape);
    }

    @Override
    public @NotNull BlockData clone() {
        GlowStairsBlockData stairs = new GlowStairsBlockData(this.getMaterial());
        stairs.setShape(this.getShape());
        stairs.setFacing(this.getFacing());
        stairs.setHalf(this.getHalf());
        stairs.setWaterlogged(this.isWaterlogged());
        return stairs;
    }
}
