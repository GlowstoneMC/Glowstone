package net.glowstone.net;

import com.flowpowered.network.Message;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.*;
import net.glowstone.net.protocol.LoginProtocol;

/**
 * Test cases for {@link LoginProtocol}.
 */
public class LoginProtocolTest extends BaseProtocolTest {

    private static final Message[] TEST_MESSAGES = new Message[]{
        new LoginStartMessage("glowstone"),
        new EncryptionKeyRequestMessage("sessionid1", new byte[]{0x00, 0x01},
            new byte[]{0x02, 0x03}),
        new KickMessage(ProtocolTestUtils.getTextMessage()),
        new KickMessage("Hello"),
        new EncryptionKeyResponseMessage(new byte[]{0x00, 0x01}, new byte[]{0x02, 0x03}),
        new LoginSuccessMessage("glowstone", "glowstone1"),
        new SetCompressionMessage(5),
        new LoginPluginResponseMessage(0, true, new byte[]{0x00, 0x01}),
        new LoginPluginRequestMessage(0, "channel", new byte[]{0x00, 0x01})
    };

    public LoginProtocolTest() {
        super(new LoginProtocol(), TEST_MESSAGES);
    }
}
