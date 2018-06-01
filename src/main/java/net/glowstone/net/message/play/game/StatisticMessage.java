package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import java.util.Map;
import lombok.Data;

@Data
public final class StatisticMessage implements Message {

    private final Map<String, Integer> values;

}
