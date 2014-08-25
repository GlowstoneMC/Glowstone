package net.glowstone.net.pipeline;

import com.flowpowered.networking.ConnectionManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Used to initialize the channels.
 */
public class GlowChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ConnectionManager connectionManager;

    public GlowChannelInitializer(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    protected final void initChannel(SocketChannel c) {
        MessageHandler handler = new MessageHandler(connectionManager);
        CodecsHandler codecs = new CodecsHandler(handler);
        FramingHandler framing = new FramingHandler(handler);

        c.pipeline()
                //.addLast("encryption", encryption)
                .addLast("framing", framing)
                //.addLast("compression", compression)
                .addLast("codecs", codecs)
                .addLast("handler", handler);
    }
}
