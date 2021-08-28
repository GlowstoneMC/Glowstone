package net.glowstone.net.pipeline;

import com.flowpowered.network.Message;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import net.glowstone.net.GameServer;
import net.glowstone.net.GlowSession;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Experimental pipeline component, based on flow-net's MessageHandler.
 */
public final class MessageHandler extends SimpleChannelInboundHandler<Message> {

    /**
     * The associated session.
     */
    private final AtomicReference<GlowSession> session = new AtomicReference<>(null);
    private final GameServer connectionManager;

    /**
     * Creates a new network event handler.
     *
     * @param connectionManager The connection manager to manage connections for this message
     *                          handler.
     */
    public MessageHandler(GameServer connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel c = ctx.channel();
        GlowSession s = connectionManager.newSession(c);
        if (!session.compareAndSet(null, s)) {
            throw new IllegalStateException("Session may not be set more than once");
        }
        s.onReady();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        session.get().onDisconnect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message i) {
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

    public GlowSession getSession() {
        return session.get();
    }

    public void setSession(GlowSession newSession) {
        session.set(newSession);
    }
}
