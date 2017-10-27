package net.glowstone.block.state;

import com.google.common.collect.Maps;

import java.util.Map;

public class BlockStateData {

    private final Map<String, String> map = Maps.newHashMap();
    private final int numericValue;

    public BlockStateData(int numericValue) {
        this.numericValue = numericValue;
    }

    public BlockStateData() {
        this.numericValue = -1;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void put(String key, String value) {
        map.put(key, value);
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public String get(String key) {
        return map.get(key);
    }

    public boolean isNumeric() {
        return numericValue != -1;
    }

    public int getNumericValue() {
        return numericValue;
    }

    @Override
    public String toString() {
        if (isNumeric()) {
            return String.valueOf(numericValue);
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(",").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.length() == 0 ? builder.toString() : builder.substring(1);
    }
}
