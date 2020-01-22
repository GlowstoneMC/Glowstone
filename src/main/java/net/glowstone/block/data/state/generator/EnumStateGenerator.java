package net.glowstone.block.data.state.generator;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.value.EnumStateValue;

public class EnumStateGenerator<T extends Enum> implements StateGenerator.EnumGenerator<T> {

    private String id;
    private T defaultValue;
    private T[] array;

    public EnumStateGenerator(String id, int defaultValue, T... values){
        this(id, values[defaultValue], values);
    }

    public EnumStateGenerator(String id, T defaultValue, T... values){
        this.id = id;
        this.defaultValue = defaultValue;
        this.array = values;
    }

    @Override
    public int getNetworkId(T id) {
        for(int i = 0; i < this.array.length; i++){
            if(this.array[i] == id){
                return i;
            }
        }
        throw new UnsupportedOperationException(id + " is not supported for state generator of " + this.getClass().getName());
    }

    @Override
    public T deserialize(int serial) {
        return this.array[serial];
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
        return this.array;
    }
}
