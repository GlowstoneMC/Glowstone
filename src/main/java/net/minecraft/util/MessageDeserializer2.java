package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class MessageDeserializer2 implements ChannelHandler {

    protected void decode(ChannelHandlerContext context, ByteBuf input, List output) {

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
