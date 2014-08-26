package net.glowstone.net.message.play.player;

import com.flowpowered.networking.Message;

public final class ResourcePackStatusMessage implements Message {

    private final String hash;
    private final int result;

    public ResourcePackStatusMessage(String hash, int result) {
        this.hash = hash;
        this.result = result;
    }

    public String getHash() {
        return hash;
    }

    public int getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "ResourcePackStatusMessage{" +
                "hash='" + hash + '\'' +
                ", result=" + result +
                '}';
    }
}

