package net.glowstone.block.data;

import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.value.StateValue;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class AbstractBlockData implements IBlockData {

    private Material material;
    private Map<String, StateValue<?>> values;

    public AbstractBlockData(Material material, StateGenerator<?>... array){
        this.material = material;
        this.values = new HashMap<>(array.length);
        for(StateGenerator<?> gen : array){
            this.values.put(gen.getId(), gen.createDefaultStateValue());
        }
    }

    public AbstractBlockData(Material material, StateValue<?>... array){
        this(material, Arrays.asList(array));
    }

    public AbstractBlockData(Material material){
        this(material, new StateValue[0]);
    }

    public AbstractBlockData(Material material, Collection<StateValue<?>> collection){
        this.material = material;
        this.values = new HashMap<>(collection.size());
        collection.forEach(s -> this.values.put(s.getGenerator().getId(), s));
    }

    @Override
    public Map<String, StateValue<?>> getStateValues(){
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
        for(StateValue<?> value : this.values.values()){
            if(entries == null){
                entries = value.getGenerator().getId() + "=" + value.getValueAsString();
            }else{
                entries += "," + value.getGenerator().getId() + "=" + value.getValueAsString();
            }
        }
        if(entries == null) {
            return "minecraft:" + this.material.name().toLowerCase();
        }
        return "minecraft:" + this.material.name().toLowerCase() + "[" + entries + "]";
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        ((AbstractBlockData)blockData).getStateValues().values().forEach(this::applyStateValue);
        return this;
    }

    @Override
    public boolean matches(@Nullable BlockData blockData) {
        if(blockData == null){
            //@Nullable?
            return false;
        }
        if(!blockData.getMaterial().equals(this.getMaterial())){
            return false;
        }
        return this.getStateValues().values().stream().filter(s -> s.isExplicit()).anyMatch(s -> ((IBlockData)blockData).getStateValue(s.getGenerator().getId()).get().getValue().equals(s.getValue()));
    }

    @Override
    public BlockData clone(){
        throw new NotImplementedException(this.getClass().getName() + " does not override clone");
    }

    private <T> void applyStateValue(StateValue<T> value){
        this.values.values().stream().filter(v -> v.getGenerator().equals(value.getGenerator()))
                .findAny()
                .ifPresent(v -> ((StateValue<T>)v).setValue(value.getValue()));
    }
}
