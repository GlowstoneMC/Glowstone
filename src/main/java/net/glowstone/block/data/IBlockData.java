package net.glowstone.block.data;

import net.glowstone.block.data.state.value.StateValue;
import org.bukkit.block.data.BlockData;

import java.util.Map;
import java.util.Optional;

public interface IBlockData extends BlockData {

    Map<String, StateValue<?>> getStateValues();
    int getNetworkId();
    String getJSONUniqueId();

    default <T> Optional<StateValue<T>> getStateValue(String id){
        return Optional.ofNullable((StateValue<T>) this.getStateValues().get(id));
    }

    default void replaceStateValue(StateValue<?> value){
        this.getStateValues().replace(value.getGenerator().getId(), value);
    }

}
