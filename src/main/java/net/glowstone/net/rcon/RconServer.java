package net.glowstone.net.rcon;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSocketServer;
import net.glowstone.net.protocol.ProtocolProvider;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of a server for the remote console protocol.
 *
 * @see <a href="http://wiki.vg/Rcon">Protocol Specifications</a>
 */
public class RconServer extends GlowSocketServer {

    /**
     * Creates an instance.
     *
     * @param server   the associated GlowServer
     * @param latch    The countdown latch used during server startup to wait for network server
     *                 binding.
     * @param password the remote operator's password
     */
    public RconServer(GlowServer server, ProtocolProvider protocolProvider,
                      CountDownLatch latch, String password) {
        super(server, protocolProvider, latch);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                    .addLast(new RconFramingHandler())
                    .addLast(new RconHandler(RconServer.this, password));
            }
        });
    }

    /**
     * Bind the server on the specified address.
     *
     * @param address The address.
     * @return Netty channel future for bind operation.
     */
    @Override
    public ChannelFuture bind(InetSocketAddress address) {
        GlowServer.logger.info("Binding rcon to "
            + address.getAddress().getHostAddress() + ":" + address.getPort() + "...");
        return super.bind(address);
    }

    @Override
    public void onBindSuccess(InetSocketAddress address) {
        GlowServer.logger.info("Successfully bound rcon to "
            + address.getAddress().getHostAddress() + ":" + address.getPort() + '.');
        super.onBindSuccess(address);
    }

    @Override
    public void onBindFailure(InetSocketAddress address, Throwable t) {
        GlowServer.logger.warning("Failed to bind rcon to "
            + address.getAddress().getHostAddress() + ":" + address.getPort() + '.');
    }

    /**
     * Shut the Rcon server down.
     */
    @Override
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
