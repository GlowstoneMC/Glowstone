package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@Data
public final class UpdateSignMessage implements Message {

    private final int x, y, z;
    private final BaseComponent[][] message;

    public UpdateSignMessage(int x, int y, int z, BaseComponent[]... message) {
        if (message.length != 4) {
            throw new IllegalArgumentException("Expected message length was 4, got " + message.length);
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.message = message;
    }

    public static UpdateSignMessage fromPlainText(int x, int y, int z, String... message) {
        if (message.length != 4) {
            throw new IllegalArgumentException("Expected message length was 4, got " + message.length);
        }

        BaseComponent[][] encoded = new BaseComponent[4][1];
        for (int i = 0; i < 4; ++i) {
            encoded[i] = TextComponent.fromLegacyText(message[i]);
        }
        return new UpdateSignMessage(x, y, z, encoded);
    }
}
