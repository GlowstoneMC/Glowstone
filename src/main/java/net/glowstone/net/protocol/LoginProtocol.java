package net.glowstone.net.protocol;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.JsonCodec;
import net.glowstone.net.codec.login.EncryptionKeyRequestCodec;
import net.glowstone.net.codec.login.EncryptionKeyResponseCodec;
import net.glowstone.net.codec.login.LoginStartCodec;
import net.glowstone.net.codec.login.LoginSuccessCodec;
import net.glowstone.net.handler.login.EncryptionKeyResponseHandler;
import net.glowstone.net.handler.login.LoginStartHandler;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;

public final class LoginProtocol extends GlowProtocol {
    public LoginProtocol(GlowServer server) {
        super(server, "LOGIN", 5);

        registerMessage(INBOUND, LoginStartMessage.class, LoginStartCodec.class, LoginStartHandler.class, 0x00);
        registerMessage(INBOUND, EncryptionKeyResponseMessage.class, EncryptionKeyResponseCodec.class, EncryptionKeyResponseHandler.class, 0x01);

        registerMessage(OUTBOUND, KickMessage.class, JsonCodec.class, null, 0x00);
        registerMessage(OUTBOUND, EncryptionKeyRequestMessage.class, EncryptionKeyRequestCodec.class, null, 0x01);
        registerMessage(OUTBOUND, LoginSuccessMessage.class, LoginSuccessCodec.class, null, 0x02);
    }
}
