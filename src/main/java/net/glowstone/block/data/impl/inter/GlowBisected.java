package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.EnumStateValue;
import org.bukkit.block.data.Bisected;

public interface GlowBisected extends IBlockData, Bisected {

    default EnumStateValue<Bisected.Half> getHalfStateValue(){
        return (EnumStateValue<Half>) this.getStateValue("half").get();
    }

    @Override
    default Bisected.Half getHalf(){
        return this.getHalfStateValue().getValue();
    }

    @Override
    default void setHalf(Bisected.Half half){
        this.getHalfStateValue().setValue(half);
    }
}
