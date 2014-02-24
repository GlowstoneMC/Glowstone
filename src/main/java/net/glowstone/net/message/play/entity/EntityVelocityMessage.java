package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class EntityVelocityMessage implements Message {

    private final int id, velocityX, velocityY, velocityZ;

    public EntityVelocityMessage(int id, int velocityX, int velocityY, int velocityZ) {
        // todo: confirm what these units actually are
        this.id = id;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    public int getId() {
        return id;
    }

    public int getVelocityX() {
        return velocityX;
    }

    public int getVelocityY() {
        return velocityY;
    }

    public int getVelocityZ() {
        return velocityZ;
    }

    @Override
    public String toString() {
        return "EntityVelocityMessage{id=" + id + ",velocityX=" + velocityX + ",velocityY=" + velocityY + ",velocityZ=" + velocityZ + "}";
    }
}
