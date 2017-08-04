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
        super("PLAY", 0x4F);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class, TeleportConfirmHandler.class);
        inbound(0x01, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x02, IncomingChatMessage.class, IncomingChatCodec.class, IncomingChatHandler.class);
        inbound(0x03, ClientStatusMessage.class, ClientStatusCodec.class, ClientStatusHandler.class);
        inbound(0x04, ClientSettingsMessage.class, ClientSettingsCodec.class, ClientSettingsHandler.class);
        inbound(0x05, TransactionMessage.class, TransactionCodec.class, TransactionHandler.class);
        inbound(0x06, EnchantItemMessage.class, EnchantItemCodec.class, EnchantItemHandler.class);
        inbound(0x07, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x08, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x09, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);
        inbound(0x0A, InteractEntityMessage.class, InteractEntityCodec.class, InteractEntityHandler.class);
        inbound(0x0B, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x0C, PlayerUpdateMessage.class, PlayerUpdateCodec.class, PlayerUpdateHandler.class);
        inbound(0x0D, PlayerPositionMessage.class, PlayerPositionCodec.class, PlayerUpdateHandler.class);
        inbound(0x0E, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x0F, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x10, VehicleMoveMessage.class, VehicleMoveCodec.class, VehicleMoveHandler.class);
        inbound(0x11, SteerBoatMessage.class, SteerBoatCodec.class, SteerBoatHandler.class);
        // TODO 0x12 : Craft Recipe Request
        inbound(0x13, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class, PlayerAbilitiesHandler.class);
        inbound(0x14, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x15, PlayerActionMessage.class, PlayerActionCodec.class, PlayerActionHandler.class);
        inbound(0x16, SteerVehicleMessage.class, SteerVehicleCodec.class, SteerVehicleHandler.class);
        // TODO 0x17 : Crafting Book Data
        inbound(0x18, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class, ResourcePackStatusHandler.class);
        inbound(0x19, AdvancementTabMessage.class, AdvancementTabCodec.class, AdvancementTabHandler.class);
        inbound(0x1A, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        inbound(0x1B, CreativeItemMessage.class, CreativeItemCodec.class, CreativeItemHandler.class);
        inbound(0x1C, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x1D, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class, PlayerSwingArmHandler.class);
        inbound(0x1E, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x1F, BlockPlacementMessage.class, BlockPlacementCodec.class, BlockPlacementHandler.class);
        inbound(0x20, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

        outbound(0x00, SpawnObjectMessage.class, SpawnObjectCodec.class);
        outbound(0x01, SpawnXpOrbMessage.class, SpawnXpOrbCodec.class);
        outbound(0x02, SpawnLightningStrikeMessage.class, SpawnLightningStrikeCodec.class);
        outbound(0x03, SpawnMobMessage.class, SpawnMobCodec.class);
        outbound(0x04, SpawnPaintingMessage.class, SpawnPaintingCodec.class);
        outbound(0x05, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x06, AnimateEntityMessage.class, AnimateEntityCodec.class);
        outbound(0x07, StatisticMessage.class, StatisticCodec.class);
        outbound(0x08, BlockBreakAnimationMessage.class, BlockBreakAnimationCodec.class);
        outbound(0x09, UpdateBlockEntityMessage.class, UpdateBlockEntityCodec.class);
        outbound(0x0A, BlockActionMessage.class, BlockActionCodec.class);
        outbound(0x0B, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x0C, BossBarMessage.class, BossBarCodec.class);
        outbound(0x0D, ServerDifficultyMessage.class, ServerDifficultyCodec.class);
        outbound(0x0E, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        outbound(0x0F, ChatMessage.class, ChatCodec.class);
        outbound(0x10, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x11, TransactionMessage.class, TransactionCodec.class);
        outbound(0x12, CloseWindowMessage.class, CloseWindowCodec.class);
        outbound(0x13, OpenWindowMessage.class, OpenWindowCodec.class);
        outbound(0x14, SetWindowContentsMessage.class, SetWindowContentsCodec.class);
        outbound(0x15, WindowPropertyMessage.class, WindowPropertyCodec.class);
        outbound(0x16, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x17, SetCooldownMessage.class, SetCooldownCodec.class);
        outbound(0x18, PluginMessage.class, PluginMessageCodec.class);
        outbound(0x19, NamedSoundEffectMessage.class, NamedSoundEffectCodec.class);
        outbound(0x1A, KickMessage.class, KickCodec.class);
        outbound(0x1B, EntityStatusMessage.class, EntityStatusCodec.class);
        outbound(0x1C, ExplosionMessage.class, ExplosionCodec.class);
        outbound(0x1D, UnloadChunkMessage.class, UnloadChunkCodec.class);
        outbound(0x1E, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x1F, PingMessage.class, PingCodec.class);
        outbound(0x20, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x21, PlayEffectMessage.class, PlayEffectCodec.class);
        outbound(0x22, PlayParticleMessage.class, PlayParticleCodec.class);
        outbound(0x23, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x24, MapDataMessage.class, MapDataCodec.class);
        // TODO 0x25 : Entity packet
        outbound(0x26, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x27, RelativeEntityPositionRotationMessage.class, RelativeEntityPositionRotationCodec.class);
        outbound(0x28, EntityRotationMessage.class, EntityRotationCodec.class);
        outbound(0x29, VehicleMoveMessage.class, VehicleMoveCodec.class);
        outbound(0x2A, SignEditorMessage.class, SignEditorCodec.class);
        // TODO 0x2B : Craft Recipe Response
        outbound(0x2C, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        outbound(0x2D, CombatEventMessage.class, CombatEventCodec.class);
        outbound(0x2E, UserListItemMessage.class, UserListItemCodec.class);
        outbound(0x2F, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x30, UseBedMessage.class, UseBedCodec.class);
        // TODO 0x31 : Unlock Recipes
        outbound(0x32, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x33, EntityRemoveEffectMessage.class, EntityRemoveEffectCodec.class);
        outbound(0x34, ResourcePackSendMessage.class, ResourcePackSendCodec.class);
        outbound(0x35, RespawnMessage.class, RespawnCodec.class);
        outbound(0x36, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        // TODO 0x37 : Select Advancement Tab
        outbound(0x38, WorldBorderMessage.class, WorldBorderCodec.class);
        outbound(0x39, CameraMessage.class, CameraCodec.class);
        outbound(0x3A, HeldItemMessage.class, HeldItemCodec.class);
        outbound(0x3B, ScoreboardDisplayMessage.class, ScoreboardDisplayCodec.class);
        outbound(0x3C, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x3D, AttachEntityMessage.class, AttachEntityCodec.class);
        outbound(0x3E, EntityVelocityMessage.class, EntityVelocityCodec.class);
        outbound(0x3F, EntityEquipmentMessage.class, EntityEquipmentCodec.class);
        outbound(0x40, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x41, HealthMessage.class, HealthCodec.class);
        outbound(0x42, ScoreboardObjectiveMessage.class, ScoreboardObjectiveCodec.class);
        outbound(0x43, SetPassengerMessage.class, SetPassengerCodec.class);
        outbound(0x44, ScoreboardTeamMessage.class, ScoreboardTeamCodec.class);
        outbound(0x45, ScoreboardScoreMessage.class, ScoreboardScoreCodec.class);
        outbound(0x46, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x47, TimeMessage.class, TimeCodec.class);
        outbound(0x48, TitleMessage.class, TitleCodec.class);
        outbound(0x49, SoundEffectMessage.class, SoundEffectCodec.class);
        outbound(0x4A, UserListHeaderFooterMessage.class, UserListHeaderFooterCodec.class);
        outbound(0x4B, CollectItemMessage.class, CollectItemCodec.class);
        outbound(0x4C, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x4D, AdvancementsMessage.class, AdvancementsCodec.class);
        outbound(0x4E, EntityPropertyMessage.class, EntityPropertyCodec.class);
        outbound(0x4F, EntityEffectMessage.class, EntityEffectCodec.class);
    }
}
