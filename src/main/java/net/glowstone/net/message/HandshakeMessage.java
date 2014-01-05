package net.glowstone.net.message;

import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public final class HandshakeMessage extends Message {

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

    public HandshakeMessage(ChannelBuffer buf) {
        version = ChannelBufferUtils.readVarInt(buf);
        address = ChannelBufferUtils.readString(buf);
        port = buf.readUnsignedShort();
        state = ChannelBufferUtils.readVarInt(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeVarInt(buf, version);
        ChannelBufferUtils.writeString(buf, address);
        buf.writeShort(port);
        ChannelBufferUtils.writeVarInt(buf, state);
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
}
