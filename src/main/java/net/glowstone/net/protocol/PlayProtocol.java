package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.play.entity.*;
import net.glowstone.net.codec.play.game.*;
import net.glowstone.net.codec.play.inv.*;
import net.glowstone.net.codec.play.player.*;
import net.glowstone.net.codec.play.scoreboard.ScoreboardDisplayCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardObjectiveCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardScoreCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardTeamCodec;
import net.glowstone.net.handler.play.game.*;
import net.glowstone.net.handler.play.inv.*;
import net.glowstone.net.handler.play.player.*;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;

public class PlayProtocol extends GlowProtocol {
    public PlayProtocol() {
        super("PLAY", 0x4B);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class, TeleportConfirmHandler.class);
        // TODO 0x01 : Unknown packet
        inbound(0x02, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x03, IncomingChatMessage.class, IncomingChatCodec.class, IncomingChatHandler.class);
        inbound(0x04, ClientStatusMessage.class, ClientStatusCodec.class, ClientStatusHandler.class);
        inbound(0x05, ClientSettingsMessage.class, ClientSettingsCodec.class, ClientSettingsHandler.class);
        inbound(0x06, TransactionMessage.class, TransactionCodec.class, TransactionHandler.class);
        inbound(0x07, EnchantItemMessage.class, EnchantItemCodec.class, EnchantItemHandler.class);
        inbound(0x08, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x09, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x0A, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);
        inbound(0x0B, InteractEntityMessage.class, InteractEntityCodec.class, InteractEntityHandler.class);
        inbound(0x0C, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x0D, PlayerPositionMessage.class, PlayerPositionCodec.class, PlayerUpdateHandler.class);
        inbound(0x0E, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x0F, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x10, PlayerUpdateMessage.class, PlayerUpdateCodec.class, PlayerUpdateHandler.class);
        inbound(0x11, VehicleMoveMessage.class, VehicleMoveCodec.class, VehicleMoveHandler.class);
        // TODO: 0x12 : Steer boat
        inbound(0x13, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class, PlayerAbilitiesHandler.class);
        inbound(0x14, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x15, PlayerActionMessage.class, PlayerActionCodec.class, PlayerActionHandler.class);
        inbound(0x16, SteerVehicleMessage.class, SteerVehicleCodec.class, SteerVehicleHandler.class);
        // TODO: 0x17 : Recipe Displayed
        inbound(0x18, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class, ResourcePackStatusHandler.class);
        inbound(0x19, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        inbound(0x1A, CreativeItemMessage.class, CreativeItemCodec.class, CreativeItemHandler.class);
        inbound(0x1B, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x1C, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class, PlayerSwingArmHandler.class);
        inbound(0x1D, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x1E, BlockPlacementMessage.class, BlockPlacementCodec.class, BlockPlacementHandler.class);
        inbound(0x1F, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

        outbound(0x00, SpawnObjectMessage.class, SpawnObjectCodec.class);
        outbound(0x01, SpawnXpOrbMessage.class, SpawnXpOrbCodec.class);
        outbound(0x02, SpawnLightningStrikeMessage.class, SpawnLightningStrikeCodec.class);
        outbound(0x03, SpawnMobMessage.class, SpawnMobCodec.class);
        outbound(0x04, SpawnPaintingMessage.class, SpawnPaintingCodec.class);
        outbound(0x05, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x06, AnimateEntityMessage.class, AnimateEntityCodec.class);
        outbound(0x07, StatisticMessage.class, StatisticCodec.class);
        // TODO 0x08 : Advancements packet
        // TODO 0x09 : Block break animation
        outbound(0x0A, UpdateBlockEntityMessage.class, UpdateBlockEntityCodec.class);
        outbound(0x0B, BlockActionMessage.class, BlockActionCodec.class);
        outbound(0x0C, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x0D, BossBarMessage.class, BossBarCodec.class);
        outbound(0x0E, ServerDifficultyMessage.class, ServerDifficultyCodec.class);
        outbound(0x0F, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        outbound(0x10, ChatMessage.class, ChatCodec.class);
        outbound(0x11, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x12, TransactionMessage.class, TransactionCodec.class);
        outbound(0x13, CloseWindowMessage.class, CloseWindowCodec.class);
        outbound(0x14, OpenWindowMessage.class, OpenWindowCodec.class);
        outbound(0x15, SetWindowContentsMessage.class, SetWindowContentsCodec.class);
        outbound(0x16, WindowPropertyMessage.class, WindowPropertyCodec.class);
        outbound(0x17, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x18, SetCooldownMessage.class, SetCooldownCodec.class);
        outbound(0x19, PluginMessage.class, PluginMessageCodec.class);
        outbound(0x1A, NamedSoundEffectMessage.class, NamedSoundEffectCodec.class);
        outbound(0x1B, KickMessage.class, KickCodec.class);
        outbound(0x1C, EntityStatusMessage.class, EntityStatusCodec.class);
        outbound(0x1D, ExplosionMessage.class, ExplosionCodec.class);
        outbound(0x1E, UnloadChunkMessage.class, UnloadChunkCodec.class);
        outbound(0x1F, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x20, PingMessage.class, PingCodec.class);
        outbound(0x21, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x22, PlayEffectMessage.class, PlayEffectCodec.class);
        outbound(0x23, PlayParticleMessage.class, PlayParticleCodec.class);
        outbound(0x24, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x25, MapDataMessage.class, MapDataCodec.class);
        outbound(0x26, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x27, RelativeEntityPositionRotationMessage.class, RelativeEntityPositionRotationCodec.class);
        outbound(0x28, EntityRotationMessage.class, EntityRotationCodec.class);
        // TODO 0x29: Entity packet
        outbound(0x2A, VehicleMoveMessage.class, VehicleMoveCodec.class);
        outbound(0x2B, SignEditorMessage.class, SignEditorCodec.class);
        outbound(0x2C, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        outbound(0x2D, CombatEventMessage.class, CombatEventCodec.class);
        outbound(0x2E, UserListItemMessage.class, UserListItemCodec.class);
        outbound(0x2F, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x30, UseBedMessage.class, UseBedCodec.class);
        // TODO 0x31 : Unlock Recipe
        outbound(0x32, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x33, EntityRemoveEffectMessage.class, EntityRemoveEffectCodec.class);
        outbound(0x34, ResourcePackSendMessage.class, ResourcePackSendCodec.class);
        outbound(0x35, RespawnMessage.class, RespawnCodec.class);
        outbound(0x36, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        outbound(0x37, WorldBorderMessage.class, WorldBorderCodec.class);
        outbound(0x38, CameraMessage.class, CameraCodec.class);
        outbound(0x39, HeldItemMessage.class, HeldItemCodec.class);
        outbound(0x3A, ScoreboardDisplayMessage.class, ScoreboardDisplayCodec.class);
        outbound(0x3B, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x3C, AttachEntityMessage.class, AttachEntityCodec.class);
        outbound(0x3D, EntityVelocityMessage.class, EntityVelocityCodec.class);
        outbound(0x3E, EntityEquipmentMessage.class, EntityEquipmentCodec.class);
        outbound(0x3F, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x40, HealthMessage.class, HealthCodec.class);
        outbound(0x41, ScoreboardObjectiveMessage.class, ScoreboardObjectiveCodec.class);
        outbound(0x42, SetPassengerMessage.class, SetPassengerCodec.class);
        outbound(0x43, ScoreboardTeamMessage.class, ScoreboardTeamCodec.class);
        outbound(0x44, ScoreboardScoreMessage.class, ScoreboardScoreCodec.class);
        outbound(0x45, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x46, TimeMessage.class, TimeCodec.class);
        outbound(0x47, TitleMessage.class, TitleCodec.class);
        outbound(0x48, SoundEffectMessage.class, SoundEffectCodec.class);
        outbound(0x49, UserListHeaderFooterMessage.class, UserListHeaderFooterCodec.class);
        outbound(0x4A, CollectItemMessage.class, CollectItemCodec.class);
        outbound(0x4B, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x4C, EntityPropertyMessage.class, EntityPropertyCodec.class);
        outbound(0x4D, EntityEffectMessage.class, EntityEffectCodec.class);
    }
}
