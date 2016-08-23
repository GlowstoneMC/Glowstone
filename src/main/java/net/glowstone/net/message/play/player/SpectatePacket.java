package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import lombok.Data;

import java.util.UUID;

@Data
public final class SpectatePacket implements Message {

    private final UUID target;

}

