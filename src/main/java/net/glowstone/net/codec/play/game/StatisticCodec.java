package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.glowstone.net.message.play.game.StatisticMessage;

public final class StatisticCodec implements Codec<StatisticMessage> {

    @Override
    public StatisticMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode StatisticMessage");
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, StatisticMessage message) throws IOException {
        Map<String, Integer> map = message.getValues();
        ByteBufUtils.writeVarInt(buf, map.size());
        for (Entry<String, Integer> entry : map.entrySet()) {
            ByteBufUtils.writeUTF8(buf, entry.getKey());
            ByteBufUtils.writeVarInt(buf, entry.getValue());
        }
        return buf;
    }
}
