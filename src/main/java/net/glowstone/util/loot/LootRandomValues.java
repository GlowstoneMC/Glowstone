package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.GlowServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
public class LootRandomValues {

    private final int min, max;
    private final Map<Integer, Double> probabilities = new HashMap<>();

    public LootRandomValues(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public LootRandomValues(JSONObject object) {
        if (!object.containsKey("count")) {
            this.min = 0;
            this.max = 0;
            return;
        }
        Object count = object.get("count");
        if (count instanceof Long) {
            this.min = ((Long) count).intValue();
            this.max = min;
            return;
        }
        if (count instanceof String) {
            GlowServer.logger.warning("Conditional random value not implemented!"); // todo
            this.min = 0;
            this.max = 0;
            return;
        }
        if (count instanceof JSONArray) {
            this.min = 0;
            this.max = 0;

            return;
        }
        object = (JSONObject) count;
        if (object.containsKey("min")) {
            this.min = ((Long) object.get("min")).intValue();
        } else {
            this.min = 0;
        }
        this.max = ((Long) object.get("max")).intValue();
    }

    /**
     * Selects a random value between min and max, inclusively
     *
     * @param random the random object to generate the number from
     * @return the random value
     */
    public int generate(Random random) {
        if (!probabilities.isEmpty()) {
            double rand = random.nextDouble();
            double cur = 0;
            for (Map.Entry<Integer, Double> entry : probabilities.entrySet()) {
                cur += entry.getValue();
                if (rand < cur) {
                    return entry.getKey();
                }
            }
            return 0;
        }
        if (min == max) {
            return min;
        }
        return random.nextInt(max + 1 - min) + min;
    }
}
