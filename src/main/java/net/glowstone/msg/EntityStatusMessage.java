package net.glowstone.msg;

public final class EntityStatusMessage extends Message {

    private final int id, status;

    public EntityStatusMessage(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "EntityStatusMessage{id=" + id + ",status=" + status + "}";
    }
}
