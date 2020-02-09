package net.glowstone.block.data.state.generator;

import net.glowstone.block.data.state.value.StateValue;
import net.glowstone.block.data.state.value.EnumStateValue;

import java.util.*;

public class EnumStateGenerator<T extends Enum> implements StateGenerator.EnumGenerator<T> {

    private String id;
    private T defaultValue;
    private TreeMap<String, T> altName = new TreeMap<>();

    public EnumStateGenerator(String id, int defaultValue, T... values){
        this(id, values[defaultValue], values);
    }

    public EnumStateGenerator(String id, T defaultValue, T... values){
        this.id = id;
        this.defaultValue = defaultValue;
        for(T value : values){
            this.altName.put(value.name().toLowerCase(), value);
        }
    }

    public EnumStateGenerator(String id, T defaultValue, Map.Entry<String, T>... entries){
        this.id = id;
        this.defaultValue = defaultValue;
        for(Map.Entry<String, T> entry : entries){
            this.altName.put(entry.getKey(), entry.getValue());
        }
    }

    public EnumStateGenerator(String id, T defaultValue, T[] values, Map.Entry<String, T>... entries){
        this.id = id;
        this.defaultValue = defaultValue;
        for(T value : values){
            String name = value.name().toUpperCase();
            for(Map.Entry<String, T> entry : entries){
                if(entry.getValue().equals(value)){
                    name = entry.getKey();
                    break;
                }
            }
            this.altName.put(name, value);
        }
    }

    public String getAltName(T value){
        Optional<Map.Entry<String, T>> opValue = this.altName.entrySet().stream().filter(e -> e.getValue().equals(value)).findAny();
        if(!opValue.isPresent()){
            throw new IllegalStateException("Value of " + value.name() + " is not present in Enum Generator of " + this.getId());
        }
        return opValue.get().getKey();
    }

    @Override
    public int serialize(T id) {
        int i = 0;
        for (Map.Entry<String, T> entry : this.altName.entrySet()){
            if(entry.getValue().equals(id)){
                return i;
            }
            i++;
        }
        throw new UnsupportedOperationException(id + " is not supported for state generator of " + this.getClass().getName());
    }

    @Override
    public T deserialize(int serial) {
        int i = 0;
        for (Map.Entry<String, T> entry : this.altName.entrySet()){
            if(i == serial){
                return entry.getValue();
            }
            i++;
        }
        throw new UnsupportedOperationException(id + " is not supported for state generator of " + this.getClass().getName());
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public StateValue createStateValue(T value) {
        return new EnumStateValue(this, value);
    }

    @Override
    public T[] getValues() {
        return (T[])this.altName.values().toArray();
    }
}
