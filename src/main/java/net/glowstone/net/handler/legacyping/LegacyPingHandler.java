package net.glowstone.net.handler.legacyping;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.net.GameServer;
import org.bukkit.event.server.ServerListPingEvent;

public class LegacyPingHandler extends ChannelInboundHandlerAdapter {

    GameServer networkServer;

    public LegacyPingHandler(GameServer networkServer) {
        this.networkServer = networkServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) {
        ByteBuf bytebuf = (ByteBuf) object;

        bytebuf.markReaderIndex();
        boolean legacyPingProtocol = false;

        try {
            if (bytebuf.readByte() == (byte) 0xFE) {
                GlowServer server = networkServer.getServer();
                int readableBytes = bytebuf.readableBytes();

                InetSocketAddress inetsocketaddress = (InetSocketAddress) channelHandlerContext
                    .channel().remoteAddress();

                ServerListPingEvent legacyPingEvent = new ServerListPingEvent(
                    inetsocketaddress.getAddress(), server.getMotd(),
                    server.getOnlinePlayers().size(), server.getMaxPlayers());
                EventFactory.getInstance().callEvent(legacyPingEvent);

                switch (readableBytes) {
                    case 0:
                        sendByteBuf(channelHandlerContext, responseToByteBuf(channelHandlerContext,
                            String.format("%s§%d§%d", legacyPingEvent.getMotd(),
                                legacyPingEvent.getNumPlayers(), legacyPingEvent.getMaxPlayers())));
                        legacyPingProtocol = true;
                        break;
                    case 1:
                        if (bytebuf.readByte() == (byte) 0x01) {
                            sendByteBuf(channelHandlerContext,
                                responseToByteBuf(channelHandlerContext, String
                                    .format("§1\0%d\0%s\0%s\0%d\0%d",
                                        GlowServer.PROTOCOL_VERSION, GlowServer.GAME_VERSION,
                                        legacyPingEvent.getMotd(), legacyPingEvent.getNumPlayers(),
                                        legacyPingEvent.getMaxPlayers())));
                            legacyPingProtocol = true;
                        }
                        break;
                    default:
                        if (bytebuf.readByte() == (byte) 0x01 && bytebuf.readByte() == (byte) 0xFA
                            && "MC|PingHost".equals(
                            new String(bytebuf.readBytes(bytebuf.readShort() << 1).array(),
                                Charsets.UTF_16BE))) {
                            int dataLength = bytebuf.readUnsignedShort();
                            short clientVersion = bytebuf.readUnsignedByte();
                            String hostname = bytebuf.readBytes(bytebuf.readShort() << 1)
                                .toString(Charsets.UTF_16BE);
                            @SuppressWarnings("unused")
                            int port = bytebuf.readInt();

                            if (clientVersion >= 73 && 7 + (hostname.length() << 1) == dataLength
                                && bytebuf.readableBytes() == 0) {
                                sendByteBuf(channelHandlerContext,
                                    responseToByteBuf(channelHandlerContext, String
                                        .format("§1\0%d\0%s\0%s\0%d\0%d",
                                            GlowServer.PROTOCOL_VERSION, GlowServer.GAME_VERSION,
                                            legacyPingEvent.getMotd(),
                                            legacyPingEvent.getNumPlayers(),
                                            legacyPingEvent.getMaxPlayers())));
                                legacyPingProtocol = true;
                            }
                        }
                        break;
                }
            }
        } catch (RuntimeException expected) {
            // Silently catch the exception
        } finally {
            // check if not successful, otherwise the connection has already been closed
            if (!legacyPingProtocol) {
                bytebuf.resetReaderIndex();
                channelHandlerContext.pipeline().remove("legacy_ping");
                channelHandlerContext.fireChannelRead(object);
            }
        }
    }

    private void sendByteBuf(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) {
        channelhandlercontext.writeAndFlush(bytebuf).addListener(ChannelFutureListener.CLOSE);
    }

    private ByteBuf responseToByteBuf(ChannelHandlerContext ctx, String string) {
        ByteBuf bytebuf = ctx.alloc().buffer(3 + string.length());

        bytebuf.writeByte(0xFF);
        bytebuf.writeShort(string.length());
        try {
            bytebuf.writeBytes(string.getBytes("UTF-16BE"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return bytebuf;
    }
}
