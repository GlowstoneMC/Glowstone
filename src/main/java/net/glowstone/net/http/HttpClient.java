package net.glowstone.net.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.resolver.dns.DefaultDnsServerAddressStreamProvider;
import io.netty.resolver.dns.DnsAddressResolverGroup;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import lombok.AllArgsConstructor;

public class HttpClient {

    private static DnsAddressResolverGroup resolverGroup = new DnsAddressResolverGroup(
        Epoll.isAvailable() ? EpollDatagramChannel.class : NioDatagramChannel.class,
        DefaultDnsServerAddressStreamProvider.INSTANCE);

    /**
     * Opens a URL.
     *
     * @param url the URL to download
     * @param eventLoop an {@link EventLoop} that will receive the response body
     * @param callback a callback to handle the response or any error
     */
    public static void connect(String url, EventLoop eventLoop, HttpCallback callback) {

        URI uri = URI.create(url);

        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();

        SslContext sslCtx = null;
        if ("https".equalsIgnoreCase(scheme)) {
            if (port == -1) {
                port = 443;
            }
            try {
                sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            } catch (SSLException e) {
                callback.error(e);
                return;
            }
        } else if ("http".equalsIgnoreCase(scheme)) {
            if (port == -1) {
                port = 80;
            }
        } else {
            throw new IllegalArgumentException("Only http(s) is supported!");
        }

        new Bootstrap()
            .group(eventLoop)
            .resolver(resolverGroup)
            .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
            .handler(new HttpChannelInitializer(sslCtx, callback))
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .connect(InetSocketAddress.createUnresolved(host, port))
            .addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    String path = uri.getRawPath() + (uri.getRawQuery() == null ? ""
                        : "?" + uri.getRawQuery());
                    HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
                        HttpMethod.GET, path);
                    request.headers().set(HttpHeaderNames.HOST, host);
                    future.channel().writeAndFlush(request);
                } else {
                    callback.error(future.cause());
                }
            });
    }

    @AllArgsConstructor
    private static class HttpChannelInitializer extends ChannelInitializer<Channel> {

        private SslContext sslCtx;
        private HttpCallback callback;

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline()
                .addLast("timeout", new ReadTimeoutHandler(6000, TimeUnit.MILLISECONDS));
            if (sslCtx != null) {
                channel.pipeline().addLast("ssl", sslCtx.newHandler(channel.alloc()));
            }
            channel.pipeline().addLast("codec", new HttpClientCodec());
            channel.pipeline().addLast("handler", new HttpHandler(callback));
        }
    }

}
