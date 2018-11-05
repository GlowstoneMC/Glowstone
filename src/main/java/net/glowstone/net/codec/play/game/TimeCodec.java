package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.TimeMessage;

public final class TimeCodec implements Codec<TimeMessage> {

    @Override
    public TimeMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        long worldAge = buffer.readLong();
        long time = buffer.readLong();

        return new TimeMessage(worldAge, time);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, TimeMessage message) throws IOException {
        buf.writeLong(message.getWorldAge());
        buf.writeLong(message.getTime());
        return buf;
    }
}
