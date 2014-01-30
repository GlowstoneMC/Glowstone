package net.glowstone.net.protocol;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.JsonCodec;
import net.glowstone.net.codec.status.StatusPingCodec;
import net.glowstone.net.codec.status.StatusRequestCodec;
import net.glowstone.net.handler.status.StatusPingHandler;
import net.glowstone.net.handler.status.StatusRequestHandler;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;

public final class StatusProtocol extends GlowProtocol {
    public StatusProtocol(GlowServer server) {
        super(server, "STATUS", 2);

        registerMessage(INBOUND, StatusRequestMessage.class, StatusRequestCodec.class, StatusRequestHandler.class, 0x00);
        registerMessage(INBOUND, StatusPingMessage.class, StatusPingCodec.class, StatusPingHandler.class, 0x01);

        registerMessage(OUTBOUND, StatusResponseMessage.class, JsonCodec.class, null, 0x00);
        registerMessage(OUTBOUND, StatusPingMessage.class, StatusPingCodec.class, null, 0x01);
    }
}
