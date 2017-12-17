package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.protocol.HandshakeProtocol;

/**
 * Test cases for {@link HandshakeProtocol}.
 */
public class HandshakeProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
        new HandshakeMessage(1, "127.0.0.1", 25565, 1)
    };

    public HandshakeProtocolTest() {
        super(new HandshakeProtocol(), TEST_MESSAGES);
    }
}
