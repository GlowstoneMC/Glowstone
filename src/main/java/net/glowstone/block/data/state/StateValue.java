package net.glowstone.block.data.state;

public interface StateValue<F extends Object> {

    StateGenerator<F> getGenerator();
    F getValue();
    String getValueAsString();
    void setValue(F value);
    StateValue<F> clone();

}
