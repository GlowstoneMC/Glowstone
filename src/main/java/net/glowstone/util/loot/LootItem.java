package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
public class LootItem {

    private static final ConditionalLootItem[] NO_ITEMS = new ConditionalLootItem[0];
    private final DefaultLootItem defaultItem;
    private final ConditionalLootItem[] conditionalItems;

    /**
     * Reads a LootItem from its JSON form.
     *
     * @param object a LootItem in JSON form
     */
    public LootItem(JSONObject object) {
        defaultItem = new DefaultLootItem((JSONObject) object.get("default"));
        if (object.containsKey("conditions")) {
            JSONArray array = (JSONArray) object.get("conditions");
            conditionalItems = new ConditionalLootItem[array.size()];
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = (JSONObject) array.get(i);
                conditionalItems[i] = new ConditionalLootItem(json);
            }
        } else {
            conditionalItems = NO_ITEMS;
        }
    }
}
