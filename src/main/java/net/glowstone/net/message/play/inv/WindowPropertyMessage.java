package net.glowstone.net.message.play.inv;

import com.flowpowered.networking.Message;

public final class WindowPropertyMessage implements Message {

    private final int id, property, value;

    public WindowPropertyMessage(int id, int property, int value) {
        this.id = id;
        this.property = property;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getProperty() {
        return property;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WindowPropertyMessage{id=" + id + ",property=" + property + ",value=" + value + "}";
    }
}
