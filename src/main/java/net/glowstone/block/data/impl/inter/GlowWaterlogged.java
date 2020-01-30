package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Waterlogged;

public interface GlowWaterlogged extends IBlockData, Waterlogged {

    default BooleanStateValue getWaterLoggedValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("waterlogged").get();
    }

    @Override
    default boolean isWaterlogged() {
        return this.getWaterLoggedValue().getValue();
    }

    @Override
    default void setWaterlogged(boolean b) {
        this.getWaterLoggedValue().setValue(b);
    }

}
