package net.glowstone.net.message.handshake;

import com.flowpowered.networking.Message;

public final class HandshakeMessage implements Message {

    private final int version;
    private final String address;
    private final int port;
    private final int state;

    public HandshakeMessage(int version, String address, int port, int state) {
        this.version = version;
        this.address = address;
        this.port = port;
        this.state = state;
    }

    public int getVersion() {
        return version;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getState() {
        return state;
    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
