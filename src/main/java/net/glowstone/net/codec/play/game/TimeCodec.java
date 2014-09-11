package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.TimeMessage;

import java.io.IOException;

public final class TimeCodec implements Codec<TimeMessage> {
    @Override
    public TimeMessage decode(ByteBuf buffer) throws IOException {
        long worldAge = buffer.readLong();
        long time = buffer.readLong();

        return new TimeMessage(worldAge, time);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TimeMessage message) throws IOException {
        buf.writeLong(message.getWorldAge());
        buf.writeLong(message.getTime());
        return buf;
    }
}
