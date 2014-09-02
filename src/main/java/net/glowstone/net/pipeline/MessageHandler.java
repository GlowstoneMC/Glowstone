package net.glowstone.net.pipeline;

import com.flowpowered.networking.ConnectionManager;
import com.flowpowered.networking.Message;
import com.flowpowered.networking.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Experimental pipeline component, based on flow-net's MessageHandler.
 */
public final class MessageHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * The associated session
     */
    private final AtomicReference<Session> session = new AtomicReference<>(null);
    private final ConnectionManager connectionManager;

    /**
     * Creates a new network event handler.
     */
    public MessageHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel c = ctx.channel();
        Session s = connectionManager.newSession(c);
        if (!session.compareAndSet(null, s)) {
            throw new IllegalStateException("Session may not be set more than once");
        }
        s.onReady();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = this.session.get();
        session.onDisconnect();
        connectionManager.sessionInactivated(session);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message i) {
        session.get().messageReceived(i);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        session.get().onInboundThrowable(cause);
    }

}
