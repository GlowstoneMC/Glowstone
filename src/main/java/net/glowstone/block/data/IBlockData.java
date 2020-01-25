package net.glowstone.block.data;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import org.bukkit.block.data.BlockData;

import java.util.Set;

public interface IBlockData extends BlockData {

    Set<StateValue<?>> getStateValues();

    default <T extends Object> StateValue<T> getStateValue(StateGenerator<T> generator){
        return (StateValue<T>) this.getStateValues().stream().filter(s -> s.getGenerator().equals(generator)).findAny().orElse(null);
    }

    default StateValue<?> getStateValue(String id){
        return this.getStateValues().stream().filter(s -> s.getGenerator().getId().equals(id)).findAny().orElse(null);
    }

}
