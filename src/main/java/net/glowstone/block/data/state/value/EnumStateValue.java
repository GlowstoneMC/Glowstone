package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.EnumStateGenerator;

public class EnumStateValue<E extends Enum> implements StateValue<E> {

    private EnumStateGenerator<E> generator;
    private E value;

    public EnumStateValue(EnumStateGenerator<E> generator){
        this(generator, generator.getDefaultValue());
    }

    public EnumStateValue(EnumStateGenerator<E> generator, E value){
        this.generator = generator;
        this.value = value;
    }

    @Override
    public EnumStateGenerator<E> getGenerator() {
        return this.generator;
    }

    @Override
    public E getValue() {
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return this.value.name().toLowerCase();
    }

    @Override
    public void setValue(E value) {
        this.value = value;
    }

    @Override
    public EnumStateValue<E> clone() {
        return new EnumStateValue<>(this.generator, this.value);
    }
}
