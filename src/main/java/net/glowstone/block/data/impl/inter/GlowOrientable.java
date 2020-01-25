package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.Axis;
import org.bukkit.block.data.Orientable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface GlowOrientable extends IBlockData, Orientable {

    default EnumStateValue<Axis> getAxisStateValue(){
        return (EnumStateValue<Axis>) this.getStateValue("axis");
    }

    @Override
    default Axis getAxis(){
        return this.getAxisStateValue().getValue();
    }

    @Override
    default void setAxis(Axis axis){
        this.getAxisStateValue().setValue(axis);
    }

    @Override
    default Set<Axis> getAxes(){
        return new HashSet<>(Arrays.asList(this.getAxisStateValue().getGenerator().getValues()));
    }
}
