package net.glowstone.util.loot;

import lombok.Data;
import net.glowstone.GlowServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
public class DefaultLootItem {

    private final LootRandomValues count;
    private final ProbableValue<String> type;
    private final int data;

    public DefaultLootItem(JSONObject object) {
        this.type = new ProbableValue<>(object, "item");
        if (object.get("count") instanceof JSONArray) {
            GlowServer.logger.warning("Probable random value: not implemented!");
            this.count = new LootRandomValues(0, 0);
        } else {
            this.count = new LootRandomValues(object);
        }
        if (object.containsKey("data")) {
            if (object.get("data") instanceof String) {
                data = 0; // todo: reflection
            } else if (object.get("data") instanceof Long) {
                data = ((Long) object.get("data")).intValue();
            } else {
                data = 0;
            }
        } else {
            data = 0;
        }
    }
}
