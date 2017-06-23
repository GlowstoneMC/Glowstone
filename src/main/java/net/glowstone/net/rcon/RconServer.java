package net.glowstone.net.rcon;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.glowstone.GlowServer;
import net.glowstone.net.GlowSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of a server for the remote console protocol.
 *
 * @see <a href="http://wiki.vg/Rcon">Protocol Specifications</a>
 */
public class RconServer extends GlowSocketServer {

    public RconServer(GlowServer server, CountDownLatch latch, String password) {
        super(server, latch);
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
    public ChannelFuture bind(InetSocketAddress address) {
        GlowServer.logger.info("Binding rcon to " + address + "...");
        return super.bind(address);
    }

    @Override
    public void onBindSuccess(InetSocketAddress address) {
        GlowServer.logger.info("Successfully bound rcon to " + address + '.');
        super.onBindSuccess(address);
    }

    @Override
    public void onBindFailure(InetSocketAddress address, Throwable t) {
        GlowServer.logger.warning("Failed to bind rcon to " + address + '.');
    }

    /**
     * Shut the Rcon server down.
     */
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
