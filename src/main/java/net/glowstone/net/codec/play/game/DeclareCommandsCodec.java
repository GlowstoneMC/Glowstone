package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.DeclareCommandsMessage;

import java.io.IOException;

// TODO

public class DeclareCommandsCodec implements Codec<DeclareCommandsMessage> {
    @Override
    public DeclareCommandsMessage decode(ByteBuf byteBuf) throws IOException {
        return null;
    }

    @Override
    public ByteBuf encode(ByteBuf byteBuf, DeclareCommandsMessage declareCommandsMessage) throws IOException {
        return null;
    }
}
