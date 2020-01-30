package net.glowstone.block.data.state.generator;

import net.glowstone.block.data.state.value.StateValue;
import net.glowstone.block.data.state.value.IntegerStateValue;

public class IntegerStateGenerator implements StateGenerator<Integer> {

    public static class Ranged extends IntegerStateGenerator implements StateGenerator.EnumGenerator<Integer>{

        private int minimum;
        private int maximum;

        public Ranged(String id, int defaultValue, int maximum){
            this(id, defaultValue, 0, maximum);
        }

        public Ranged(String id, int defaultValue, int minimum, int maximum) {
            super(id, defaultValue);
            this.maximum = maximum;
            this.minimum = minimum;
        }

        public int getMinimum(){
            return this.minimum;
        }

        public int getMaximum(){
            return this.maximum;
        }

        @Override
        public Integer[] getValues() {
            Integer[] array = new Integer[this.maximum - this.minimum];
            for(int i = this.minimum; i < this.maximum; i++){
                array[i] = i;
            }
            return array;
        }

        @Override
        public StateValue createStateValue(Integer value) {
            return new IntegerStateValue.Ranged(this, value);
        }

    }

    private String id;
    private int defaultValue;

    public IntegerStateGenerator(String id, int defaultValue){
        this.id = id;
        this.defaultValue = defaultValue;
    }

    @Override
    public int serialize(Integer id) {
        return id;
    }

    @Override
    public Integer deserialize(int serial) {
        return serial;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Integer getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public StateValue createStateValue(Integer value) {
        return new IntegerStateValue(this, value);
    }
}
