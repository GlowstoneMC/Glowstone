package net.glowstone.net.codec;

import net.glowstone.msg.ServerListPingMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class ServerListPingCodec extends MessageCodec<ServerListPingMessage> {

    private static final ServerListPingMessage LIST_PING_MESSAGE = new ServerListPingMessage();

    public ServerListPingCodec() {
        super(ServerListPingMessage.class, 0xFE);
    }

    @Override
    public ChannelBuffer encode(ServerListPingMessage message) throws IOException {
        return ChannelBuffers.EMPTY_BUFFER;
    }

    @Override
    public ServerListPingMessage decode(ChannelBuffer buffer) throws IOException {
        return LIST_PING_MESSAGE;
    }
    
}
