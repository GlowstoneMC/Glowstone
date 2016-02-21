package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
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

}
