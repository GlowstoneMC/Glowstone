package net.glowstone.msg;

public final class ActivateItemMessage extends Message {

    private final int slot;

    public ActivateItemMessage(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString() {
        return "ActivateItemMessage{slot=" + slot + "}";
    }
}
