package net.glowstone.net.protocol;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.net.config.DnsEndpoint;
import net.glowstone.net.http.HttpClient;
import net.glowstone.util.config.ServerConfig;

/**
 * Enumeration of the different Minecraft protocol states.
 */
@Getter
@RequiredArgsConstructor
public class ProtocolProvider {
    public final HandshakeProtocol handshake;
    public final StatusProtocol status;
    public final LoginProtocol login;
    public final PlayProtocol play;

    public ProtocolProvider(ServerConfig serverConfig) {
        List<DnsEndpoint> dnsEndpoints = serverConfig.getMapList(ServerConfig.Key.DNS_OVERRIDES)
            .stream()
            .map(DnsEndpoint::fromConfigMap)
            .collect(Collectors.toList());
        HttpClient httpClient = new HttpClient(dnsEndpoints);

        this.status = new StatusProtocol();
        this.login = new LoginProtocol(httpClient);
        this.handshake = new HandshakeProtocol(status, login);
        this.play = new PlayProtocol();
    }
}
