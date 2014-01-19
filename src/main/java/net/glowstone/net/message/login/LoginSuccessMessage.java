package net.glowstone.net.message.login;

import com.flowpowered.networking.Message;

public final class LoginSuccessMessage implements Message {

    private final String uuid;
    private final String username;

    public LoginSuccessMessage(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "LoginSuccessMessage{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
