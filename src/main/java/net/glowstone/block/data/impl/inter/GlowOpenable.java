package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Openable;

public interface GlowOpenable extends IBlockData, Openable {

    default BooleanStateValue getOpenStateValue(){
        return (BooleanStateValue) this.getStateValue("open");
    }

    @Override
    default boolean isOpen(){
        return this.getOpenStateValue().getValue();
    }

    @Override
    default void setOpen(boolean open){
        this.getOpenStateValue().setValue(open);
    }
}
