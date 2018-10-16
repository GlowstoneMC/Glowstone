package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
public class EntityLootTable {

    private static final LootItem[] NO_ITEMS = new LootItem[0];
    private final LootItem[] items;
    private final LootRandomValues experience;

    /**
     * Parses a loot table from JSON.
     *
     * @param object a loot table in JSON form
     */
    public EntityLootTable(JSONObject object) {
        if (object.containsKey("experience")) { // NON-NLS
            this.experience
                    = new LootRandomValues((JSONObject) object.get("experience")); // NON-NLS
        } else {
            this.experience = null;
        }
        if (object.containsKey("items")) { // NON-NLS
            JSONArray array = (JSONArray) object.get("items"); // NON-NLS
            this.items = new LootItem[array.size()];
            for (int i = 0; i < array.size(); i++) {
                JSONObject json = (JSONObject) array.get(i);
                this.items[i] = new LootItem(json);
            }
        } else {
            this.items = NO_ITEMS;
        }
    }
}
