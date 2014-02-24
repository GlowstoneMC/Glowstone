package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityRemoveEffectMessage implements Message {

    private final int id;
    private final byte effect;

    public EntityRemoveEffectMessage(int id, byte effect) {
        this.id = id;
        this.effect = effect;
    }

    public int getId() {
        return id;
    }

    public byte getEffect() {
        return effect;
    }

    @Override
    public String toString() {
        return "EntityRemoveMessage{id=" + id + ",effect=" + effect + "}";
    }
}
