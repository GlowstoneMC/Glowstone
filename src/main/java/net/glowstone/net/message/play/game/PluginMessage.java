package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Arrays;

public final class PluginMessage implements Message {

    private final String channel;
    private final byte[] data;

    public PluginMessage(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    public String getChannel() {
        return channel;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PluginMessage{" +
                "channel='" + channel + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }

}

