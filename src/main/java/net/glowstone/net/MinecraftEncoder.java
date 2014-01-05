package net.glowstone.net;

import net.glowstone.net.message.Message;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * A {@link OneToOneEncoder} which encodes Minecraft {@link net.glowstone.net.message.Message}s into
 * {@link ChannelBuffer}s.
 */
public class MinecraftEncoder extends OneToOneEncoder {

    private final MinecraftHandler handler;

    public MinecraftEncoder(MinecraftHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof Message) {
            Message message = (Message) msg;
            Session session = handler.session; // hacky, fix later
            MessageMap map = MessageMap.getForState(session.getState());
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            map.encode(message, buffer);
            return buffer;
        }
        return msg;
    }

}
