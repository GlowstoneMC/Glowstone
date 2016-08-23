package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.TimePacket;

import java.io.IOException;

public final class TimeCodec implements Codec<TimePacket> {
    @Override
    public TimePacket decode(ByteBuf buffer) throws IOException {
        long worldAge = buffer.readLong();
        long time = buffer.readLong();

        return new TimePacket(worldAge, time);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, TimePacket message) throws IOException {
        buf.writeLong(message.getWorldAge());
        buf.writeLong(message.getTime());
        return buf;
    }
}
