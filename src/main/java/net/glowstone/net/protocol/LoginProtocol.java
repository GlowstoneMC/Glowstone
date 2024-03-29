package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.SetCompressionCodec;
import net.glowstone.net.handler.login.EncryptionKeyResponseHandler;
import net.glowstone.net.handler.login.LoginPluginResponseHandler;
import net.glowstone.net.handler.login.LoginStartHandler;
import net.glowstone.net.http.HttpClient;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestCodec;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseCodec;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseWithSignature;
import net.glowstone.net.message.login.EncryptionKeyResponseWithVerifyToken;
import net.glowstone.net.message.login.LoginPluginRequestCodec;
import net.glowstone.net.message.login.LoginPluginRequestMessage;
import net.glowstone.net.message.login.LoginPluginResponseCodec;
import net.glowstone.net.message.login.LoginPluginResponseMessage;
import net.glowstone.net.message.login.LoginStartCodec;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessCodec;
import net.glowstone.net.message.login.LoginSuccessMessage;

public class LoginProtocol extends GlowProtocol {

    /**
     * Creates the instance.
     */
    public LoginProtocol(final HttpClient httpClient) {
        super("LOGIN", 0x04);

        inbound(0x00, LoginStartMessage.class, LoginStartCodec.class, LoginStartHandler.class);
        inbound(0x01, EncryptionKeyResponseMessage.class, EncryptionKeyResponseCodec.class,
            new EncryptionKeyResponseHandler(httpClient));
        inbound(0x01, EncryptionKeyResponseWithVerifyToken.class, EncryptionKeyResponseCodec.class,
                new EncryptionKeyResponseHandler(httpClient));
        inbound(0x01, EncryptionKeyResponseWithSignature.class, EncryptionKeyResponseCodec.class,
                new EncryptionKeyResponseHandler(httpClient));
        inbound(0x02, LoginPluginResponseMessage.class, LoginPluginResponseCodec.class,
            LoginPluginResponseHandler.class);

        outbound(0x00, KickMessage.class, KickCodec.class);
        outbound(0x01, EncryptionKeyRequestMessage.class, EncryptionKeyRequestCodec.class);
        outbound(0x02, LoginSuccessMessage.class, LoginSuccessCodec.class);
        outbound(0x03, SetCompressionMessage.class, SetCompressionCodec.class);
        outbound(0x04, LoginPluginRequestMessage.class, LoginPluginRequestCodec.class);
    }
}
