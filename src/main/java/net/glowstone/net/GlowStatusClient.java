package net.glowstone.net;

import com.destroystokyo.paper.network.StatusClient;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class GlowStatusClient implements StatusClient {

    private final GlowSession session;

    @Override
    public InetSocketAddress getAddress() {
        return session.getAddress();
    }

    @Override
    public int getProtocolVersion() {
        return session.getVersion();
    }

    @Nullable
    @Override
    public InetSocketAddress getVirtualHost() {
        return session.getVirtualHost();
    }
}
