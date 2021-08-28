package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.AcknowledgePlayerDiggingMessage;

import java.io.IOException;

// TODO

public class AcknowledgePlayerDiggingCodec implements Codec<AcknowledgePlayerDiggingMessage> {
    @Override
    public AcknowledgePlayerDiggingMessage decode(ByteBuf byteBuf) throws IOException {
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf,
                          AcknowledgePlayerDiggingMessage acknowledgePlayerDiggingMessage)
        throws IOException {
        return null;
    }
}
