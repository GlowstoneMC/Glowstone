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
            throw new IllegalArgumentException();
        }

        this.x = x;
        this.y = y;
        this.z = z;
        this.message = message;
    }

    public static UpdateSignMessage fromPlainText(int x, int y, int z, String... message) {
        if (message.length != 4) {
            throw new IllegalArgumentException();
        }

        BaseComponent[] encoded = new BaseComponent[4];
        for (int i = 0; i < 4; ++i) {
            encoded[i] = TextComponent.fromLegacyText(message[i])[0];
        }
        return new UpdateSignMessage(x, y, z, encoded);
    }
}
