package net.glowstone.msg;

public class UserListItemMessage extends Message {

    private final String name;
    private final boolean something;
    private final short ping;

    public UserListItemMessage(String name, boolean something, short ping) {
        this.name = name;
        this.something = something;
        this.ping = ping;
    }

    public String getName() {
        return name;
    }

    public boolean getSomething() {
        return something;
    }

    public short getPing() {
        return ping;
    }
    
}
