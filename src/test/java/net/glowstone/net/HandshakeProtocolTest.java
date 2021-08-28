package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.HandshakeProtocol;
import net.glowstone.net.protocol.ProtocolProvider;
import net.glowstone.util.config.ServerConfig;

import java.util.Collections;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Test cases for {@link HandshakeProtocol}.
 */
public class HandshakeProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
        new HandshakeMessage(1, "127.0.0.1", 25565, 1)
    };

    private static HandshakeProtocol createHandshakeProtocol() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        when(serverConfig.getMapList(ServerConfig.Key.DNS_OVERRIDES)).thenReturn(Collections.emptyList());
        ProtocolProvider protocolProvider = new ProtocolProvider(serverConfig);
        return protocolProvider.handshake;
    }

    public HandshakeProtocolTest() {
        super(createHandshakeProtocol(), TEST_MESSAGES);
    }
}
