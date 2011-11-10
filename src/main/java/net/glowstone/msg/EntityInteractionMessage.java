package net.glowstone.msg;

public final class EntityInteractionMessage extends Message {

    private final int id, target;
    private final boolean punching;

    public EntityInteractionMessage(int id, int target, boolean punching) {
        this.id = id;
        this.target = target;
        this.punching = punching;
    }

    public int getId() {
        return id;
    }

    public int getTarget() {
        return target;
    }

    public boolean isPunching() {
        return punching;
    }

    @Override
    public String toString() {
        return "EntityInteractionMessage{id=" + id + ",target=" + target + ",punching=" + punching + "}";
    }
}
