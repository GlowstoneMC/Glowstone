package net.glowstone.entity;

public class ActiveEntityEffect {
    private final EntityEffect effect;
    private byte amplitude;
    private short duration;

    public ActiveEntityEffect(EntityEffect effect, byte amplitude, short duration) {
        this.effect = effect;
        this.amplitude = amplitude;
        this.duration = duration;
    }

    public EntityEffect getEffect() {
        return effect;
    }

    public byte getAmplitude() {
        return amplitude;
    }

    public short getDuration() {
        return duration;
    }

    public boolean pulse() {
        if (duration < 1) return false;
        --duration;
        return true;
    }
}
