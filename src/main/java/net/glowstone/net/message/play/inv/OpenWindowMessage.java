package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import net.glowstone.util.TextMessage;

public final class OpenWindowMessage implements Message {

    private final int id;
    private final String type;
    private final TextMessage title;
    private final int slots, entityId;

    public OpenWindowMessage(int id, String type, String title, int slots) {
        this(id, type, new TextMessage(title), slots, 0);
    }

    public OpenWindowMessage(int id, String type, TextMessage title, int slots, int entityId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.slots = slots;
        this.entityId = entityId;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public TextMessage getTitle() {
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
