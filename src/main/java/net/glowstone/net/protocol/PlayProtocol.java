package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.play.entity.*;
import net.glowstone.net.codec.play.game.*;
import net.glowstone.net.codec.play.inv.*;
import net.glowstone.net.codec.play.player.*;
import net.glowstone.net.codec.play.scoreboard.*;
import net.glowstone.net.handler.play.game.*;
import net.glowstone.net.handler.play.inv.*;
import net.glowstone.net.handler.play.player.*;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;
import net.glowstone.net.message.play.scoreboard.*;

public class PlayProtocol extends GlowProtocol {

    /**
     * Creates the instance for the game's main network protocol.
     */
    public PlayProtocol() {
        super("PLAY", 0x55);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class,
            TeleportConfirmHandler.class);
        inbound(0x01, QueryBlockNBTMessage.class, QueryBlockNBTCodec.class,
                QueryBlockNBTHandler.class);
        inbound(0x02, IncomingChatMessage.class, IncomingChatCodec.class,
            IncomingChatHandler.class);
        inbound(0x03, ClientStatusMessage.class, ClientStatusCodec.class,
            ClientStatusHandler.class);
        inbound(0x04, ClientSettingsMessage.class, ClientSettingsCodec.class,
            ClientSettingsHandler.class);
        inbound(0x05, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x06, TransactionMessage.class, TransactionCodec.class, TransactionHandler.class);
        inbound(0x07, EnchantItemMessage.class, EnchantItemCodec.class, EnchantItemHandler.class);
        inbound(0x08, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x09, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x0A, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);
        inbound(0x0B, EditBookMessage.class, EditBookCodec.class, EditBookHandler.class);
        inbound(0x0C, QueryEntityNBTMessage.class, QueryEntityNBTCodec.class,
                QueryEntityNBTHandler.class);
        inbound(0x0D, InteractEntityMessage.class, InteractEntityCodec.class,
            InteractEntityHandler.class);
        inbound(0x0E, PingMessage.class, PingCodec.class, PingHandler.class);
        inbound(0x0F, PlayerUpdateMessage.class, PlayerUpdateCodec.class,
            PlayerUpdateHandler.class);
        inbound(0x10, PlayerPositionMessage.class, PlayerPositionCodec.class,
            PlayerUpdateHandler.class);
        inbound(0x11, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class,
            PlayerUpdateHandler.class);
        inbound(0x12, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x13, VehicleMoveMessage.class, VehicleMoveCodec.class, VehicleMoveHandler.class);
        inbound(0x14, SteerBoatMessage.class, SteerBoatCodec.class, SteerBoatHandler.class);
        inbound(0x15, PickItemMessage.class, PickItemCodec.class, PickItemHandler.class);
        inbound(0x16, CraftRecipeRequestMessage.class, CraftRecipeRequestCodec.class,
            CraftRecipeRequestHandler.class);
        inbound(0x17, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class,
            PlayerAbilitiesHandler.class);
        inbound(0x18, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x19, PlayerActionMessage.class, PlayerActionCodec.class,
            PlayerActionHandler.class);
        inbound(0x1A, SteerVehicleMessage.class, SteerVehicleCodec.class,
            SteerVehicleHandler.class);
        inbound(0x1B, CraftingBookDataMessage.class, CraftingBookDataCodec.class,
            CraftingBookDataHandler.class);
        inbound(0x1C, NameItemMessage.class, NameItemCodec.class, NameItemHandler.class);
        inbound(0x1D, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class,
            ResourcePackStatusHandler.class);
        inbound(0x1E, AdvancementTabMessage.class, AdvancementTabCodec.class,
            AdvancementTabHandler.class);
        inbound(0x1F, SelectTradeMessage.class, SelectTradeCodec.class, SelectTradeHandler.class);
        inbound(0x20, SetBeaconEffectMessage.class, SetBeaconEffectCodec.class,
                SetBeaconEffectHandler.class);
        inbound(0x21, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        inbound(0x22, UpdateCommandBlockMessage.class, UpdateCommandBlockCodec.class,
                UpdateCommandBlockHandler.class);
        inbound(0x23, UpdateCommandBlockMinecartMessage.class,
                UpdateCommandBlockMinecartCodec.class, UpdateCommandBlockMinecartHandler.class);
        inbound(0x24, CreativeItemMessage.class, CreativeItemCodec.class,
            CreativeItemHandler.class);
        inbound(0x25, UpdateStructureBlockMessage.class, UpdateStructureBlockCodec.class,
                UpdateStructureBlockHandler.class);
        inbound(0x26, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x27, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class,
            PlayerSwingArmHandler.class);
        inbound(0x28, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x29, BlockPlacementMessage.class, BlockPlacementCodec.class,
            BlockPlacementHandler.class);
        inbound(0x2A, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

        outbound(0x00, SpawnObjectMessage.class, SpawnObjectCodec.class);
        outbound(0x01, SpawnXpOrbMessage.class, SpawnXpOrbCodec.class);
        outbound(0x02, SpawnGlobalEntityMessage.class, SpawnGlobalEntityCodec.class);
        outbound(0x03, SpawnMobMessage.class, SpawnMobCodec.class);
        outbound(0x04, SpawnPaintingMessage.class, SpawnPaintingCodec.class);
        outbound(0x05, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x06, EntityAnimationMessage.class, EntityAnimationCodec.class);
        outbound(0x07, StatisticsMessage.class, StatisticsCodec.class);
        outbound(0x08, AcknowledgePlayerDiggingMessage.class, AcknowledgePlayerDiggingCodec.class);
        outbound(0x09, BlockBreakAnimationMessage.class, BlockBreakAnimationCodec.class);
        outbound(0x0A, UpdateBlockEntityMessage.class, UpdateBlockEntityCodec.class);
        outbound(0x0B, BlockActionMessage.class, BlockActionCodec.class);
        outbound(0x0C, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x0D, BossBarMessage.class, BossBarCodec.class);
        outbound(0x0E, ServerDifficultyMessage.class, ServerDifficultyCodec.class);
        outbound(0x0F, ChatMessage.class, ChatCodec.class);
        outbound(0x10, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x11, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        outbound(0x12, DeclareCommandsMessage.class, DeclareCommandsCodec.class);
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
        outbound(0x1D, NBTQueryResponseMessage.class, NBTQueryResponseCodec.class);
        outbound(0x1E, ExplosionMessage.class, ExplosionCodec.class);
        outbound(0x1F, UnloadChunkMessage.class, UnloadChunkCodec.class);
        outbound(0x20, StateChangeMessage.class, StateChangeCodec.class);
        outbound(0x21, PingMessage.class, PingCodec.class);
        outbound(0x22, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x23, PlayEffectMessage.class, PlayEffectCodec.class);
        outbound(0x24, PlayParticleMessage.class, PlayParticleCodec.class);
        outbound(0x25, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x26, MapDataMessage.class, MapDataCodec.class);
        // TODO 0x27 : Entity packet
        outbound(0x28, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x29, RelativeEntityPositionRotationMessage.class,
            RelativeEntityPositionRotationCodec.class);
        outbound(0x2A, EntityRotationMessage.class, EntityRotationCodec.class);
        outbound(0x2B, VehicleMoveMessage.class, VehicleMoveCodec.class);
        outbound(0x2C, SignEditorMessage.class, SignEditorCodec.class);
        outbound(0x2D, CraftRecipeResponseMessage.class, CraftRecipeResponseCodec.class);
        outbound(0x2E, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        outbound(0x2F, CombatEventMessage.class, CombatEventCodec.class);
        outbound(0x30, UserListItemMessage.class, UserListItemCodec.class);
        // TODO 0x31 : Face Player packet
        outbound(0x32, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x33, UseBedMessage.class, UseBedCodec.class);
        outbound(0x34, UnlockRecipesMessage.class, UnlockRecipesCodec.class);
        outbound(0x35, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x36, EntityRemoveEffectMessage.class, EntityRemoveEffectCodec.class);
        outbound(0x37, ResourcePackSendMessage.class, ResourcePackSendCodec.class);
        outbound(0x38, RespawnMessage.class, RespawnCodec.class);
        outbound(0x39, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        // TODO 0x3A : Select Advancement Tab
        outbound(0x3B, WorldBorderMessage.class, WorldBorderCodec.class);
        outbound(0x3C, CameraMessage.class, CameraCodec.class);
        outbound(0x3D, HeldItemMessage.class, HeldItemCodec.class);
        outbound(0x3E, ScoreboardDisplayMessage.class, ScoreboardDisplayCodec.class);
        outbound(0x3F, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x40, AttachEntityMessage.class, AttachEntityCodec.class);
        outbound(0x41, EntityVelocityMessage.class, EntityVelocityCodec.class);
        outbound(0x42, EntityEquipmentMessage.class, EntityEquipmentCodec.class);
        outbound(0x43, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x44, HealthMessage.class, HealthCodec.class);
        outbound(0x45, ScoreboardObjectiveMessage.class, ScoreboardObjectiveCodec.class);
        outbound(0x46, SetPassengerMessage.class, SetPassengerCodec.class);
        outbound(0x47, ScoreboardTeamMessage.class, ScoreboardTeamCodec.class);
        outbound(0x48, ScoreboardScoreMessage.class, ScoreboardScoreCodec.class);
        outbound(0x49, SpawnPositionMessage.class, SpawnPositionCodec.class);
        outbound(0x4A, TimeMessage.class, TimeCodec.class);
        outbound(0x4B, TitleMessage.class, TitleCodec.class);
        // TODO 0x4C : Stop Sound packet
        outbound(0x4D, SoundEffectMessage.class, SoundEffectCodec.class);
        outbound(0x4E, UserListHeaderFooterMessage.class, UserListHeaderFooterCodec.class);
        outbound(0x4F, CollectItemMessage.class, CollectItemCodec.class);
        outbound(0x50, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x51, AdvancementsMessage.class, AdvancementsCodec.class);
        outbound(0x52, EntityPropertyMessage.class, EntityPropertyCodec.class);
        outbound(0x53, EntityEffectMessage.class, EntityEffectCodec.class);
        // TODO 0x54 : Declare Recipes packet
        // TODO 0x55 : Tags packet
    }
}
