package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.block.data.AnaloguePowerable;

public interface GlowAnalogPowered extends IBlockData, AnaloguePowerable {

    default IntegerStateValue.Ranged getPowerStateValue(){
        return (IntegerStateValue.Ranged) this.<Integer>getStateValue("power").get();
    }

    @Override
    default int getPower(){
        return this.getPowerStateValue().getValue();
    }

    @Override
    default void setPower(int power){
        this.getPowerStateValue().setValue(power);
    }

    @Override
    default int getMaximumPower(){
        return this.getPowerStateValue().getGenerator().getMaximum();
    }
}
