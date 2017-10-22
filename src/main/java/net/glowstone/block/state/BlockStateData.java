package net.glowstone.block.state;

import java.util.HashMap;

public class BlockStateData extends HashMap<String, String> {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<String, String> entry : entrySet()) {
            builder.append(",").append(entry.getKey()).append("=").append(entry.getValue());
        }
        return builder.length() == 0 ? builder.toString() : builder.substring(1);
    }
}
