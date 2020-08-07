package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.entity.LivingEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Data
public class LootRandomValues {

    private final Optional<Integer> min;
    private final Optional<Integer> max;
    private final Optional<String> reflectiveCount;
    private final Map<Integer, Double> probabilities = new HashMap<>();

    /**
     * Creates an instance for a given range.
     *
     * @param min the minimum number
     * @param max the maximum number
     */
    public LootRandomValues(int min, int max) {
        this.min = Optional.of(min);
        this.max = Optional.of(max);
        this.reflectiveCount = Optional.empty();
    }

    /**
     * Reads an instance from its JSON form.
     *
     * @param object a LootRandomValues instance in JSON form
     */
    public LootRandomValues(JSONObject object) {
        if (!object.containsKey("count")) {
            this.min = Optional.empty();
            this.max = Optional.empty();
            this.reflectiveCount = Optional.empty();
            return;
        }
        Object count = object.get("count");
        if (count instanceof Long) {
            this.min = Optional.of(((Long) count).intValue());
            this.max = min;
            this.reflectiveCount = Optional.empty();
            return;
        }
        if (count instanceof String) {
            this.min = Optional.empty();
            this.max = Optional.empty();
            this.reflectiveCount = Optional.of((String) count);
            return;
        }
        if (count instanceof JSONArray) {
            this.min = Optional.empty();
            this.max = Optional.empty();
            this.reflectiveCount = Optional.empty();

            // todo: probabilities
            return;
        }
        this.reflectiveCount = Optional.empty();
        object = (JSONObject) count;
        if (object.containsKey("min")) {
            this.min = Optional.of(((Long) object.get("min")).intValue());
        } else {
            this.min = Optional.of(0);
        }
        this.max = Optional.of(((Long) object.get("max")).intValue());
    }

    /**
     * Selects a random value between min and max, inclusively.
     *
     * @param random the random object to generate the number from
     * @param entity the entity
     * @return the random value
     */
    public int generate(Random random, LivingEntity entity) {
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
        if (reflectiveCount.isPresent()) {
            return ((Number) new ReflectionProcessor(reflectiveCount.get(), entity).process())
                .intValue();
        }
        if (min.isPresent() && max.isPresent()) {
            if (Objects.equals(min.get(), max.get())) {
                return min.get();
            }
            return random.nextInt(max.get() + 1 - min.get()) + min.get();
        }
        return 0;
    }
}
