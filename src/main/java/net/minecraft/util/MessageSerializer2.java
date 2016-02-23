package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class MessageSerializer2 implements ChannelHandler {

    protected void encode(ChannelHandlerContext context, ByteBuf input, ByteBuf output) {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
