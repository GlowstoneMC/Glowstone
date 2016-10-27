package net.glowstone.net.handler.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public class UseItemMessage implements Message {

    private final int hand;
}
