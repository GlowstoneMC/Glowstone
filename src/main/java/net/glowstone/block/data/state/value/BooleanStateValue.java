package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.generator.StateGenerator;
import net.glowstone.block.data.state.generator.BooleanStateGenerator;

public class BooleanStateValue implements StateValue<Boolean> {

    private BooleanStateGenerator generator;
    private Boolean value;
    private boolean fromString;

    public BooleanStateValue(BooleanStateGenerator generator){
        this(generator, null);
    }

    public BooleanStateValue(BooleanStateGenerator generator, Boolean value){
        this(generator, value, false);
    }

    private BooleanStateValue(BooleanStateGenerator generator, Boolean value, boolean fromString){
        this.generator = generator;
        this.value = value;
        this.fromString = fromString;
    }

    @Override
    public StateGenerator<Boolean> getGenerator() {
        return this.generator;
    }

    @Override
    public Boolean getValue() {
        if(this.value == null){
            return this.generator.getDefaultValue();
        }
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return this.getValue().toString();
    }

    @Override
    public void setValue(Boolean value) {
        this.fromString = false;
        this.value = value;
    }

    @Override
    public void setValueFromString(String value) throws IllegalArgumentException {
        this.fromString = true;
        if(value.equalsIgnoreCase("true")){
            this.value = true;
        }else if(value.equalsIgnoreCase("false")){
            this.value = false;
        }
        throw new IllegalArgumentException("Invalid value for " + this.getGenerator().getId());
    }

    @Override
    public boolean isExplicit() {
        return this.value != null;
    }

    @Override
    public boolean fromString() {
        return this.fromString;
    }

    @Override
    public BooleanStateValue clone() {
        return new BooleanStateValue(this.generator, this.value);
    }

    public static BooleanStateValue from(BooleanStateGenerator generator, String toParse){
        return new BooleanStateValue(generator, Boolean.parseBoolean(toParse), true);
    }
}
