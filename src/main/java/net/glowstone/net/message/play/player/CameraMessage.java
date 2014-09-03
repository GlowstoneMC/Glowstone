package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class CameraMessage implements Message {

    private final int cameraID;

    public CameraMessage(int cameraID) {
        this.cameraID = cameraID;
    }

    public int getCameraID() {
        return cameraID;
    }

    @Override
    public String toString() {
        return "CameraMessage{" +
                "cameraID=" + cameraID +
                '}';
    }
}
