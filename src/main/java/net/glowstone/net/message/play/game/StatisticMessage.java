package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

import java.util.Map;

public final class StatisticMessage implements Message {

    private final Map<String, Integer> values;

    public StatisticMessage(Map<String, Integer> values) {
        this.values = values;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "StatisticMessage{" +
                "values=" + values +
                '}';
    }
}
