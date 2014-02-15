package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;

public final class OpenWindowMessage implements Message {

    private final int id, type, slots;
    private final String title;
    private final boolean useTitle;
    private final int entityId;

    public OpenWindowMessage(int id, int type, String title, int slots, boolean useTitle) {
        this(id, type, title, slots, useTitle, 0);
    }

    public OpenWindowMessage(int id, int type, String title, int slots, boolean useTitle, int entityId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.slots = slots;
        this.useTitle = useTitle;
        this.entityId = entityId;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getSlots() {
        return slots;
    }

    public boolean getUseTitle() {
        return useTitle;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public String toString() {
        return "OpenWindowMessage{" +
                "id=" + id +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", slots=" + slots +
                ", useTitle=" + useTitle +
                ", entityId=" + entityId +
                '}';
    }
}
