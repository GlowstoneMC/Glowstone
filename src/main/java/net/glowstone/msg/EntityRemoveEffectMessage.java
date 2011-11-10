package net.glowstone.msg;

public class EntityRemoveEffectMessage extends Message {

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
