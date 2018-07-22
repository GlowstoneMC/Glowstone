package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.SetCompressionCodec;
import net.glowstone.net.codec.login.EncryptionKeyRequestCodec;
import net.glowstone.net.codec.login.EncryptionKeyResponseCodec;
import net.glowstone.net.codec.login.LoginStartCodec;
import net.glowstone.net.codec.login.LoginSuccessCodec;
import net.glowstone.net.handler.login.EncryptionKeyResponseHandler;
import net.glowstone.net.handler.login.LoginStartHandler;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;

public final class LoginProtocol extends GlowProtocol {

    /**
     * Creates the instance.
     */
    public LoginProtocol() {
        super("LOGIN", 5);

        inbound(0x00, LoginStartMessage.class, LoginStartCodec.class, LoginStartHandler.class);
        inbound(0x01, EncryptionKeyResponseMessage.class, EncryptionKeyResponseCodec.class,
            EncryptionKeyResponseHandler.class);
        // TODO 0x02 : Login Plugin Request packet

        outbound(0x00, KickMessage.class, KickCodec.class);
        outbound(0x01, EncryptionKeyRequestMessage.class, EncryptionKeyRequestCodec.class);
        outbound(0x02, LoginSuccessMessage.class, LoginSuccessCodec.class);
        outbound(0x03, SetCompressionMessage.class, SetCompressionCodec.class);
        // TODO 0x04 : Login Plugin Response packet
    }
}
