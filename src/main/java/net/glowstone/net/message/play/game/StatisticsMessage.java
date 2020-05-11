package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import java.util.Map;
import lombok.Data;
import org.bukkit.Statistic;

@Data
public final class StatisticsMessage implements Message {

    private final Map<Statistic, Integer> values;

}
