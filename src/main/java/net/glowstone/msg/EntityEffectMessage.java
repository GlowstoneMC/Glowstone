package net.glowstone.msg;

public class EntityEffectMessage extends Message {

    private final int id;
    private final byte effect, amplifier;
    private final short duration;

    public EntityEffectMessage(int id, byte effect, byte amplifier, short duration) {
        this.id = id;
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public byte getEffect() {
        return effect;
    }

    public byte getAmplifier() {
        return amplifier;
    }

    public short getDuration() {
        return duration;
    }
    
}
