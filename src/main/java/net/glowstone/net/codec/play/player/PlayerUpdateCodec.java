package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;

public final class PlayerUpdateCodec implements Codec<PlayerUpdateMessage> {

    @Override
    public PlayerUpdateMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        boolean onGround = buffer.readBoolean();
        return new PlayerUpdateMessage(onGround);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, PlayerUpdateMessage message) throws IOException {
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
