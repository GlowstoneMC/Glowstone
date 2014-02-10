package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class PlayerAbilitiesMessage implements Message {

    private final int flags;
    private final float flySpeed, walkSpeed;

    public PlayerAbilitiesMessage(int flags, float flySpeed, float walkSpeed) {
        this.flags = flags;
        this.flySpeed = flySpeed;
        this.walkSpeed = walkSpeed;
    }

    public int getFlags() {
        return flags;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    @Override
    public String toString() {
        return "PlayerAbilitiesMessage{" +
                "flags=" + flags +
                ", flySpeed=" + flySpeed +
                ", walkSpeed=" + walkSpeed +
                '}';
    }
}

