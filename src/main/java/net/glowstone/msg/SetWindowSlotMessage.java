package net.glowstone.msg;

public final class SetWindowSlotMessage extends Message {

    private final int id, slot, item, count, damage;

    public SetWindowSlotMessage(int id, int slot) {
        this(id, slot, -1, 0, 0);
    }

    public SetWindowSlotMessage(int id, int slot, int item, int count, int damage) {
        this.id = id;
        this.slot = slot;
        this.item = item;
        this.count = count;
        this.damage = damage;
    }

    public int getId() {
        return id;
    }

    public int getSlot() {
        return slot;
    }

    public int getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return "SetWindowSlotMessage{id=" + id + ",slot=" + slot + ",item=" + item + ",count=" + count + ",damage=" + damage + "}";
    }
}
