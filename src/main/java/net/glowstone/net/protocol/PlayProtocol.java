package net.glowstone.net.protocol;

import net.glowstone.net.codec.KickCodec;
import net.glowstone.net.codec.play.entity.AnimateEntityCodec;
import net.glowstone.net.codec.play.entity.AttachEntityCodec;
import net.glowstone.net.codec.play.entity.CollectItemCodec;
import net.glowstone.net.codec.play.entity.DestroyEntitiesCodec;
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
import net.glowstone.net.codec.play.entity.SpawnLightningStrikeCodec;
import net.glowstone.net.codec.play.entity.SpawnMobCodec;
import net.glowstone.net.codec.play.entity.SpawnObjectCodec;
import net.glowstone.net.codec.play.entity.SpawnPaintingCodec;
import net.glowstone.net.codec.play.entity.SpawnPlayerCodec;
import net.glowstone.net.codec.play.entity.SpawnXpOrbCodec;
import net.glowstone.net.codec.play.entity.VehicleMoveCodec;
import net.glowstone.net.codec.play.game.BlockActionCodec;
import net.glowstone.net.codec.play.game.BlockBreakAnimationCodec;
import net.glowstone.net.codec.play.game.BlockChangeCodec;
import net.glowstone.net.codec.play.game.ChatCodec;
import net.glowstone.net.codec.play.game.ChunkDataCodec;
import net.glowstone.net.codec.play.game.ClientSettingsCodec;
import net.glowstone.net.codec.play.game.CraftRecipeRequestCodec;
import net.glowstone.net.codec.play.game.CraftRecipeResponseCodec;
import net.glowstone.net.codec.play.game.CraftingBookDataCodec;
import net.glowstone.net.codec.play.game.ExperienceCodec;
import net.glowstone.net.codec.play.game.ExplosionCodec;
import net.glowstone.net.codec.play.game.HealthCodec;
import net.glowstone.net.codec.play.game.IncomingChatCodec;
import net.glowstone.net.codec.play.game.JoinGameCodec;
import net.glowstone.net.codec.play.game.MapDataCodec;
import net.glowstone.net.codec.play.game.MultiBlockChangeCodec;
import net.glowstone.net.codec.play.game.NamedSoundEffectCodec;
import net.glowstone.net.codec.play.game.PingCodec;
import net.glowstone.net.codec.play.game.PlayEffectCodec;
import net.glowstone.net.codec.play.game.PlayParticleCodec;
import net.glowstone.net.codec.play.game.PluginMessageCodec;
import net.glowstone.net.codec.play.game.PositionRotationCodec;
import net.glowstone.net.codec.play.game.RespawnCodec;
import net.glowstone.net.codec.play.game.SignEditorCodec;
import net.glowstone.net.codec.play.game.SoundEffectCodec;
import net.glowstone.net.codec.play.game.SpawnPositionCodec;
import net.glowstone.net.codec.play.game.StateChangeCodec;
import net.glowstone.net.codec.play.game.StatisticCodec;
import net.glowstone.net.codec.play.game.TimeCodec;
import net.glowstone.net.codec.play.game.TitleCodec;
import net.glowstone.net.codec.play.game.UnloadChunkCodec;
import net.glowstone.net.codec.play.game.UnlockRecipesCodec;
import net.glowstone.net.codec.play.game.UpdateBlockEntityCodec;
import net.glowstone.net.codec.play.game.UpdateSignCodec;
import net.glowstone.net.codec.play.game.UserListHeaderFooterCodec;
import net.glowstone.net.codec.play.game.UserListItemCodec;
import net.glowstone.net.codec.play.game.WorldBorderCodec;
import net.glowstone.net.codec.play.inv.CloseWindowCodec;
import net.glowstone.net.codec.play.inv.CreativeItemCodec;
import net.glowstone.net.codec.play.inv.EnchantItemCodec;
import net.glowstone.net.codec.play.inv.HeldItemCodec;
import net.glowstone.net.codec.play.inv.OpenWindowCodec;
import net.glowstone.net.codec.play.inv.SetWindowContentsCodec;
import net.glowstone.net.codec.play.inv.SetWindowSlotCodec;
import net.glowstone.net.codec.play.inv.TransactionCodec;
import net.glowstone.net.codec.play.inv.WindowClickCodec;
import net.glowstone.net.codec.play.inv.WindowPropertyCodec;
import net.glowstone.net.codec.play.player.AdvancementTabCodec;
import net.glowstone.net.codec.play.player.AdvancementsCodec;
import net.glowstone.net.codec.play.player.BlockPlacementCodec;
import net.glowstone.net.codec.play.player.BossBarCodec;
import net.glowstone.net.codec.play.player.CameraCodec;
import net.glowstone.net.codec.play.player.ClientStatusCodec;
import net.glowstone.net.codec.play.player.CombatEventCodec;
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
import net.glowstone.net.codec.play.player.UseBedCodec;
import net.glowstone.net.codec.play.player.UseItemCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardDisplayCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardObjectiveCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardScoreCodec;
import net.glowstone.net.codec.play.scoreboard.ScoreboardTeamCodec;
import net.glowstone.net.handler.play.game.ClientSettingsHandler;
import net.glowstone.net.handler.play.game.CraftRecipeRequestHandler;
import net.glowstone.net.handler.play.game.CraftingBookDataHandler;
import net.glowstone.net.handler.play.game.IncomingChatHandler;
import net.glowstone.net.handler.play.game.PingHandler;
import net.glowstone.net.handler.play.game.PluginMessageHandler;
import net.glowstone.net.handler.play.game.UpdateSignHandler;
import net.glowstone.net.handler.play.inv.CloseWindowHandler;
import net.glowstone.net.handler.play.inv.CreativeItemHandler;
import net.glowstone.net.handler.play.inv.EnchantItemHandler;
import net.glowstone.net.handler.play.inv.HeldItemHandler;
import net.glowstone.net.handler.play.inv.TransactionHandler;
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
import net.glowstone.net.message.play.entity.AnimateEntityMessage;
import net.glowstone.net.message.play.entity.AttachEntityMessage;
import net.glowstone.net.message.play.entity.CollectItemMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
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
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;
import net.glowstone.net.message.play.entity.SpawnMobMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import net.glowstone.net.message.play.entity.SpawnPlayerMessage;
import net.glowstone.net.message.play.entity.SpawnXpOrbMessage;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;
import net.glowstone.net.message.play.game.BlockActionMessage;
import net.glowstone.net.message.play.game.BlockBreakAnimationMessage;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import net.glowstone.net.message.play.game.ChatMessage;
import net.glowstone.net.message.play.game.ChunkDataMessage;
import net.glowstone.net.message.play.game.ClientSettingsMessage;
import net.glowstone.net.message.play.game.CraftRecipeRequestMessage;
import net.glowstone.net.message.play.game.CraftRecipeResponseMessage;
import net.glowstone.net.message.play.game.CraftingBookDataMessage;
import net.glowstone.net.message.play.game.ExperienceMessage;
import net.glowstone.net.message.play.game.ExplosionMessage;
import net.glowstone.net.message.play.game.HealthMessage;
import net.glowstone.net.message.play.game.IncomingChatMessage;
import net.glowstone.net.message.play.game.JoinGameMessage;
import net.glowstone.net.message.play.game.MapDataMessage;
import net.glowstone.net.message.play.game.MultiBlockChangeMessage;
import net.glowstone.net.message.play.game.NamedSoundEffectMessage;
import net.glowstone.net.message.play.game.PingMessage;
import net.glowstone.net.message.play.game.PlayEffectMessage;
import net.glowstone.net.message.play.game.PlayParticleMessage;
import net.glowstone.net.message.play.game.PluginMessage;
import net.glowstone.net.message.play.game.PositionRotationMessage;
import net.glowstone.net.message.play.game.RespawnMessage;
import net.glowstone.net.message.play.game.SignEditorMessage;
import net.glowstone.net.message.play.game.SoundEffectMessage;
import net.glowstone.net.message.play.game.SpawnPositionMessage;
import net.glowstone.net.message.play.game.StateChangeMessage;
import net.glowstone.net.message.play.game.StatisticMessage;
import net.glowstone.net.message.play.game.TimeMessage;
import net.glowstone.net.message.play.game.TitleMessage;
import net.glowstone.net.message.play.game.UnloadChunkMessage;
import net.glowstone.net.message.play.game.UnlockRecipesMessage;
import net.glowstone.net.message.play.game.UpdateBlockEntityMessage;
import net.glowstone.net.message.play.game.UpdateSignMessage;
import net.glowstone.net.message.play.game.UserListHeaderFooterMessage;
import net.glowstone.net.message.play.game.UserListItemMessage;
import net.glowstone.net.message.play.game.WorldBorderMessage;
import net.glowstone.net.message.play.inv.CloseWindowMessage;
import net.glowstone.net.message.play.inv.CreativeItemMessage;
import net.glowstone.net.message.play.inv.EnchantItemMessage;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import net.glowstone.net.message.play.inv.OpenWindowMessage;
import net.glowstone.net.message.play.inv.SetWindowContentsMessage;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;
import net.glowstone.net.message.play.inv.TransactionMessage;
import net.glowstone.net.message.play.inv.WindowClickMessage;
import net.glowstone.net.message.play.inv.WindowPropertyMessage;
import net.glowstone.net.message.play.player.AdvancementTabMessage;
import net.glowstone.net.message.play.player.AdvancementsMessage;
import net.glowstone.net.message.play.player.BlockPlacementMessage;
import net.glowstone.net.message.play.player.BossBarMessage;
import net.glowstone.net.message.play.player.CameraMessage;
import net.glowstone.net.message.play.player.ClientStatusMessage;
import net.glowstone.net.message.play.player.CombatEventMessage;
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
import net.glowstone.net.message.play.player.UseBedMessage;
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
        super("PLAY", 0x4F);

        inbound(0x00, TeleportConfirmMessage.class, TeleportConfirmCodec.class,
            TeleportConfirmHandler.class);
        // TODO 0x01 : Query Block NBT packet
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
        // TODO 0x0B : Edit Book packet
        // TODO 0x0C : Query Entity NBT packet
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
        // TODO 0x15 : Pick Item packet
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
        // TODO 0x1C : Name Item packet
        inbound(0x1D, ResourcePackStatusMessage.class, ResourcePackStatusCodec.class,
            ResourcePackStatusHandler.class);
        inbound(0x1E, AdvancementTabMessage.class, AdvancementTabCodec.class,
            AdvancementTabHandler.class);
        // TODO 0x1F : Select Trade packet
        // TODO 0x20 : Set Beacon Effect packet
        inbound(0x21, HeldItemMessage.class, HeldItemCodec.class, HeldItemHandler.class);
        // TODO 0x22 : Update Command Block packet
        // TODO 0x23 : Update Command Block Minecart packet
        inbound(0x24, CreativeItemMessage.class, CreativeItemCodec.class,
            CreativeItemHandler.class);
        // TODO 0x25 : Update Structure Block packet
        inbound(0x26, UpdateSignMessage.class, UpdateSignCodec.class, UpdateSignHandler.class);
        inbound(0x27, PlayerSwingArmMessage.class, PlayerSwingArmCodec.class,
            PlayerSwingArmHandler.class);
        inbound(0x28, SpectateMessage.class, SpectateCodec.class, SpectateHandler.class);
        inbound(0x29, BlockPlacementMessage.class, BlockPlacementCodec.class,
            BlockPlacementHandler.class);
        inbound(0x2A, UseItemMessage.class, UseItemCodec.class, UseItemHandler.class);

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
        outbound(0x0E, ChatMessage.class, ChatCodec.class);
        outbound(0x0F, MultiBlockChangeMessage.class, MultiBlockChangeCodec.class);
        outbound(0x10, TabCompleteResponseMessage.class, TabCompleteResponseCodec.class);
        // TODO 0x11 : Declare Commands packet
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
        // TODO 0x1D NBT Query Response packet
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
