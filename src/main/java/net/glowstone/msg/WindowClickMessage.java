package net.glowstone.msg;

public final class WindowClickMessage extends Message {

    private final int id, slot;
    private final boolean rightClick, shift;
    private final int transaction, item, count, damage;

    public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, boolean shift) {
        this(id, slot, rightClick, transaction, shift, -1, 0, 0);
    }

    public WindowClickMessage(int id, int slot, boolean rightClick, int transaction, boolean shift, int item, int count, int damage) {
        this.id = id;
        this.slot = slot;
        this.rightClick = rightClick;
        this.transaction = transaction;
        this.shift = shift;
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

    public boolean isRightClick() {
        return rightClick;
    }
    
    public boolean isShift() {
        return shift;
    }

    public int getTransaction() {
        return transaction;
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
        return "WindowClickMessage{id=" + id + ",slot=" + slot + ",rightClick=" + rightClick +
                ",shift=" + shift + ",transaction=" + transaction +
                ",item=" + item + ",count=" + count + ",damage=" + damage + "}";
    }
}
