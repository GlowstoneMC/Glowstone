package net.glowstone.net.message.play.entity;

import com.flowpowered.networking.Message;

public final class CollectItemMessage implements Message {

    private final int id, collector;

    public CollectItemMessage(int id, int collector) {
        this.id = id;
        this.collector = collector;
    }

    public int getId() {
        return id;
    }

    public int getCollector() {
        return collector;
    }

    @Override
    public String toString() {
        return "CollectItemMessage{id=" + id + ",collector=" + collector + "}";
    }
}
