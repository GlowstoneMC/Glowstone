package net.glowstone.util.loot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import lombok.Data;
import org.jetbrains.annotations.NonNls;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Probability distribution with a list of possible values.
 */
@Data
public class ProbableValue<T> {

    private final Map<T, Double> possibilities = new HashMap<>();

    /**
     * Reads a distribution from a JSON object.
     *
     * @param json a JSON object
     * @param type the JSON property of {@code json} to read, which is either an instance of
     *             {@code T} or an array of objects of the form {@code {'chance': 0.123, 'value': T}}
     */
    public ProbableValue(JSONObject json, @NonNls String type) {
        Object o = json.get(type);
        if (o instanceof JSONArray) {
            JSONArray array = (JSONArray) o;
            for (Object obj : array) {
                JSONObject object = (JSONObject) obj;
                Double chance = (Double) object.get("chance"); // NON-NLS
                T val = (T) object.get("value"); // NON-NLS
                possibilities.put(val, chance);
            }
        } else {
            possibilities.put((T) o, 1.0);
        }
    }

    /**
     * Samples a value from this distribution.
     *
     * @param random the PRNG to use
     * @return a random value
     */
    public T generate(Random random) {
        if (possibilities.size() == 1
            && (Double) Arrays.asList(possibilities.values().toArray()).get(0) == 1.0) {
            return (T) Arrays.asList(possibilities.keySet().toArray()).get(0);
        }
        double rand = random.nextDouble();
        double cur = 0;
        for (Map.Entry<T, Double> entry : possibilities.entrySet()) {
            cur += entry.getValue();
            if (rand < cur) {
                return entry.getKey();
            }
        }
        return null;
    }
}
