package net.glowstone.util.loot;

import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Data
public class EntityLootTable {

    private final LootItem[] items;
    private final LootRandomValues experience;
    private static final LootItem[] NO_ITEMS = new LootItem[0];

    public EntityLootTable(JSONObject object) {
        if (object.containsKey("experience")) {
            this.experience = new LootRandomValues((JSONObject) object.get("experience"));
        } else {
            this.experience = null;
        }
        if (object.containsKey("items")) {
            JSONArray array = (JSONArray) object.get("items");
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
