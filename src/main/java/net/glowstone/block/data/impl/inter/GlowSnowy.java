package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Snowable;

public interface GlowSnowy extends IBlockData, Snowable {

    default BooleanStateValue getSnowStateValue(){
        return (BooleanStateValue) this.getStateValue("snowy");
    }

    @Override
    default boolean isSnowy(){
        return this.getSnowStateValue().getValue();
    }

    @Override
    default void setSnowy(boolean snowy){
        this.getSnowStateValue().setValue(snowy);
    }
}
