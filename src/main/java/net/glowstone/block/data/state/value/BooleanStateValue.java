package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;

public class BooleanStateValue implements StateValue<Boolean> {

    private BooleanStateGenerator generator;
    private boolean value;

    public BooleanStateValue(BooleanStateGenerator generator, boolean value){
        this.generator = generator;
        this.value = value;
    }

    @Override
    public StateGenerator<Boolean> getGenerator() {
        return this.generator;
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return ((Boolean)this.value).toString();
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public BooleanStateValue clone() {
        return new BooleanStateValue(this.generator, this.value);
    }
}
