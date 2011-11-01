package net.glowstone.msg;

import net.glowstone.inventory.GlowItemStack;

import java.util.Arrays;

public final class SetWindowSlotsMessage extends Message {

    private final int id;
    private final GlowItemStack[] items;

    public SetWindowSlotsMessage(int id, GlowItemStack[] items) {
        this.id = id;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public GlowItemStack[] getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SetWindowSlotsMessage{id=" + id + ",slots=" + Arrays.toString(items) + "}";
    }
}
