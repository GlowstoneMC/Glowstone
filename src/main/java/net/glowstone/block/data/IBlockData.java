package net.glowstone.block.data;

import net.glowstone.block.data.state.StateValue;
import org.bukkit.block.data.BlockData;

import java.util.Map;

public interface IBlockData extends BlockData {

    Map<String, StateValue<?>> getStateValues();

    default StateValue<?> getStateValue(String id){
        return this.getStateValues().get(id);
    }

    default void replaceStateValue(StateValue<?> value){
        this.getStateValues().replace(value.getGenerator().getId(), value);
    }

}
