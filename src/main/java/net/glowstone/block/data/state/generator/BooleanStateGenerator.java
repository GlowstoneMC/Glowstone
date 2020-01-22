package net.glowstone.block.data.state.generator;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.value.BooleanStateValue;

public class BooleanStateGenerator implements StateGenerator.EnumGenerator<Boolean> {

    private String id;

    public BooleanStateGenerator(String id){
        this.id = id;
    }

    @Override
    public int getNetworkId(Boolean id) {
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
        return false;
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
