package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class UpdateCommandBlockMinecartMessage implements Message {
    private final int entityID;
    private final String command;
    private final boolean trackOutput;
}
