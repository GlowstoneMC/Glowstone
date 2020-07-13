package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class UpdateCommandBlockMessage implements Message {
    private final int x;
    private final int y;
    private final int z;
    private final String command;
    private final int mode;
    private final boolean trackOutput;
    private final boolean isConditional;
    private final boolean automatic;
}
