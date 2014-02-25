package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class SteerVehicleMessage implements Message {

    private final float sideways, forward;
    private final boolean jump, unmount;

    public SteerVehicleMessage(float sideways, float forward, boolean jump, boolean unmount) {
        this.sideways = sideways;
        this.forward = forward;
        this.jump = jump;
        this.unmount = unmount;
    }

    public float getSideways() {
        return sideways;
    }

    public float getForward() {
        return forward;
    }

    public boolean isJump() {
        return jump;
    }

    public boolean isUnmount() {
        return unmount;
    }

    @Override
    public String toString() {
        return "SteerVehicleMessage{" +
                "sideways=" + sideways +
                ", forward=" + forward +
                ", jump=" + jump +
                ", unmount=" + unmount +
                '}';
    }
}

