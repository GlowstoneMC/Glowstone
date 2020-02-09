package net.glowstone.block.data.state.generator;

import net.glowstone.block.data.state.value.StateValue;
import net.glowstone.block.data.state.value.BooleanStateValue;

public class BooleanStateGenerator implements StateGenerator.EnumGenerator<Boolean> {

    private String id;
    private boolean defaultValue;

    public BooleanStateGenerator(String id, boolean defaultValue){
        this.id = id;
        this.defaultValue = defaultValue;
    }

    @Override
    public int serialize(Boolean id) {
        return id ? 0 : 1;
    }

    @Override
    public Boolean deserialize(int serial) {
        switch (serial){
            case 0: return true;
            case 1: return false;
            default: throw new UnsupportedOperationException(id + " is not supported for state generator of " + this.getClass().getName());
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public StateValue createStateValue(Boolean value) {
        return new BooleanStateValue(this, value);
    }

    @Override
    public Boolean[] getValues() {
        return new Boolean[]{false, true};
    }
}
