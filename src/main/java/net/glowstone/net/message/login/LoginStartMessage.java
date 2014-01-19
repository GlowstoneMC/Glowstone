package net.glowstone.net.message.login;

import com.flowpowered.networking.Message;

public final class LoginStartMessage implements Message {

    private final String username;

    public LoginStartMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
