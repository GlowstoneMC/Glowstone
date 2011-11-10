package net.glowstone.msg;

public class UserListItemMessage extends Message {

    private final String name;
    private final boolean addOrRemove;
    private final short ping;

    public UserListItemMessage(String name, boolean addOrRemove, short ping) {
        this.name = name;
        this.addOrRemove = addOrRemove;
        this.ping = ping;
    }

    public String getName() {
        return name;
    }

    public boolean addOrRemove() {
        return addOrRemove;
    }

    public short getPing() {
        return ping;
    }

    @Override
    public String toString() {
        return "UserListItemMessage{name=" + name + ",addOrRemove=" + addOrRemove +  ",ping=" + ping + "}";
    }
}
