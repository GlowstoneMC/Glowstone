package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;
import net.glowstone.inventory.GlowItemStack;

import java.util.Arrays;

public final class SetWindowContentsMessage implements Message {

    private final int id;
    private final GlowItemStack[] items;

    public SetWindowContentsMessage(int id, GlowItemStack[] items) {
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
        return "SetWindowContentsMessage{id=" + id + ",slots=" + Arrays.toString(items) + "}";
    }
}
