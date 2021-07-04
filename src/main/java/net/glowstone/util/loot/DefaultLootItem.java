package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONObject;

import java.util.Optional;

@Data
public class DefaultLootItem {

    private final LootRandomValues count;
    private final ProbableValue<String> type;
    private final Optional<ProbableValue<Integer>> data;
    private final Optional<ReflectiveValue<Integer>> reflectiveData;

    /**
     * Parses a loot-table entry from JSON.
     *
     * @param object a loot-table entry in JSON format.
     */
    public DefaultLootItem(JSONObject object) {
        this.type = new ProbableValue<>(object, "item");
        this.count = new LootRandomValues(object);
        if (object.containsKey("data")) { // NON-NLS
            final Object data = object.get("data"); // NON-NLS
            if (data instanceof String) {
                this.reflectiveData = Optional
                    .of(new ReflectiveValue<Integer>((String) data));
                this.data = Optional.empty();
            } else if (data instanceof Long) {
                this.reflectiveData = Optional
                    .of(new ReflectiveValue<>(((Long) data).intValue()));
                this.data = Optional.empty();
            } else {
                this.reflectiveData = Optional.empty();
                this.data = Optional.of(new ProbableValue<>(object, "data"));
            }
        } else {
            this.reflectiveData = Optional.of(new ReflectiveValue<>(0));
            this.data = Optional.empty();
        }
    }
}
