package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.block.data.Rail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface GlowRail extends IBlockData, Rail {

    default EnumStateValue<Rail.Shape> getShapeStateValue(){
        return (EnumStateValue<Shape>) this.<Shape>getStateValue("shape").get();
    }

    @Override
    default Rail.Shape getShape(){
        return this.getShapeStateValue().getValue();
    }

    @Override
    default void setShape(Rail.Shape shape){
        this.getShapeStateValue().setValue(shape);
    }

    @Override
    default Set<Rail.Shape> getShapes(){
        return new HashSet<>(Arrays.asList(this.getShapeStateValue().getGenerator().getValues()));
    }
}
