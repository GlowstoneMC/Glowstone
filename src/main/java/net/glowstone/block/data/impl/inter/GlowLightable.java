package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Lightable;

public interface GlowLightable extends IBlockData, Lightable {

    default BooleanStateValue getLitStateValue(){
        return (BooleanStateValue) this.getStateValue("lit").get();
    }

    @Override
    default boolean isLit(){
        return this.getLitStateValue().getValue();
    }

    @Override
    default void setLit(boolean b){
        this.getLitStateValue().setValue(b);
    }
}
