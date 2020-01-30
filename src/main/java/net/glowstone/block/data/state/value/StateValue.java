package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.generator.StateGenerator;

public interface StateValue<F extends Object> extends Cloneable {

    StateGenerator<F> getGenerator();
    F getValue();
    String getValueAsString();
    void setValue(F value);
    void setValueFromString(String value) throws IllegalArgumentException;
    boolean isExplicit();
    boolean fromString();
    StateValue<F> clone() throws CloneNotSupportedException;

}
