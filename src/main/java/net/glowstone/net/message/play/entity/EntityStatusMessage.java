package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;
import org.bukkit.EntityEffect;

public final class EntityStatusMessage implements Message {

    // statuses not included in Bukkit's EntityEffect
    public final int MYSTERY_LIVING = 0;
    public final int MYSTERY_PLAYER = 1;
    public final int GOLEM_FLING_ARMS = 4;
    public final int EATING_ACCEPTED = 9;
    public final int ANIMAL_HEARTS = 18;

    private final int id, status;

    public EntityStatusMessage(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public EntityStatusMessage(int id, EntityEffect effect) {
        this(id, effect.getData());
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
