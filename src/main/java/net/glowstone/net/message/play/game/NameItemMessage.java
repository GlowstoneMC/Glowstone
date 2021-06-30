package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class NameItemMessage implements Message {

    private final String name;
}
