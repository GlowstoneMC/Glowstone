package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.glowstone.net.message.play.game.StatisticMessage;
import org.bukkit.Statistic;

public final class StatisticCodec implements Codec<StatisticMessage> {

    @Override
    public StatisticMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode StatisticMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StatisticMessage message) throws IOException {
        Map<Statistic, Integer> map = message.getValues();
        ByteBufUtils.writeVarInt(buf, map.size());
        for (Entry<Statistic, Integer> entry : map.entrySet()) {
            // todo: implement statistic categories, and map IDs
            // ByteBufUtils.writeUTF8(buf, entry.getKey());
            // ByteBufUtils.writeVarInt(buf, entry.getValue());
        }
        return buf;
    }
}
