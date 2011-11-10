package net.glowstone.msg;

public final class CollectItemMessage extends Message {

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
