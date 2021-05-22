package net.glowstone.net;

import com.destroystokyo.paper.network.StatusClient;
import java.net.InetSocketAddress;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;

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
