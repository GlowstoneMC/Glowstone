package net.glowstone.net;

import com.flowpowered.network.Message;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.LoginPluginRequestMessage;
import net.glowstone.net.message.login.LoginPluginResponseMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.protocol.LoginProtocol;
import net.glowstone.net.protocol.ProtocolProvider;
import net.glowstone.util.config.ServerConfig;

import java.util.Collections;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

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

    private static LoginProtocol createLoginProtocol() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        when(serverConfig.getMapList(ServerConfig.Key.DNS_OVERRIDES)).thenReturn(Collections.emptyList());
        ProtocolProvider protocolProvider = new ProtocolProvider(serverConfig);
        return protocolProvider.login;
    }

    public LoginProtocolTest() {
        super(createLoginProtocol(), TEST_MESSAGES);
    }
}
