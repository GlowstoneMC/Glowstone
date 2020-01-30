package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.generator.EnumStateGenerator;

public class EnumStateValue<E extends Enum> implements StateValue<E> {

    private EnumStateGenerator<E> generator;
    private E value;
    private boolean fromString;

    public EnumStateValue(EnumStateGenerator<E> generator){
        this(generator, null);
    }

    public EnumStateValue(EnumStateGenerator<E> generator, E value){
        this(generator, value, false);
    }

    private EnumStateValue(EnumStateGenerator<E> generator, E value, boolean fromString){
        this.generator = generator;
        this.value = value;
        this.fromString = fromString;
    }

    @Override
    public EnumStateGenerator<E> getGenerator() {
        return this.generator;
    }

    @Override
    public E getValue() {
        if(this.value == null){
            return this.generator.getDefaultValue();
        }
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return this.getValue().name().toLowerCase();
    }

    @Override
    public void setValue(E value) {
        this.fromString = false;
        this.value = value;
    }

    @Override
    public void setValueFromString(String value) throws IllegalArgumentException {
        this.fromString = true;
        E value1 = (E) Enum.valueOf(this.value.getClass(), value.toUpperCase());
        if(value1 == null){
            throw new IllegalArgumentException("Invalid argument for " + this.getGenerator().getId());
        }
        this.value = value1;
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
    public EnumStateValue<E> clone() {
        return new EnumStateValue<>(this.generator, this.value);
    }
}
