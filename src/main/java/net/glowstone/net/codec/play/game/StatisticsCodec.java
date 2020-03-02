package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.glowstone.constants.GlowStatistic;
import net.glowstone.net.message.play.game.StatisticsMessage;
import org.bukkit.Statistic;

public final class StatisticsCodec implements Codec<StatisticsMessage> {

    @Override
    public StatisticsMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode StatisticMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StatisticsMessage message) throws IOException {
        Map<Statistic, Integer> map = message.getValues();
        ByteBufUtils.writeVarInt(buf, map.size());
        for (Entry<Statistic, Integer> entry : map.entrySet()) {
            Statistic statistic = entry.getKey();
            int categoryId = GlowStatistic.getCategoryId(statistic);
            int statisticId = GlowStatistic.getStatisticId(statistic);
            int value = entry.getValue();

            ByteBufUtils.writeVarInt(buf, categoryId);
            ByteBufUtils.writeVarInt(buf, statisticId);
            ByteBufUtils.writeVarInt(buf, value);
        }
        return buf;
    }
}
