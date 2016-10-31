package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.GlowServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Optional;

@Data
public class ConditionalLootItem {

    private final Optional<ProbableValue<String>> type;
    private final String condition;
    private final Optional<LootRandomValues> count;
    private final Optional<ProbableValue<Integer>> data;

    public ConditionalLootItem(JSONObject object) {
        if (object.containsKey("item")) {
            type = Optional.of(new ProbableValue<>(object, "item"));
        } else {
            type = Optional.empty();
        }
        if (object.containsKey("data")) {
            data = Optional.of(new ProbableValue<>(object, "data"));
        } else {
            data = Optional.empty();
        }
        if (object.containsKey("count")) {
            if (object.get("count") instanceof JSONArray) {
                GlowServer.logger.warning("Probable random value: not implemented!");
                count = Optional.empty();
            } else {
                count = Optional.of(new LootRandomValues(object));
            }
        } else {
            count = Optional.empty();
        }
        condition = (String) object.get("condition");
    }
}
