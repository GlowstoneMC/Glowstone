package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;

@Data
public final class UpdateSignMessage implements Message {

    private final int x, y, z;
    private final TextMessage[] message;

    public UpdateSignMessage(int x, int y, int z, TextMessage[] message) {
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

        TextMessage[] encoded = new TextMessage[4];
        for (int i = 0; i < 4; ++i) {
            encoded[i] = new TextMessage(message[i]);
        }
        return new UpdateSignMessage(x, y, z, encoded);
    }

}
