package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.GlowServer;
import org.json.simple.JSONObject;

import java.util.Random;

@Data
public class LootRandomValues {

    private final int min, max;

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
        if (min == max) {
            return min;
        }
        return random.nextInt(max + 1 - min) + min;
    }
}
