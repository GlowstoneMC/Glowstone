package net.glowstone.net.pipeline;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;

/**
 * ChannelHandler which does nothing.
 */
@ChannelHandler.Sharable
public final class NoopHandler extends ChannelHandlerAdapter {

    private NoopHandler() {}

    public static NoopHandler instance = new NoopHandler();

}
