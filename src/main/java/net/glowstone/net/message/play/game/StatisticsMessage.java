package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import org.bukkit.Statistic;

import java.util.Map;

@Data
public final class StatisticsMessage implements Message {

    private final Map<Statistic, Integer> values;

}
