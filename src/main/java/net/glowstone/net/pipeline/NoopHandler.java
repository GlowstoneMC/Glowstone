package net.glowstone.net.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

/**
 * ChannelHandler which does nothing.
 */
@ChannelHandler.Sharable
public final class NoopHandler extends ChannelHandlerAdapter {

    public static final NoopHandler INSTANCE = new NoopHandler();

    private NoopHandler() {
    }

}
