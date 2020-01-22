package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.StateGenerator;
import net.glowstone.block.data.state.StateValue;
import net.glowstone.block.data.state.generator.IntegerStateGenerator;

public class IntegerStateValue implements StateValue<Integer> {

    private IntegerStateGenerator generator;
    private int value;

    public static class Ranged extends IntegerStateValue{

        public Ranged(IntegerStateGenerator.Ranged generator){
            this(generator, generator.getDefaultValue());
        }

        public Ranged(IntegerStateGenerator.Ranged generator, int value) {
            super(generator, value);
        }

        @Override
        public IntegerStateGenerator.Ranged getGenerator(){
            return (IntegerStateGenerator.Ranged)super.getGenerator();
        }

        @Override
        public void setValue(Integer value){
            if(value >= this.getGenerator().getMinimum() && value < this.getGenerator().getMaximum()){
                super.setValue(value);
            }
            throw new UnsupportedOperationException(value + " can not be applied with generator " + this.getGenerator().getClass().getName());
        }

        @Override
        public IntegerStateValue.Ranged clone() {
            return new IntegerStateValue.Ranged(this.getGenerator(), this.getValue());
        }
    }

    public IntegerStateValue(IntegerStateGenerator generator){
        this(generator, generator.getDefaultValue());
    }

    public IntegerStateValue(IntegerStateGenerator generator, int value){
        this.generator = generator;
        this.value = value;
    }

    @Override
    public IntegerStateGenerator getGenerator() {
        return this.generator;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return this.value + "";
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public IntegerStateValue clone() {
        return new IntegerStateValue(this.generator, this.value);
    }
}
