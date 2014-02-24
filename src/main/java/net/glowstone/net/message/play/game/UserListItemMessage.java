package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class UserListItemMessage implements Message {

    private final String name;
    private final boolean online;
    private final int ping;

    public UserListItemMessage(String name, boolean online, int ping) {
        this.name = name;
        this.online = online;
        this.ping = ping;
    }

    public String getName() {
        return name;
    }

    public boolean getOnline() {
        return online;
    }

    public int getPing() {
        return ping;
    }

    @Override
    public String toString() {
        return "UserListItemMessage{name=" + name + ",online=" + online +  ",ping=" + ping + "}";
    }
}
