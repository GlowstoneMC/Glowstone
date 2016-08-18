package net.glowstone.net.rcon;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import net.glowstone.GlowServer;

import java.net.SocketAddress;

/**
 * Implementation of a server for the remote console protocol.
 *
 * @see <a href="http://wiki.vg/Rcon">Protocol Specifications</a>
 */
public class RconServer {

    private final GlowServer server;
    private final EventLoopGroup bossGroup = new EpollEventLoopGroup();
    private final EventLoopGroup workerGroup = new EpollEventLoopGroup();
    private ServerBootstrap bootstrap = new ServerBootstrap();

    public RconServer(GlowServer server, String password) {
        this.server = server;

        bootstrap
                .group(bossGroup, workerGroup)
                .channel(EpollServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new RconFramingHandler())
                                .addLast(new RconHandler(RconServer.this, password));
                    }
                });
    }

    public GlowServer getServer() {
        return server;
    }

    /**
     * Bind the server on the specified address.
     *
     * @param address The address.
     * @return Netty channel future for bind operation.
     */
    public ChannelFuture bind(SocketAddress address) {
        return bootstrap.bind(address);
    }

    /**
     * Shut the Rcon server down.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
