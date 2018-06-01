package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.net.protocol.StatusProtocol;

/**
 * Test cases for {@link StatusProtocol}.
 */
public class StatusProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
        new StatusPingMessage(1),
        new StatusResponseMessage(ProtocolTestUtils.getJson()),
        new StatusRequestMessage(),
    };

    public StatusProtocolTest() {
        super(new StatusProtocol(), TEST_MESSAGES);
    }
}
