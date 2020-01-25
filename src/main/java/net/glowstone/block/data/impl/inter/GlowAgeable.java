package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.block.data.Ageable;

public interface GlowAgeable extends IBlockData, Ageable {

    default IntegerStateValue.Ranged getAgeStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("age");
    }

    @Override
    default int getAge() {
        return this.getAgeStateValue().getValue();
    }

    @Override
    default void setAge(int i) {
        this.getAgeStateValue().setValue(i);
    }

    @Override
    default int getMaximumAge() {
        return this.getAgeStateValue().getGenerator().getMaximum();
    }
}
