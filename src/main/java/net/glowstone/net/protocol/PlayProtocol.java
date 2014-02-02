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
        super(server, "PLAY");

        inbound(0x00, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x01, IncomingChatMessage.class, IncomingChatCodec.class, ChatHandler.class);
        inbound(0x03, PlayerUpdateMessage.class, PlayerUpdateCodec.class, PlayerUpdateHandler.class);
        inbound(0x04, PlayerPositionMessage.class, PlayerPositionCodec.class, PlayerUpdateHandler.class);
        inbound(0x05, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x06, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class, PlayerUpdateHandler.class);

        outbound(0x00, PingMessage.class, PingCodec.class);
        outbound(0x01, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x02, ChatMessage.class, JsonCodec.class);
        outbound(0x03, TimeMessage.class, TimeCodec.class);
        outbound(0x05, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x06, HealthMessage.class, HealthCodec.class);
        outbound(0x08, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x1F, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x21, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x23, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x26, ChunkBulkMessage.class, ChunkBulkCodec.class);
        outbound(0x2B, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x2F, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x40, KickMessage.class, JsonCodec.class);
    }
}
