package net.glowstone.block.data;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractBlockData implements IBlockData {

    private Material material;
    private Set<StateValue<? extends Object>> values;

    public AbstractBlockData(Material material, StateGenerator<? extends Object>... array){
        this.material = material;
        this.values = new HashSet<>(array.length);
        for(StateGenerator<?> gen : array){
            this.values.add(gen.getDefaultStateValue());
        }
    }

    public AbstractBlockData(Material material, StateValue<? extends Object>... array){
        this(material, Arrays.asList(array));
    }

    public AbstractBlockData(Material material){
        this(material, new StateValue[0]);
    }

    public AbstractBlockData(Material material, Collection<StateValue<? extends Object>> collection){
        this.material = material;
        this.values = new HashSet<>(collection);
    }

    @Override
    public Set<StateValue<?>> getStateValues(){
        return this.values;
    }

    @Override
    public @NotNull Material getMaterial() {
        return this.material;
    }

    @Override
    public @NotNull String getAsString(boolean b) {
        return b ? "minecraft:" + this.material.name().toLowerCase() : getAsString();
    }

    @Override
    public @NotNull String getAsString(){
        String entries = null;
        for(StateValue<?> value : this.values){
            if(entries == null){
                entries = value.getGenerator().getId() + "=" + value.getValueAsString();
            }else{
                entries = entries + ", " + value.getGenerator().getId() + ": " + value.getValueAsString();
            }
        }
        if(entries == null) {
            return "minecraft:" + this.material.name().toLowerCase();
        }
        return "minecraft:" + this.material.name().toLowerCase() + " [" + entries + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        ((AbstractBlockData)blockData).getStateValues().forEach(s -> applyStateValue(s));
        return this;
    }

    @Override
    public boolean matches(@Nullable BlockData blockData) {
        return Objects.equals(blockData, this);
    }

    private <T> void applyStateValue(StateValue<T> value){
        this.values.stream().filter(v -> v.getGenerator().equals(value.getGenerator()))
                .findAny()
                .ifPresent(v -> ((StateValue<T>)v).setValue(value.getValue()));

    }
}
