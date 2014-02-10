package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;

public final class HeldItemMessage implements Message {

    private final int slot;

    public HeldItemMessage(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "HeldItemMessage{" +
                "slot=" + slot +
                '}';
    }
}

