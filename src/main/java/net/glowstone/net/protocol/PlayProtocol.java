package net.glowstone.net.protocol;

import net.glowstone.GlowServer;
import net.glowstone.net.codec.JsonCodec;
import net.glowstone.net.codec.play.entity.*;
import net.glowstone.net.codec.play.game.*;
import net.glowstone.net.codec.play.inv.*;
import net.glowstone.net.codec.play.player.*;
import net.glowstone.net.handler.play.entity.AnimateEntityHandler;
import net.glowstone.net.handler.play.game.ChatHandler;
import net.glowstone.net.handler.play.game.ClientSettingsHandler;
import net.glowstone.net.handler.play.game.PingHandler;
import net.glowstone.net.handler.play.game.PluginMessageHandler;
import net.glowstone.net.handler.play.inv.*;
import net.glowstone.net.handler.play.player.*;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;

public final class PlayProtocol extends GlowProtocol {
    public PlayProtocol(GlowServer server) {
        super(server, "PLAY", 0x43);

        inbound(0x00, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x01, IncomingChatMessage.class, IncomingChatCodec.class, ChatHandler.class);
        inbound(0x02, InteractEntityMessage.class, InteractEntityCodec.class, InteractEntityHandler.class);
        inbound(0x03, PlayerUpdateMessage.class, PlayerUpdateCodec.class, PlayerUpdateHandler.class);
        inbound(0x04, PlayerPositionMessage.class, PlayerPositionCodec.class, PlayerUpdateHandler.class);
        inbound(0x05, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x06, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x07, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x08, BlockPlacementMessage.class, BlockPlacementCodec.class, BlockPlacementHandler.class);
        inbound(0x09, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        inbound(0x0A, AnimateEntityMessage.class, AnimateEntityCodec.class, AnimateEntityHandler.class);
        inbound(0x0B, PlayerActionMessage.class, PlayerActionCodec.class, PlayerActionHandler.class);
        inbound(0x0D, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x0E, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x0F, TransactionMessage.class, TransactionCodec.class, TransactionHandler.class);
        inbound(0x10, CreativeItemMessage.class, CreativeItemCodec.class, CreativeItemHandler.class);
        inbound(0x13, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class, PlayerAbilitiesHandler.class);
        inbound(0x14, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x15, ClientSettingsMessage.class, ClientSettingsCodec.class, ClientSettingsHandler.class);
        inbound(0x17, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);

        outbound(0x00, PingMessage.class, PingCodec.class);
        outbound(0x01, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x02, ChatMessage.class, JsonCodec.class);
        outbound(0x03, TimeMessage.class, TimeCodec.class);
        outbound(0x05, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x06, HealthMessage.class, HealthCodec.class);
        outbound(0x08, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x09, HeldItemMessage.class, HeldItemCodec.class);
        outbound(0x0B, AnimateEntityMessage.class, AnimateEntityCodec.class);
        outbound(0x0C, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x0F, SpawnMobMessage.class, SpawnMobCodec.class);
        outbound(0x13, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x15, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x16, EntityRotationMessage.class, EntityRotationCodec.class);
        outbound(0x17, RelativeEntityPositionRotationMessage.class, RelativeEntityPositionRotationCodec.class);
        outbound(0x18, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x19, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        outbound(0x1C, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x1F, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x21, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x22, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x23, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x26, ChunkBulkMessage.class, ChunkBulkCodec.class);
        outbound(0x2B, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x2C, SpawnLightningStrikeMessage.class, SpawnLightningStrikeCodec.class);
        outbound(0x2D, OpenWindowMessage.class, OpenWindowCodec.class);
        outbound(0x2E, CloseWindowMessage.class, CloseWindowCodec.class);
        outbound(0x2F, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x30, SetWindowContentsMessage.class, SetWindowContentsCodec.class);
        outbound(0x31, WindowPropertyMessage.class, WindowPropertyCodec.class);
        outbound(0x32, TransactionMessage.class, TransactionCodec.class);
        outbound(0x39, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        outbound(0x3F, PluginMessage.class, PluginMessageCodec.class);
        outbound(0x40, KickMessage.class, JsonCodec.class);
    }
}
