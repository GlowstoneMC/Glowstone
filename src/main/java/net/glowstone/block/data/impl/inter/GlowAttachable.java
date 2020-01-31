package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Attachable;

public interface GlowAttachable extends IBlockData, Attachable {

    default BooleanStateValue getAttachedStateValue(){
        return (BooleanStateValue) this.<Boolean>getStateValue("attached").get();
    }

    @Override
    default boolean isAttached(){
        return this.getAttachedStateValue().getValue();
    }

    @Override
    default void setAttached(boolean state){
        this.getAttachedStateValue().setValue(state);
    }
}
