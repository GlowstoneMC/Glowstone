package net.glowstone.msg;

public final class HealthMessage extends Message {

    private final int health;

    public HealthMessage(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

}
