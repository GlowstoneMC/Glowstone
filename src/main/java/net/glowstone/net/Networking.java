package net.glowstone.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueDatagramChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class Networking {
    public static final boolean EPOLL_AVAILABLE = Epoll.isAvailable();
    public static final boolean KQUEUE_AVAILABLE = KQueue.isAvailable();

    private Networking() {
    }

    /**
     * Creates the "best" event loop group available.
     *
     * <p>Epoll and KQueue are favoured and will be returned if available, followed by NIO.</p>
     *
     * @return the "best" event loop group available
     */
    public static EventLoopGroup createBestEventLoopGroup() {
        if (EPOLL_AVAILABLE) {
            return new EpollEventLoopGroup();
        } else if (KQUEUE_AVAILABLE) {
            return new KQueueEventLoopGroup();
        } else {
            return new NioEventLoopGroup();
        }
    }

    /**
     * Gets the "best" server socket channel available.
     *
     * <p>Epoll and KQueue are favoured and will be returned if available, followed by NIO.</p>
     *
     * @return the "best" server socket channel available
     */
    public static Class<? extends ServerSocketChannel> bestServerSocketChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollServerSocketChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueServerSocketChannel.class;
        } else {
            return NioServerSocketChannel.class;
        }
    }

    /**
     * Gets the "best" socket channel available.
     *
     * <p>Epoll and KQueue are favoured and will be returned if available, followed by NIO.</p>
     *
     * @return the "best" socket channel available
     */
    public static Class<? extends SocketChannel> bestSocketChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollSocketChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueSocketChannel.class;
        } else {
            return NioSocketChannel.class;
        }
    }

    /**
     * Gets the "best" datagram channel available.
     *
     * <p>Epoll and KQueue are favoured and will be returned if available, followed by NIO.</p>
     *
     * @return the "best" datagram channel available
     */
    public static Class<? extends DatagramChannel> bestDatagramChannel() {
        if (EPOLL_AVAILABLE) {
            return EpollDatagramChannel.class;
        } else if (KQUEUE_AVAILABLE) {
            return KQueueDatagramChannel.class;
        } else {
            return NioDatagramChannel.class;
        }
    }
}
