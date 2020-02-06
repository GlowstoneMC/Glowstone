package net.glowstone.block.data.state.value;

import net.glowstone.block.data.state.generator.IntegerStateGenerator;

public class IntegerStateValue implements StateValue<Integer> {

    private IntegerStateGenerator generator;
    private Integer value;
    private boolean fromString;

    public static class Ranged extends IntegerStateValue{

        public Ranged(IntegerStateGenerator.Ranged generator){
            this(generator, null);
        }

        public Ranged(IntegerStateGenerator.Ranged generator, Integer value) {
            super(generator, value);
        }

        private Ranged(IntegerStateGenerator.Ranged generator, Integer value, boolean fromString){
            super(generator, value, fromString);
        }

        @Override
        public IntegerStateGenerator.Ranged getGenerator(){
            return (IntegerStateGenerator.Ranged)super.getGenerator();
        }

        @Override
        public void setValue(Integer value){
            if(value >= this.getGenerator().getMinimum() && value < this.getGenerator().getMaximum()){
                super.setValue(value);
                return;
            }
            throw new UnsupportedOperationException(value + " can not be applied with generator " + this.getGenerator().getId() + ". Must be greater or equal to " + this.getGenerator().getMinimum() + " and less then " + this.getGenerator().getMaximum());
        }

        @Override
        public IntegerStateValue.Ranged clone() {
            return new IntegerStateValue.Ranged(this.getGenerator(), this.getValue());
        }
    }

    public IntegerStateValue(IntegerStateGenerator generator){
        this(generator, null);
    }

    public IntegerStateValue(IntegerStateGenerator generator, Integer value){
        this(generator, value, false);
    }

    public IntegerStateValue(IntegerStateGenerator generator, Integer value, boolean fromString){
        this.generator = generator;
        this.value = value;
        this.fromString = fromString;
    }

    @Override
    public IntegerStateGenerator getGenerator() {
        return this.generator;
    }

    @Override
    public Integer getValue() {
        if(this.value == null){
            return this.generator.getDefaultValue();
        }
        return this.value;
    }

    @Override
    public String getValueAsString() {
        return this.getValue() + "";
    }

    @Override
    public void setValue(Integer value) {
        this.fromString = false;
        this.value = value;
    }

    @Override
    public void setValueFromString(String value) throws IllegalArgumentException {
        this.fromString = true;
        try{
            this.value = Integer.parseInt(value);
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Invalid argument for " + this.getGenerator().getId());
        }
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
    public IntegerStateValue clone() {
        return new IntegerStateValue(this.generator, this.value);
    }
}
