package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.DeclareCommandsMessage;

// TODO

public class DeclareCommandsCodec implements Codec<DeclareCommandsMessage> {
    @Override
    public DeclareCommandsMessage decode(ByteBuf byteBuf) throws IOException {
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, DeclareCommandsMessage declareCommandsMessage)
        throws IOException {
        return null;
    }
}
