package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.glowstone.util.TextMessage;

@Data
public final class UpdateSignMessage implements Message {

    private final int x;
    private final int y;
    private final int z;
    private final TextMessage[] message;

    /**
     * Creates a message.
     *
     * @param x the sign X coordinate
     * @param y the sign Y coordinate
     * @param z the sign Z coordinate
     * @param message 4 messages, each containing a line of the sign's new contents
     * @throws IllegalArgumentException if {@code message.length != 4}
     */
    public UpdateSignMessage(int x, int y, int z, TextMessage[] message) {
        if (message.length != 4) {
            throw new IllegalArgumentException();
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.message = message;
    }

    /**
     * Builds an UpdateSignMessage from 4 strings.
     *
     * @param x the sign X coordinate
     * @param y the sign Y coordinate
     * @param z the sign Z coordinate
     * @param message 4 strings, each containing a line of the sign's new contents
     * @return an UpdateSignMessage for the parameters
     * @throws IllegalArgumentException if {@code message} isn't exactly 4 strings
     */
    public static UpdateSignMessage fromPlainText(int x, int y, int z, String... message) {
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
