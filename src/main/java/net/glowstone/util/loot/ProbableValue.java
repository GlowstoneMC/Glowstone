package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
public class ProbableValue<T> {

    private final Map<T, Double> possibilities = new HashMap<>();

    public ProbableValue(JSONObject json, String type) {
        Object o = json.get(type);
        if (o instanceof JSONArray) {
            JSONArray array = (JSONArray) o;
            for (Object obj : array) {
                JSONObject object = (JSONObject) obj;
                Double chance = (Double) object.get("chance");
                T val = (T) object.get("value");
                possibilities.put(val, chance);
            }
        } else {
            possibilities.put((T) o, 1.0);
        }
    }

    public T generate(Random random) {
        if (possibilities.size() == 1 && (Double) Arrays.asList(possibilities.values().toArray()).get(0) == 1.0) {
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
