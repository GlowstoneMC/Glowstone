package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
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
        new SetCompressionMessage(5)
    };

    public LoginProtocolTest() {
        super(new LoginProtocol(), TEST_MESSAGES);
    }
}
