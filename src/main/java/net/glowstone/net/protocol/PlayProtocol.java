package net.glowstone.net.protocol;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.JsonCodec;
import net.glowstone.net.codec.play.game.*;
import net.glowstone.net.codec.play.player.PlayerLookCodec;
import net.glowstone.net.codec.play.player.PlayerPositionCodec;
import net.glowstone.net.codec.play.player.PlayerPositionLookCodec;
import net.glowstone.net.codec.play.player.PlayerUpdateCodec;
import net.glowstone.net.handler.play.game.ChatHandler;
import net.glowstone.net.handler.play.game.PingHandler;
import net.glowstone.net.handler.play.player.PlayerUpdateHandler;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.player.PlayerLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionMessage;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;

public final class PlayProtocol extends GlowProtocol {

    public PlayProtocol(GlowServer server) {
        super(server, "PLAY", 43);

        registerMessage(INBOUND, PingMessage.class, PingCodec.class, PingHandler.class, 0x00);
        registerMessage(INBOUND, IncomingChatMessage.class, IncomingChatCodec.class, ChatHandler.class, 0x01);
        registerMessage(INBOUND, PlayerUpdateMessage.class, PlayerUpdateCodec.class, PlayerUpdateHandler.class, 0x03);
        registerMessage(INBOUND, PlayerPositionMessage.class, PlayerPositionCodec.class, PlayerUpdateHandler.class, 0x04);
        registerMessage(INBOUND, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class, 0x05);
        registerMessage(INBOUND, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class, PlayerUpdateHandler.class, 0x06);

        registerMessage(OUTBOUND, PingMessage.class, PingCodec.class, null, 0x00);
        registerMessage(OUTBOUND, JoinGameMessage.class, JoinGameCodec.class, null, 0x01);
        registerMessage(OUTBOUND, ChatMessage.class, JsonCodec.class, null, 0x02);
        registerMessage(OUTBOUND, TimeMessage.class, TimeCodec.class, null, 0x03);
        registerMessage(OUTBOUND, SpawnPositionMessage.class, SpawnPositionCodec.class, null, 0x05);
        registerMessage(OUTBOUND, PositionRotationMessage.class, PositionRotationCodec.class, null, 0x08);
        registerMessage(OUTBOUND, ChunkDataMessage.class, ChunkDataCodec.class, null, 0x21);
        registerMessage(OUTBOUND, BlockChangeMessage.class, BlockChangeCodec.class, null, 0x23);
        registerMessage(OUTBOUND, StateChangeMessage.class, StateChangeCodec.class, null, 0x2B);
        registerMessage(OUTBOUND, KickMessage.class, JsonCodec.class, null, 0x40);
    }
}
