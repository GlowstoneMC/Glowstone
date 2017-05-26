package net.glowstone.net.pipeline;

import com.flowpowered.network.Message;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.glowstone.EventFactory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.event.PacketReceiveEvent;
import net.glowstone.net.event.PacketSendEvent;

public class PacketAPIHandler extends ChannelDuplexHandler {

    private GlowSession session;

    public PacketAPIHandler(GlowSession session) {
        this.session = session;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        PacketSendEvent event = new PacketSendEvent(session, (Message) msg);
        EventFactory.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        PacketReceiveEvent event = new PacketReceiveEvent(session, (Message) msg);
        EventFactory.callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        super.channelRead(ctx, msg);
    }
}
