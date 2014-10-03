package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.Data;

import java.util.Map;

@Data
public final class StatisticMessage implements Message {

    private final Map<String, Integer> values;

}
