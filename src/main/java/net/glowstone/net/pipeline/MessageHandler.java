package net.glowstone.net.pipeline;

import com.flowpowered.networking.Message;
import com.flowpowered.networking.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import net.glowstone.net.GlowNetworkServer;
import net.glowstone.net.GlowSession;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Experimental pipeline component, based on flow-net's MessageHandler.
 */
public final class MessageHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * The associated session
     */
    private final AtomicReference<GlowSession> session = new AtomicReference<>(null);
    private final GlowNetworkServer connectionManager;

    /**
     * Creates a new network event handler.
     */
    public MessageHandler(GlowNetworkServer connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel c = ctx.channel();
        GlowSession s = connectionManager.newSession(c);
        if (!session.compareAndSet(null, s)) {
            throw new IllegalStateException("Session may not be set more than once");
        }
        s.onReady();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = this.session.get();
        session.onDisconnect();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Message i) {
        session.get().messageReceived(i);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            session.get().idle(); // todo: find a more elegant way to do this in the future
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        session.get().onInboundThrowable(cause);
    }

}
