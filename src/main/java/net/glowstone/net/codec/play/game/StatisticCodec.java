package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.StatisticMessage;

import java.io.IOException;
import java.util.Map;

public final class StatisticCodec implements Codec<StatisticMessage> {
    @Override
    public StatisticMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode StatisticMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StatisticMessage message) throws IOException {
        Map<String, Integer> map = message.getValues();
        ByteBufUtils.writeVarInt(buf, map.size());
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            ByteBufUtils.writeUTF8(buf, entry.getKey());
            ByteBufUtils.writeVarInt(buf, entry.getValue());
        }
        return buf;
    }
}
