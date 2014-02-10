package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class AnimateEntityMessage implements Message {

    public static final int ANIMATION_SWING_ARM = 1;
    public static final int ANIMATION_HURT = 2;
    public static final int ANIMATION_LEAVE_BED = 3;

    private final int id, animation;

    public AnimateEntityMessage(int id, int animation) {
        this.id = id;
        this.animation = animation;
    }

    public int getId() {
        return id;
    }

    public int getAnimation() {
        return animation;
    }

    @Override
    public String toString() {
        return "AnimateEntityMessage{id=" + id + ",animation=" + animation + "}";
    }
}
