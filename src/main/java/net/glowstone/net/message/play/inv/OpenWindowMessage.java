package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.JsonMessage;
import org.json.simple.JSONObject;

public final class OpenWindowMessage implements Message {

    private final int id;
    private final String type, title;
    private final int slots, entityId;

    public OpenWindowMessage(int id, String type, String title, int slots) {
        this(id, type, JsonMessage.toTextJson(title), slots, 0);
    }

    public OpenWindowMessage(int id, String type, JSONObject titleJson, int slots, int entityId) {
        this.id = id;
        this.type = type;
        this.title = titleJson.toJSONString();
        this.slots = slots;
        this.entityId = entityId;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitleJson() {
        return title;
    }

    public int getSlots() {
        return slots;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return "OpenWindowMessage{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", title=" + title +
                ", slots=" + slots +
                ", entityId=" + entityId +
                '}';
    }
}
