package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityStatusMessage implements Message {

    private final int id, status;

    public EntityStatusMessage(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public EntityStatusMessage(int id, Status status) {
        this(id, status.ordinal());
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

    public enum Status {
        UNKNOWN_0,
        HURT,
        UNKNOWN_2,
        DEAD,
        GOLEM_FLAIL,
        WOLF_TAMING,
        WOLF_TAMED,
        WOLF_SHAKE,
        EATING_ACCEPTED,
        SHEEP_EAT,
        GOLEM_ROSE,
        VILLAGER_HEARTS,
        VILLAGER_ANGRY,
        VILLAGER_HAPPY,
        WITCH_MAGIC,
        VILLAGER_DEZOMBIE,
        FIREWORK_EXPLODE,
        FALL_IN_LOVE
    }
}
