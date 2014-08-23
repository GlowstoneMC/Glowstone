package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class InteractEntityMessage implements Message {

    private final int id, action;
    private final float targetX, targetY, targetZ;

    public InteractEntityMessage(int id, int action, float targetX, float targetY, float targetZ) {
        this.id = id;
        this.action = action;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public int getId() {
        return id;
    }

    public int getAction() {
        return action;
    }

    public float getTargetX() {
        return targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public float getTargetZ() {
        return targetZ;
    }

    @Override
    public String toString() {
        return "InteractEntityMessage{" +
                "id=" + id +
                ", action=" + action +
                '}';
    }
}

