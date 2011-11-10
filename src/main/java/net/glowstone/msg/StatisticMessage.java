package net.glowstone.msg;

public final class StatisticMessage extends Message {

    private final int id;
    private final byte amount;

    public StatisticMessage(int id, byte amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }
    
    public byte getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "StatisticMessage{id=" + id + ",amount=" + amount + "}";
    }
}
