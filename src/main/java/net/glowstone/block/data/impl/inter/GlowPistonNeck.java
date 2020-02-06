package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.block.data.type.TechnicalPiston;

public interface GlowPistonNeck extends IBlockData, TechnicalPiston{

    default EnumStateValue<TechnicalPiston.Type> getTypeStateValue(){
        return (EnumStateValue<TechnicalPiston.Type>) this.<TechnicalPiston.Type>getStateValue("neck").get();
    }

    @Override
    default TechnicalPiston.Type getType(){
        return getTypeStateValue().getValue();
    }

    @Override
    default void setType(TechnicalPiston.Type type){
        this.getTypeStateValue().setValue(type);
    }
}
