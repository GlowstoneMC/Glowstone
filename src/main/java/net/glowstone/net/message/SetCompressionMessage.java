package net.glowstone.net.message;

import com.flowpowered.networking.Message;

public final class SetCompressionMessage implements Message {

    private final int threshold;

    public SetCompressionMessage(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    @Override
    public String toString() {
        return "SetCompressionMessage{" +
                "threshold=" + threshold +
                '}';
    }
}

