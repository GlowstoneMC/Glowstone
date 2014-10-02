package net.glowstone.net.protocol;

import net.glowstone.net.codec.status.StatusPingCodec;
import net.glowstone.net.codec.status.StatusRequestCodec;
import net.glowstone.net.codec.status.StatusResponseCodec;
import net.glowstone.net.handler.status.StatusPingHandler;
import net.glowstone.net.handler.status.StatusRequestHandler;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;

public final class StatusProtocol extends GlowProtocol {
    public StatusProtocol() {
        super("STATUS", 2);

        inbound(0x00, StatusRequestMessage.class, StatusRequestCodec.class, StatusRequestHandler.class);
        inbound(0x01, StatusPingMessage.class, StatusPingCodec.class, StatusPingHandler.class);

        outbound(0x00, StatusResponseMessage.class, StatusResponseCodec.class);
        outbound(0x01, StatusPingMessage.class, StatusPingCodec.class);
    }
}
