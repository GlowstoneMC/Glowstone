package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

@Data
public final class RespawnMessage implements Message {

    private final int dimension, difficulty, mode;
    private final String levelType;

}
