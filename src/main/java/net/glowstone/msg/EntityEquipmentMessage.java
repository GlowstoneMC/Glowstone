package net.glowstone.msg;

public final class EntityEquipmentMessage extends Message {
    
    public static final int HELD_ITEM = 0;
    public static final int BOOTS_SLOT = 1;
    public static final int LEGGINGS_SLOT = 2;
    public static final int CHESTPLATE_SLOT = 3;
    public static final int HELMET_SLOT = 4;

    private final int id, slot, item, damage;

    public EntityEquipmentMessage(int id, int slot, int item, int damage) {
        this.id = id;
        this.slot = slot;
        this.item = item;
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

    public int getDamage() {
        return damage;
    }

    @Override
    public String toString() {
        return "EntityEquipmentMessage{id=" + id + ",slot=" + slot + ",item=" + item + ",damage" + damage +"}";
    }
}
