package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import net.glowstone.net.message.JsonMessage;

import java.util.Arrays;

public final class UpdateSignMessage implements Message {

    private final int x, y, z;
    private final String[] message;

    public UpdateSignMessage(int x, int y, int z, String[] message) {
        if (message.length != 4) {
            throw new IllegalArgumentException();
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.message = message;
    }

    public static UpdateSignMessage fromPlainText(int x, int y, int z, String[] message) {
        if (message.length != 4) {
            throw new IllegalArgumentException();
        }

        String[] encoded = new String[4];
        for (int i = 0; i < 4; ++i) {
            encoded[i] = JsonMessage.toTextJson(message[i]).toJSONString();
        }
        return new UpdateSignMessage(x, y, z, encoded);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String[] getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UpdateSignMessage{x=" + x + ",y=" + y + ",z=" + z + ",message=" + Arrays.toString(message) + "}";
    }
}
