package net.glowstone.net.message.play.player;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.UUID;

@Data
public final class PlayerSessionMessage implements Message {
    private final UUID sessionId;
    private final long expiresAt;
    private final ByteBuf pubKey;
    private final ByteBuf keySig;
}
