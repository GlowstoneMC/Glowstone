package net.glowstone.net.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.AllArgsConstructor;

import javax.net.ssl.SSLException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    public static void connect(String url, EventLoop eventLoop, HttpCallback callback) {

        final URI uri = URI.create(url);

        String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();

        SslContext sslCtx = null;
        if ("https".equalsIgnoreCase(scheme)) {
            if (port == -1) port = 443;
            try {
                sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            } catch (SSLException e) {
                callback.error(e);
                return;
            }
        } else if ("http".equalsIgnoreCase(scheme)) {
            if (port == -1) port = 80;
        } else {
            throw new IllegalArgumentException("Only http(s) is supported!");
        }

        InetAddress address;
        try {
            address = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            callback.error(e);
            return;
        }

        new Bootstrap()
                .group(eventLoop)
                .channel(NioSocketChannel.class) // TODO epoll
                .handler(new HttpChannelInitializer(sslCtx, callback))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .connect(address, port)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            String path = uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
                            HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
                            request.headers().set("host", host);
                            future.channel().writeAndFlush(request);
                        } else {
                            callback.error(future.cause());
                        }
                    }
                });
    }

    @AllArgsConstructor
    private static class HttpChannelInitializer extends ChannelInitializer<Channel> {

        private SslContext sslCtx;
        private HttpCallback callback;

        @Override
        protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast("timeout", new ReadTimeoutHandler(6000, TimeUnit.MILLISECONDS));
            if (sslCtx != null) {
                channel.pipeline().addLast("ssl", sslCtx.newHandler(channel.alloc()));
            }
            channel.pipeline().addLast("codec", new HttpClientCodec());
            channel.pipeline().addLast("handler", new HttpHandler(callback));
        }
    }

}
