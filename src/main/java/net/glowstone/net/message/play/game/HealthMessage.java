package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class HealthMessage implements Message {

    private final float health;
    private final int food;
    private final float saturation;

    public HealthMessage(float health, int food, float saturation) {
        this.health = health;
        this.food = food;
        this.saturation = saturation;
    }

    public float getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public float getSaturation() {
        return saturation;
    }

    @Override
    public String toString() {
        return "HealthMessage{health=" + health + ",food=" + food + ",saturation=" + saturation + "}";
    }

}
