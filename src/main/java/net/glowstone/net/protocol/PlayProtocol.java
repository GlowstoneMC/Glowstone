package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.play.entity.AttachEntityCodec;
import net.glowstone.net.codec.play.entity.CollectItemCodec;
import net.glowstone.net.codec.play.entity.DestroyEntitiesCodec;
import net.glowstone.net.codec.play.entity.EntityAnimationCodec;
import net.glowstone.net.codec.play.entity.EntityEffectCodec;
import net.glowstone.net.codec.play.entity.EntityEquipmentCodec;
import net.glowstone.net.codec.play.entity.EntityHeadRotationCodec;
import net.glowstone.net.codec.play.entity.EntityMetadataCodec;
import net.glowstone.net.codec.play.entity.EntityPropertyCodec;
import net.glowstone.net.codec.play.entity.EntityRemoveEffectCodec;
import net.glowstone.net.codec.play.entity.EntityRotationCodec;
import net.glowstone.net.codec.play.entity.EntityStatusCodec;
import net.glowstone.net.codec.play.entity.EntityTeleportCodec;
import net.glowstone.net.codec.play.entity.EntityVelocityCodec;
import net.glowstone.net.codec.play.entity.RelativeEntityPositionCodec;
import net.glowstone.net.codec.play.entity.RelativeEntityPositionRotationCodec;
import net.glowstone.net.codec.play.entity.SetCooldownCodec;
import net.glowstone.net.codec.play.entity.SetPassengerCodec;
import net.glowstone.net.codec.play.entity.SpawnEntityCodec;
import net.glowstone.net.codec.play.entity.SpawnPlayerCodec;
import net.glowstone.net.codec.play.entity.SpawnXpOrbCodec;
import net.glowstone.net.codec.play.entity.VehicleMoveCodec;
import net.glowstone.net.codec.play.game.*;
import net.glowstone.net.codec.play.inv.ClickWindowButtonCodec;
import net.glowstone.net.codec.play.inv.CloseWindowCodec;
import net.glowstone.net.codec.play.inv.CreativeItemCodec;
import net.glowstone.net.codec.play.inv.HeldItemCodec;
import net.glowstone.net.codec.play.inv.OpenWindowCodec;
import net.glowstone.net.codec.play.inv.SetWindowContentsCodec;
import net.glowstone.net.codec.play.inv.SetWindowSlotCodec;
import net.glowstone.net.codec.play.inv.WindowClickCodec;
import net.glowstone.net.codec.play.inv.WindowPropertyCodec;
import net.glowstone.net.codec.play.player.AdvancementTabCodec;
import net.glowstone.net.codec.play.player.AdvancementsCodec;
import net.glowstone.net.codec.play.player.BlockPlacementCodec;
import net.glowstone.net.codec.play.player.BossBarCodec;
import net.glowstone.net.codec.play.player.CameraCodec;
import net.glowstone.net.codec.play.player.ClientStatusCodec;
import net.glowstone.net.codec.play.player.DiggingCodec;
import net.glowstone.net.codec.play.player.InteractEntityCodec;
import net.glowstone.net.codec.play.player.PlayerAbilitiesCodec;
import net.glowstone.net.codec.play.player.PlayerActionCodec;
import net.glowstone.net.codec.play.player.PlayerLookCodec;
import net.glowstone.net.codec.play.player.PlayerPositionCodec;
import net.glowstone.net.codec.play.player.PlayerPositionLookCodec;
import net.glowstone.net.codec.play.player.PlayerSwingArmCodec;
import net.glowstone.net.codec.play.player.PlayerUpdateCodec;
import net.glowstone.net.codec.play.player.ResourcePackSendCodec;
import net.glowstone.net.codec.play.player.ResourcePackStatusCodec;
import net.glowstone.net.codec.play.player.ServerDifficultyCodec;
import net.glowstone.net.codec.play.player.SpectateCodec;
import net.glowstone.net.codec.play.player.SteerBoatCodec;
import net.glowstone.net.codec.play.player.SteerVehicleCodec;
import net.glowstone.net.codec.play.player.TabCompleteCodec;
import net.glowstone.net.codec.play.player.TabCompleteResponseCodec;
import net.glowstone.net.codec.play.player.TeleportConfirmCodec;
import net.glowstone.net.codec.play.player.UseItemCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardDisplayCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardObjectiveCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardScoreCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardTeamCodec;
import net.glowstone.net.handler.play.game.BeaconEffectHandler;
import net.glowstone.net.handler.play.game.ClientSettingsHandler;
import net.glowstone.net.handler.play.game.CraftRecipeRequestHandler;
import net.glowstone.net.handler.play.game.DisplayedRecipeHandler;
import net.glowstone.net.handler.play.game.EditBookHandler;
import net.glowstone.net.handler.play.game.IncomingChatHandler;
import net.glowstone.net.handler.play.game.NameItemHandler;
import net.glowstone.net.handler.play.game.PingHandler;
import net.glowstone.net.handler.play.game.PluginMessageHandler;
import net.glowstone.net.handler.play.game.RecipeBookStateHandler;
import net.glowstone.net.handler.play.game.UpdateSignHandler;
import net.glowstone.net.handler.play.inv.ClickWindowButtonHandler;
import net.glowstone.net.handler.play.inv.CloseWindowHandler;
import net.glowstone.net.handler.play.inv.CreativeItemHandler;
import net.glowstone.net.handler.play.inv.HeldItemHandler;
import net.glowstone.net.handler.play.inv.VehicleMoveHandler;
import net.glowstone.net.handler.play.inv.WindowClickHandler;
import net.glowstone.net.handler.play.player.AdvancementTabHandler;
import net.glowstone.net.handler.play.player.BlockPlacementHandler;
import net.glowstone.net.handler.play.player.ClientStatusHandler;
import net.glowstone.net.handler.play.player.DiggingHandler;
import net.glowstone.net.handler.play.player.InteractEntityHandler;
import net.glowstone.net.handler.play.player.PlayerAbilitiesHandler;
import net.glowstone.net.handler.play.player.PlayerActionHandler;
import net.glowstone.net.handler.play.player.PlayerSwingArmHandler;
import net.glowstone.net.handler.play.player.PlayerUpdateHandler;
import net.glowstone.net.handler.play.player.ResourcePackStatusHandler;
import net.glowstone.net.handler.play.player.SpectateHandler;
import net.glowstone.net.handler.play.player.SteerBoatHandler;
import net.glowstone.net.handler.play.player.SteerVehicleHandler;
import net.glowstone.net.handler.play.player.TabCompleteHandler;
import net.glowstone.net.handler.play.player.TeleportConfirmHandler;
import net.glowstone.net.handler.play.player.UseItemHandler;
import net.glowstone.net.message.KickMessage;
import net.glowstone.net.message.play.entity.AttachEntityMessage;
import net.glowstone.net.message.play.entity.CollectItemMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityAnimationMessage;
import net.glowstone.net.message.play.entity.EntityEffectMessage;
import net.glowstone.net.message.play.entity.EntityEquipmentMessage;
import net.glowstone.net.message.play.entity.EntityHeadRotationMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;
import net.glowstone.net.message.play.entity.EntityRemoveEffectMessage;
import net.glowstone.net.message.play.entity.EntityRotationMessage;
import net.glowstone.net.message.play.entity.EntityStatusMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;
import net.glowstone.net.message.play.entity.RelativeEntityPositionRotationMessage;
import net.glowstone.net.message.play.entity.SetCooldownMessage;
import net.glowstone.net.message.play.entity.SetPassengerMessage;
import net.glowstone.net.message.play.entity.SpawnEntityMessage;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.ClickWindowButtonMessage;
import net.glowstone.net.message.play.inv.CloseWindowMessage;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import net.glowstone.net.message.play.inv.OpenWindowMessage;
import net.glowstone.net.message.play.inv.SetWindowContentsMessage;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import net.glowstone.net.message.play.inv.WindowPropertyMessage;
import net.glowstone.net.message.play.player.AdvancementTabMessage;
import net.glowstone.net.message.play.player.AdvancementsMessage;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.net.message.play.player.BossBarMessage;
import net.glowstone.net.message.play.player.CameraMessage;
import net.glowstone.net.message.play.player.ClientStatusMessage;
import net.glowstone.net.message.play.player.DiggingMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import net.glowstone.net.message.play.player.PlayerActionMessage;
import net.glowstone.net.message.play.player.PlayerLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionLookMessage;
import net.glowstone.net.message.play.player.PlayerPositionMessage;
import net.glowstone.net.message.play.player.PlayerSwingArmMessage;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;
import net.glowstone.net.message.play.player.ResourcePackSendMessage;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;
import net.glowstone.net.message.play.player.ServerDifficultyMessage;
import net.glowstone.net.message.play.player.SpectateMessage;
import net.glowstone.net.message.play.player.SteerBoatMessage;
import net.glowstone.net.message.play.player.SteerVehicleMessage;
import net.glowstone.net.message.play.player.TabCompleteMessage;
import net.glowstone.net.message.play.player.TabCompleteResponseMessage;
import net.glowstone.net.message.play.player.TeleportConfirmMessage;
import net.glowstone.net.message.play.player.UseItemMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;

public class PlayProtocol extends GlowProtocol {

    /**
     * Creates the instance for the game's main network protocol.
     */
    public PlayProtocol() {
        super("PLAY", 0x68);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class,
            TeleportConfirmHandler.class);
        // TODO 0x01 : Query Block NBT
        // Note: 0x02 (Set Difficulty) is unused in multiplayer
        // TODO 0x03 Message Acknowledge
        // TODO 0x04 Chat Command
        // TODO 0x05 Update Chat Message Handling to relfect Protocol changes
        inbound(0x05, IncomingChatMessage.class, IncomingChatCodec.class,
            IncomingChatHandler.class);
        inbound(0x06, ClientStatusMessage.class, ClientStatusCodec.class,
            ClientStatusHandler.class);
        inbound(0x07, ClientSettingsMessage.class, ClientSettingsCodec.class,
            ClientSettingsHandler.class);
        inbound(0x08, TabCompleteMessage.class, TabCompleteCodec.class, TabCompleteHandler.class);
        inbound(0x09, ClickWindowButtonMessage.class, ClickWindowButtonCodec.class,
            ClickWindowButtonHandler.class);
        //TODO 0x0A : Update WindowClickMessage
        inbound(0x0A, WindowClickMessage.class, WindowClickCodec.class, WindowClickHandler.class);
        inbound(0x0B, CloseWindowMessage.class, CloseWindowCodec.class, CloseWindowHandler.class);
        inbound(0x0C, PluginMessage.class, PluginMessageCodec.class, PluginMessageHandler.class);
        //TODO 0x0D : Update EditBookMessage
        inbound(0x0D, EditBookMessage.class, EditBookCodec.class, EditBookHandler.class);
        // TODO 0x0E : Query Entity NBT
        // TODO 0x0F : Add shifting flag to InteractEntityMessage
        inbound(0x0F, InteractEntityMessage.class, InteractEntityCodec.class,
            InteractEntityHandler.class);
        // TODO 0x10 : Generate Structure (Jigsaw Block stuff: https://wiki.vg/Protocol#Generate_Structure)
        inbound(0x11, PingMessage.class, PingCodec.class, PingHandler.class);
        // Note: 0x12 (Lock Difficulty) is unused in multiplayer
        inbound(0x13, PlayerPositionMessage.class, PlayerPositionCodec.class,
            PlayerUpdateHandler.class);

        inbound(0x14, PlayerPositionLookMessage.class, PlayerPositionLookCodec.class,
            PlayerUpdateHandler.class);
        inbound(0x15, PlayerLookMessage.class, PlayerLookCodec.class, PlayerUpdateHandler.class);
        inbound(0x16, PlayerUpdateMessage.class, PlayerUpdateCodec.class,
            PlayerUpdateHandler.class);
        inbound(0x17, VehicleMoveMessage.class, VehicleMoveCodec.class, VehicleMoveHandler.class);
        inbound(0x18, SteerBoatMessage.class, SteerBoatCodec.class, SteerBoatHandler.class);
        // TODO 0x19 : Pick Item (see https://wiki.vg/Protocol#Pick_Item)
        inbound(0x1A, CraftRecipeRequestMessage.class, CraftRecipeRequestCodec.class,
            CraftRecipeRequestHandler.class);
        // TODO 0x1B Update PlayerAbilitiesMessage
        inbound(0x1B, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class,
            PlayerAbilitiesHandler.class);
        // TODO 0x1C Update DiggingMessage
        inbound(0x1C, DiggingMessage.class, DiggingCodec.class, DiggingHandler.class);
        inbound(0x1D, PlayerActionMessage.class, PlayerActionCodec.class,
            PlayerActionHandler.class);
        inbound(0x1E, SteerVehicleMessage.class, SteerVehicleCodec.class,
            SteerVehicleHandler.class);
        // TODO 0x1F : Pong
        // TODO 0x20 : PlayerSession
        inbound(0x21, RecipeBookStateMessage.class, RecipeBookStateCodec.class,
            RecipeBookStateHandler.class);
        inbound(0x22, DisplayedRecipeMessage.class, DisplayedRecipeCodec.class, DisplayedRecipeHandler.class);
        inbound(0x23, NameItemMessage.class, NameItemCodec.class, NameItemHandler.class);
        inbound(0x24, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class,
            ResourcePackStatusHandler.class);
        inbound(0x25, AdvancementTabMessage.class, AdvancementTabCodec.class,
            AdvancementTabHandler.class);
        // TODO 0x26 : Select Trade (when a player selects a specific trade in a villager GUI)
        // TODO 0x27 : Update BeaconEffectMessage
        inbound(0x27, BeaconEffectMessage.class, BeaconEffectCodec.class,
            BeaconEffectHandler.class);
        inbound(0x28, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        // TODO 0x29 : Update Command Block
        // TODO 0x2A : Update Command Block Minecart
        inbound(0x2B, CreativeItemMessage.class, CreativeItemCodec.class,
            CreativeItemHandler.class);
        // TODO 0x2C : Update Jigsaw Block
        // TODO 0x2D : Update Structure Block (difference with 0x29)
        inbound(0x2E, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x2F, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class,
            PlayerSwingArmHandler.class);
        inbound(0x30, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x31, BlockPlacementMessage.class, BlockPlacementCodec.class,
            BlockPlacementHandler.class);
        // TODO 0x32 : Add sequence field to UseItemMessage
        inbound(0x32, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

        outbound(0x00, SpawnEntityMessage.class, SpawnEntityCodec.class);
        outbound(0x01, SpawnXpOrbMessage.class, SpawnXpOrbCodec.class);
        outbound(0x02, SpawnPlayerMessage.class, SpawnPlayerCodec.class);
        outbound(0x03, EntityAnimationMessage.class, EntityAnimationCodec.class);
        outbound(0x04, StatisticsMessage.class, StatisticsCodec.class);
        outbound(0x05, AcknowledgeBlockChanges.class, AcknowledgeBlockChangesCodec.class);
        outbound(0x06, BlockBreakAnimationMessage.class, BlockBreakAnimationCodec.class);
        outbound(0x07, UpdateBlockEntityMessage.class, UpdateBlockEntityCodec.class);
        outbound(0x08, BlockActionMessage.class, BlockActionCodec.class);
        outbound(0x09, BlockChangeMessage.class, BlockChangeCodec.class);
        outbound(0x0A, BossBarMessage.class, BossBarCodec.class);
        outbound(0x0B, ServerDifficultyMessage.class, ServerDifficultyCodec.class);
        // TODO 0x0C : Clear Titles
        outbound(0x0D, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        outbound(0x0E, DeclareCommandsMessage.class, DeclareCommandsCodec.class);
        outbound(0x0F, CloseWindowMessage.class, CloseWindowCodec.class);
        outbound(0x10, SetWindowContentsMessage.class, SetWindowContentsCodec.class);
        outbound(0x11, WindowPropertyMessage.class, WindowPropertyCodec.class);
        outbound(0x12, SetWindowSlotMessage.class, SetWindowSlotCodec.class);
        outbound(0x13, SetCooldownMessage.class, SetCooldownCodec.class);
        //TODO 0x14 : Add ChatSuggestionsMessage
        outbound(0x15, PluginMessage.class, PluginMessageCodec.class);
        //TODO 0x16 : Add DeleteMessageMessage
        //outbound(0x16, NamedSoundEffectMessage.class, NamedSoundEffectCodec.class);
        outbound(0x17, KickMessage.class, KickCodec.class);
        //TODO 0x18 Add DisguisedChatMessageMessage
        outbound(0x19, EntityStatusMessage.class, EntityStatusCodec.class);
        outbound(0x1A, ExplosionMessage.class, ExplosionCodec.class);
        outbound(0x1B, UnloadChunkMessage.class, UnloadChunkCodec.class);
        outbound(0x1C, StateChangeMessage.class, StateChangeCodec.class);
        // TODO 0x1D : Open Horse Window (opens the horse window, all other inventories use 0x2D)
        outbound(0x1E, WorldBorderMessage.class, WorldBorderCodec.class);
        outbound(0x1F, PingMessage.class, PingCodec.class);
        outbound(0x20, ChunkDataMessage.class, ChunkDataCodec.class);
        outbound(0x21, PlayEffectMessage.class, PlayEffectCodec.class);
        outbound(0x22, PlayParticleMessage.class, PlayParticleCodec.class);
        outbound(0x23, ChunkLightDataMessage.class, ChunkLightDataCodec.class);
        outbound(0x24, JoinGameMessage.class, JoinGameCodec.class);
        outbound(0x25, MapDataMessage.class, MapDataCodec.class);
        // TODO 0x26 : Trade List (the list of trades a villager is offering)
        outbound(0x27, RelativeEntityPositionMessage.class, RelativeEntityPositionCodec.class);
        outbound(0x28, RelativeEntityPositionRotationMessage.class,
            RelativeEntityPositionRotationCodec.class);
        outbound(0x29, EntityRotationMessage.class, EntityRotationCodec.class);
        outbound(0x2A, VehicleMoveMessage.class, VehicleMoveCodec.class);
        // TODO 0x2B : Open Book (this replaces the old plugin channel. tells the client to open the book they have in hand)
        outbound(0x2C, OpenWindowMessage.class, OpenWindowCodec.class);
        outbound(0x2D, SignEditorMessage.class, SignEditorCodec.class);
        // TODO 0x2E Ping (it's different to 0x1E PingMessage)
        outbound(0x2F, CraftRecipeResponseMessage.class, CraftRecipeResponseCodec.class);
        outbound(0x30, PlayerAbilitiesMessage.class, PlayerAbilitiesCodec.class);
        // TODO 0x31 Player Chat Message
        // TODO 0x32 End Combat Event
        // TODO 0x33 Enter Combat Event
        // TODO 0x34 Death Combat Event
        // TODO 0x35 Player Info Remove
        // TODO 0x36 Double check spec
        outbound(0x36, UserListItemMessage.class, UserListItemCodec.class);
        // TODO 0x37 : Face Player (rotates the client to face a location or entity)
        outbound(0x38, PositionRotationMessage.class, PositionRotationCodec.class);
        outbound(0x39, UnlockRecipesMessage.class, UnlockRecipesCodec.class);
        outbound(0x3A, DestroyEntitiesMessage.class, DestroyEntitiesCodec.class);
        outbound(0x3B, EntityRemoveEffectMessage.class, EntityRemoveEffectCodec.class);
        outbound(0x3C, ResourcePackSendMessage.class, ResourcePackSendCodec.class);
        outbound(0x3D, RespawnMessage.class, RespawnCodec.class);
        outbound(0x3E, EntityHeadRotationMessage.class, EntityHeadRotationCodec.class);
        outbound(0x3F, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        // TODO 0x40 : Select Advancement Tab (tells the client to switch tabs)
        // TODO 0x41 : Server Data
        // TODO 0x42 : Action Bar
        // TODO 0x43 : World Border Center
        // TODO 0x44 : World Border Lerp Size
        // TODO 0x45 : World Border Size
        // TODO 0x46 : World Border Warning Delay
        // TODO 0x47 : World Border Warning Reach
        outbound(0x48, CameraMessage.class, CameraCodec.class);
        outbound(0x49, HeldItemMessage.class, HeldItemCodec.class);
        // TODO 0x4A Set Center Chunk
        // TODO 0x4B Set Render Distance
        outbound(0x4C, SpawnPositionMessage.class, SpawnPositionCodec.class);
        // TODO 0x4B : Set Display Chat Preview
        outbound(0x4D, ScoreboardDisplayMessage.class, ScoreboardDisplayCodec.class);
        outbound(0x4E, EntityMetadataMessage.class, EntityMetadataCodec.class);
        outbound(0x4F, AttachEntityMessage.class, AttachEntityCodec.class);
        outbound(0x50, EntityVelocityMessage.class, EntityVelocityCodec.class);
        outbound(0x51, EntityEquipmentMessage.class, EntityEquipmentCodec.class);
        outbound(0x52, ExperienceMessage.class, ExperienceCodec.class);
        outbound(0x53, HealthMessage.class, HealthCodec.class);
        outbound(0x54, ScoreboardObjectiveMessage.class, ScoreboardObjectiveCodec.class);
        outbound(0x55, SetPassengerMessage.class, SetPassengerCodec.class);
        outbound(0x56, ScoreboardTeamMessage.class, ScoreboardTeamCodec.class);
        outbound(0x57, ScoreboardScoreMessage.class, ScoreboardScoreCodec.class);
        // TODO 0x58 : Update Simulation Distance
        // TODO 0x59 : Set Title SubTitle
        outbound(0x5A, TimeMessage.class, TimeCodec.class);
        // TODO 0x5B : Set Title Text       outbound(0x5A, TitleMessage.class, TitleCodec.class);
        // TODO 0x5C : Set Title Times
        // TODO 0x5D : Entity Sound Effect
        outbound(0x5E, SoundEffectMessage.class, SoundEffectCodec.class);
        outbound(0x5F, StopSoundMessage.class, StopSoundCodec.class);
        outbound(0x60, ChatMessage.class, ChatCodec.class);
        outbound(0x61, UserListHeaderFooterMessage.class, UserListHeaderFooterCodec.class);
        // TODO 0x62 : NBT Query Response (response to Query Block/Entity NBT packets)
        outbound(0x63, CollectItemMessage.class, CollectItemCodec.class);
        outbound(0x64, EntityTeleportMessage.class, EntityTeleportCodec.class);
        outbound(0x65, AdvancementsMessage.class, AdvancementsCodec.class);
        outbound(0x66, EntityPropertyMessage.class, EntityPropertyCodec.class);
        // TODO 0x67 : Feature flags
        outbound(0x68, EntityEffectMessage.class, EntityEffectCodec.class);
        // TODO 0x69 : Update recipes
        // TODO 0x6A : Update Tags
    }
}
