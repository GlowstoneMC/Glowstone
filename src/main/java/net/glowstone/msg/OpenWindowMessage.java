package net.glowstone.msg;

public final class OpenWindowMessage extends Message {

    private final int id, type, slots;
    private final String title;

    public OpenWindowMessage(int id, int type, String title, int slots) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.slots = slots;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getSlots() {
        return slots;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
    return "OpenWindowMessage{id=" + id + ",type=" + type + ",slots=" + slots + ",title=" + title + "}";
    }
}
