package net.glowstone.msg;

public class QuickBarMessage extends Message {

    private final short slot, id, amount, damage;

    public QuickBarMessage(short slot, short id, short amount, short damage) {
        this.slot = slot;
        this.id = id;
        this.amount = amount;
        this.damage = damage;
    }

    public short getSlot() {
        return slot;
    }

    public short getId() {
        return id;
    }

    public short getAmount() {
        return amount;
    }

    public short getDamage() {
        return damage;
    }
    
}
