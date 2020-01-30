package net.glowstone.block.data.impl.inter;

import net.glowstone.block.data.IBlockData;
import net.glowstone.block.data.state.value.IntegerStateValue;
import org.bukkit.block.data.Levelled;

public interface GlowLevelled extends IBlockData, Levelled {

    default IntegerStateValue.Ranged getLevelStateValue(){
        return (IntegerStateValue.Ranged) this.getStateValue("level").get();
    }

    @Override
    default int getLevel() {
        return this.getLevelStateValue().getValue();
    }

    @Override
    default void setLevel(int i) {
        this.getLevelStateValue().setValue(i);
    }

    @Override
    default int getMaximumLevel() {
        return this.getLevelStateValue().getGenerator().getMaximum();
    }
}
