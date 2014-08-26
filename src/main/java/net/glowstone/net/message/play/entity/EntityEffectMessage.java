package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityEffectMessage implements Message {

    private final int id;
    private final byte effect, amplifier;
    private final int duration;
    private final boolean hideParticles;

    public EntityEffectMessage(int id, byte effect, byte amplifier, int duration, boolean hideParticles) {
        this.id = id;
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.hideParticles = hideParticles;
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

    public int getDuration() {
        return duration;
    }

    public boolean getHideParticles() {
        return hideParticles;
    }

    @Override
    public String toString() {
        return "EntityEffectMessage{id=" + id + ",effect=" + effect + ",amplifier=" + amplifier + ",duration=" + duration + "}";
    }
}
