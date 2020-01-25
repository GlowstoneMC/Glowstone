package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.BooleanStateValue;
import org.bukkit.block.data.Powerable;

public interface GlowPowered extends IBlockData, Powerable {

    default BooleanStateValue getPoweredStateValue(){
        return (BooleanStateValue) this.getStateValue("powered");
    }

    @Override
    default boolean isPowered(){
        return this.getPoweredStateValue().getValue();
    }

    @Override
    default void setPowered(boolean power){
        this.getPoweredStateValue().setValue(power);
    }
}
