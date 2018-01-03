package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class RespawnMessage implements Message {

    private final int dimension;
    private final int difficulty;
    private final int mode;
    private final String levelType;

}
