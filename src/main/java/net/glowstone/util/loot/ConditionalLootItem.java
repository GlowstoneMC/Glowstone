package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.Optional;

@Data
public class ConditionalLootItem {

    private final Optional<ProbableValue<String>> type;
    private final String condition;
    private final Optional<LootRandomValues> count;
    private final Optional<ProbableValue<Integer>> data;
    private final Optional<ReflectiveValue<Integer>> reflectiveData;

    /**
     * Parses a conditional loot item from its JSONObject form.
     *
     * @param object a JSON object describing the loot item
     */
    public ConditionalLootItem(JSONObject object) {
        if (object.containsKey("item")) { // NON-NLS
            type = Optional.of(new ProbableValue<>(object, "item"));
        } else {
            type = Optional.empty();
        }
        if (object.containsKey("data")) { // NON-NLS
            Object data = object.get("data"); // NON-NLS
            if (data instanceof String) {
                this.reflectiveData = Optional.of(new ReflectiveValue<Integer>((String) data));
                this.data = Optional.empty();
            } else {
                this.data = Optional.of(new ProbableValue<>(object, "data"));
                this.reflectiveData = Optional.empty();
            }
        } else {
            data = Optional.empty();
            reflectiveData = Optional.empty();
        }
        if (object.containsKey("count")) { // NON-NLS
            count = Optional.of(new LootRandomValues(object));
        } else {
            count = Optional.empty();
        }
        condition = (String) object.get("condition"); // NON-NLS
    }
}
