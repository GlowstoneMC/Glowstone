package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import java.util.UUID;
import lombok.Data;

@Data
public final class SpectateMessage implements Message {

    private final UUID target;

}

