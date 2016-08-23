package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.GlowServer;
import net.glowstone.net.handler.play.player.UseItemPacket;
import net.glowstone.net.message.KickPacket;
import net.glowstone.net.message.SetCompressionMessage;
import net.glowstone.net.message.handshake.HandshakeMessage;
import net.glowstone.net.message.login.EncryptionKeyRequestMessage;
import net.glowstone.net.message.login.EncryptionKeyResponseMessage;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.*;
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayPacket;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectivePacket;
import net.glowstone.net.message.play.scoreboard.ScoreboardScorePacket;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamPacket;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.net.protocol.ProtocolType;

import java.util.HashSet;
import java.util.Set;

import static net.glowstone.net.api.GlowPacket.Destination.IN;
import static net.glowstone.net.api.GlowPacket.Destination.OUT;
import static net.glowstone.net.protocol.ProtocolType.*;

public class GlowPacket<M extends Message> {

    public abstract static class Handshake {
        public static class In {
            public static final GlowPacket<HandshakeMessage> Handshake = new GlowPacket<>(0x00, HANDSHAKE, HandshakeMessage.class, IN);
        }

        public static class Out {
        }
    }

    public abstract static class Status {
        public static class In {
            public static final GlowPacket<StatusRequestMessage> Request = new GlowPacket<>(0x00, STATUS, StatusRequestMessage.class, IN);
            public static final GlowPacket<StatusPingMessage> Ping = new GlowPacket<>(0x01, STATUS, StatusPingMessage.class, IN);
        }

        public static class Out {
            public static final GlowPacket<StatusResponseMessage> Response = new GlowPacket<>(0x00, STATUS, StatusResponseMessage.class, OUT);
            public static final GlowPacket<StatusPingMessage> Pong = new GlowPacket<>(0x01, STATUS, StatusPingMessage.class, OUT);
        }
    }

    public abstract static class Login {
        public static class In {
            public static final GlowPacket<LoginStartMessage> Start = new GlowPacket<>(0x00, LOGIN, LoginStartMessage.class, IN);
            public static final GlowPacket<EncryptionKeyResponseMessage> Encrypt = new GlowPacket<>(0x01, LOGIN, EncryptionKeyResponseMessage.class, IN);
        }

        public static class Out {
            public static final GlowPacket<KickPacket> Kick = new GlowPacket<>(0x00, LOGIN, KickPacket.class, OUT);
            public static final GlowPacket<EncryptionKeyRequestMessage> Encrypt = new GlowPacket<>(0x01, LOGIN, EncryptionKeyRequestMessage.class, OUT);
            public static final GlowPacket<LoginSuccessMessage> Success = new GlowPacket<>(0x02, LOGIN, LoginSuccessMessage.class, OUT);
            public static final GlowPacket<SetCompressionMessage> Compress = new GlowPacket<>(0x03, LOGIN, SetCompressionMessage.class, OUT);
        }
    }

    public abstract static class Play {
        public static class In {
            public static final GlowPacket<TeleportConfirmPacket> Teleport = new GlowPacket<>(0x00, PLAY, TeleportConfirmPacket.class, IN);
            public static final GlowPacket<TabCompletePacket> TabComplete = new GlowPacket<>(0x01, PLAY, TabCompletePacket.class, IN);
            public static final GlowPacket<InboundChatPacket> Chat = new GlowPacket<>(0x02, PLAY, InboundChatPacket.class, IN);
            public static final GlowPacket<ClientStatusPacket> ClientStatus = new GlowPacket<>(0x03, PLAY, ClientStatusPacket.class, IN);
            public static final GlowPacket<ClientSettingsPacket> ClientSettings = new GlowPacket<>(0x04, PLAY, ClientSettingsPacket.class, IN);
            public static final GlowPacket<TransactionPacket> Transaction = new GlowPacket<>(0x05, PLAY, TransactionPacket.class, IN);
            public static final GlowPacket<EnchantItemPacket> Enchant = new GlowPacket<>(0x06, PLAY, EnchantItemPacket.class, IN);
            public static final GlowPacket<WindowClickPacket> WindowClick = new GlowPacket<>(0x07, PLAY, WindowClickPacket.class, IN);
            public static final GlowPacket<WindowClosePacket> WindowClose = new GlowPacket<>(0x08, PLAY, WindowClosePacket.class, IN);
            public static final GlowPacket<PluginLoadPacket> PluginLoad = new GlowPacket<>(0x09, PLAY, PluginLoadPacket.class, IN);
            public static final GlowPacket<InteractEntityPacket> InteractEntity = new GlowPacket<>(0x0A, PLAY, InteractEntityPacket.class, IN);
            public static final GlowPacket<PingPacket> Ping = new GlowPacket<>(0x0B, PLAY, PingPacket.class, IN);
            public static final GlowPacket<PlayerPositionPacket> PlayerPosition = new GlowPacket<>(0x0C, PLAY, PlayerPositionPacket.class, IN);
            public static final GlowPacket<PlayerPositionLookPacket> PlayerPositionLook = new GlowPacket<>(0x0D, PLAY, PlayerPositionLookPacket.class, IN);
            public static final GlowPacket<PlayerLookPacket> PlayerLook = new GlowPacket<>(0x0E, PLAY, PlayerLookPacket.class, IN);
            public static final GlowPacket<PlayerUpdatePacket> PlayerUpdate = new GlowPacket<>(0x0F, PLAY, PlayerUpdatePacket.class, IN);
            public static final GlowPacket<VehicleMovePacket> VehicleMove = new GlowPacket<>(0x10, PLAY, VehicleMovePacket.class, IN);
            @Deprecated
            public static final GlowPacket SteerBoat = new GlowPacket<>(0x11, PLAY, null, IN);
            public static final GlowPacket<PlayerAbilitiesPacket> PlayerAbilities = new GlowPacket<>(0x12, PLAY, PlayerAbilitiesPacket.class, IN);
            public static final GlowPacket<BlockDigPacket> BlockDig = new GlowPacket<>(0x13, PLAY, BlockDigPacket.class, IN);
            public static final GlowPacket<PlayerActionPacket> PlayerAction = new GlowPacket<>(0x14, PLAY, PlayerActionPacket.class, IN);
            public static final GlowPacket<SteerVehiclePacket> SteerVehicle = new GlowPacket<>(0x15, PLAY, SteerVehiclePacket.class, IN);
            public static final GlowPacket<ResourcePackStatusPacket> ResourcePackStatus = new GlowPacket<>(0x16, PLAY, ResourcePackStatusPacket.class, IN);
            public static final GlowPacket<HeldItemPacket> HeldItem = new GlowPacket<>(0x17, PLAY, HeldItemPacket.class, IN);
            public static final GlowPacket<CreativeItemPacket> CreativeItem = new GlowPacket<>(0x18, PLAY, CreativeItemPacket.class, IN);
            public static final GlowPacket<UpdateSignPacket> UpdateSign = new GlowPacket<>(0x19, PLAY, UpdateSignPacket.class, IN);
            public static final GlowPacket<PlayerSwingArmPacket> SwingArm = new GlowPacket<>(0x1A, PLAY, PlayerSwingArmPacket.class, IN);
            public static final GlowPacket<SpectatePacket> Spectate = new GlowPacket<>(0x1B, PLAY, SpectatePacket.class, IN);
            public static final GlowPacket<BlockPlacePacket> BlockPlace = new GlowPacket<>(0x1C, PLAY, BlockPlacePacket.class, IN);
            public static final GlowPacket<UseItemPacket> UseItem = new GlowPacket<>(0x1D, PLAY, UseItemPacket.class, IN);
        }

        public static class Out {
            public static final GlowPacket<SpawnObjectPacket> SpawnObject = new GlowPacket<>(0x00, PLAY, SpawnObjectPacket.class, OUT);
            public static final GlowPacket<SpawnXpOrbPacket> SpawnXP = new GlowPacket<>(0x01, PLAY, SpawnXpOrbPacket.class, OUT);
            public static final GlowPacket<SpawnLightningPacket> SpawnLightning = new GlowPacket<>(0x02, PLAY, SpawnLightningPacket.class, OUT);
            public static final GlowPacket<SpawnMobPacket> SpawnMob = new GlowPacket<>(0x03, PLAY, SpawnMobPacket.class, OUT);
            public static final GlowPacket<SpawnPaintingPacket> SpawnPainting = new GlowPacket<>(0x04, PLAY, SpawnPaintingPacket.class, OUT);
            public static final GlowPacket<SpawnPlayerPacket> SpawnPlayer = new GlowPacket<>(0x05, PLAY, SpawnPlayerPacket.class, OUT);
            public static final GlowPacket<EntityAnimationPacket> EntityAnimation = new GlowPacket<>(0x06, PLAY, EntityAnimationPacket.class, OUT);
            public static final GlowPacket<StatisticPacket> Statistic = new GlowPacket<>(0x07, PLAY, StatisticPacket.class, OUT);
            @Deprecated
            public static final GlowPacket BlockDig = new GlowPacket<>(0x08, PLAY, null, OUT);
            public static final GlowPacket<UpdateBlockEntityPacket> UpdateBlockEntity = new GlowPacket<>(0x09, PLAY, UpdateBlockEntityPacket.class, OUT);
            public static final GlowPacket<BlockActionPacket> BlockAction = new GlowPacket<>(0x0A, PLAY, BlockActionPacket.class, OUT);
            public static final GlowPacket<BlockChangePacket> BlockChange = new GlowPacket<>(0x0B, PLAY, BlockChangePacket.class, OUT);
            public static final GlowPacket<BossBarPacket> BossBar = new GlowPacket<>(0x0C, PLAY, BossBarPacket.class, OUT);
            public static final GlowPacket<ServerDifficultyPacket> ServerDifficulty = new GlowPacket<>(0x0D, PLAY, ServerDifficultyPacket.class, OUT);
            public static final GlowPacket<TabCompleteResponsePacket> TabComplete = new GlowPacket<>(0x0E, PLAY, TabCompleteResponsePacket.class, OUT);
            public static final GlowPacket<OutboundChatPacket> Chat = new GlowPacket<>(0x0F, PLAY, OutboundChatPacket.class, OUT);
            public static final GlowPacket<MultiBlockChangePacket> MultiBlockChange = new GlowPacket<>(0x10, PLAY, MultiBlockChangePacket.class, OUT);
            public static final GlowPacket<TransactionPacket> Transaction = new GlowPacket<>(0x11, PLAY, TransactionPacket.class, OUT);
            public static final GlowPacket<WindowClosePacket> WindowClose = new GlowPacket<>(0x12, PLAY, WindowClosePacket.class, OUT);
            public static final GlowPacket<WindowOpenPacket> WindowOpen = new GlowPacket<>(0x13, PLAY, WindowOpenPacket.class, OUT);
            public static final GlowPacket<WindowContentPacket> WindowContent = new GlowPacket<>(0x14, PLAY, WindowContentPacket.class, OUT);
            public static final GlowPacket<WindowPropertyPacket> WindowProperty = new GlowPacket<>(0x15, PLAY, WindowPropertyPacket.class, OUT);
            public static final GlowPacket<WindowSlotPacket> WindowSlot = new GlowPacket<>(0x16, PLAY, WindowSlotPacket.class, OUT);
            public static final GlowPacket<CooldownPacket> Cooldown = new GlowPacket<>(0x17, PLAY, CooldownPacket.class, OUT);
            public static final GlowPacket<PluginLoadPacket> PluginLoad = new GlowPacket<>(0x18, PLAY, PluginLoadPacket.class, OUT);
            public static final GlowPacket<NamedSoundEffectPacket> NamedSoundEffect = new GlowPacket<>(0x19, PLAY, NamedSoundEffectPacket.class, OUT);
            public static final GlowPacket<KickPacket> Kick = new GlowPacket<>(0x1A, PLAY, KickPacket.class, OUT);
            public static final GlowPacket<EntityStatusPacket> EntityStatus = new GlowPacket<>(0x1B, PLAY, EntityStatusPacket.class, OUT);
            public static final GlowPacket<ExplosionPacket> Explosion = new GlowPacket<>(0x1C, PLAY, ExplosionPacket.class, OUT);
            public static final GlowPacket<UnloadChunkPacket> UnloadChunk = new GlowPacket<>(0x1D, PLAY, UnloadChunkPacket.class, OUT);
            public static final GlowPacket<StateChangePacket> StateChange = new GlowPacket<>(0x1E, PLAY, StateChangePacket.class, OUT);
            public static final GlowPacket<PingPacket> Pong = new GlowPacket<>(0x1F, PLAY, PingPacket.class, OUT);
            public static final GlowPacket<ChunkDataPacket> ChunkData = new GlowPacket<>(0x20, PLAY, ChunkDataPacket.class, OUT);
            public static final GlowPacket<PlayEffectPacket> PlayEffect = new GlowPacket<>(0x21, PLAY, PlayEffectPacket.class, OUT);
            public static final GlowPacket<PlayParticlePacket> PlayParticle = new GlowPacket<>(0x22, PLAY, PlayParticlePacket.class, OUT);
            public static final GlowPacket<JoinGamePacket> JoinGame = new GlowPacket<>(0x23, PLAY, JoinGamePacket.class, OUT);
            public static final GlowPacket<MapDataPacket> MapData = new GlowPacket<>(0x24, PLAY, MapDataPacket.class, OUT);
            public static final GlowPacket<RelativeEntityPositionPacket> EntityPosition = new GlowPacket<>(0x25, PLAY, RelativeEntityPositionPacket.class, OUT);
            public static final GlowPacket<RelativeEntityPositionRotationPacket> EntityPositionRotation = new GlowPacket<>(0x26, PLAY, RelativeEntityPositionRotationPacket.class, OUT);
            public static final GlowPacket<EntityRotationPacket> EntityRotation = new GlowPacket<>(0x27, PLAY, EntityRotationPacket.class, OUT);
            @Deprecated
            public static final GlowPacket Entity = new GlowPacket<>(0x28, PLAY, null, OUT);
            public static final GlowPacket<VehicleMovePacket> VehicleMove = new GlowPacket<>(0x29, PLAY, VehicleMovePacket.class, OUT);
            public static final GlowPacket<SignEditorPacket> SignEditor = new GlowPacket<>(0x2A, PLAY, SignEditorPacket.class, OUT);
            public static final GlowPacket<PlayerAbilitiesPacket> PlayerAbilities = new GlowPacket<>(0x2B, PLAY, PlayerAbilitiesPacket.class, OUT);
            public static final GlowPacket<CombatPacket> Combat = new GlowPacket<>(0x2C, PLAY, CombatPacket.class, OUT);
            public static final GlowPacket<PlayerListItemPacket> PlayerListItem = new GlowPacket<>(0x2D, PLAY, PlayerListItemPacket.class, OUT);
            public static final GlowPacket<PositionRotationPacket> PositionRotation = new GlowPacket<>(0x2E, PLAY, PositionRotationPacket.class, OUT);
            public static final GlowPacket<UseBedPacket> UseBed = new GlowPacket<>(0x2F, PLAY, UseBedPacket.class, OUT);
            public static final GlowPacket<DestroyEntitiesPacket> DestroyEntities = new GlowPacket<>(0x30, PLAY, DestroyEntitiesPacket.class, OUT);
            public static final GlowPacket<EntityRemoveEffectPacket> EntityRemoveEffect = new GlowPacket<>(0x31, PLAY, EntityRemoveEffectPacket.class, OUT);
            public static final GlowPacket<ResourcePackSendPacket> ResourcePack = new GlowPacket<>(0x32, PLAY, ResourcePackSendPacket.class, OUT);
            public static final GlowPacket<RespawnPacket> Respawn = new GlowPacket<>(0x33, PLAY, RespawnPacket.class, OUT);
            public static final GlowPacket<EntityHeadRotationPacket> EntityHeadRotation = new GlowPacket<>(0x34, PLAY, EntityHeadRotationPacket.class, OUT);
            public static final GlowPacket<WorldBorderPacket> WorldBorder = new GlowPacket<>(0x35, PLAY, WorldBorderPacket.class, OUT);
            public static final GlowPacket<CameraPacket> Camera = new GlowPacket<>(0x36, PLAY, CameraPacket.class, OUT);
            public static final GlowPacket<HeldItemPacket> HeldItem = new GlowPacket<>(0x37, PLAY, HeldItemPacket.class, OUT);
            public static final GlowPacket<ScoreboardDisplayPacket> ScoreboardDisplay = new GlowPacket<>(0x38, PLAY, ScoreboardDisplayPacket.class, OUT);
            public static final GlowPacket<EntityMetadataPacket> EntityMetadata = new GlowPacket<>(0x39, PLAY, EntityMetadataPacket.class, OUT);
            public static final GlowPacket<AttachEntityPacket> AttachEntity = new GlowPacket<>(0x3A, PLAY, AttachEntityPacket.class, OUT);
            public static final GlowPacket<EntityVelocityPacket> EntityVelocity = new GlowPacket<>(0x3B, PLAY, EntityVelocityPacket.class, OUT);
            public static final GlowPacket<EntityEquipmentPacket> EntityEquipment = new GlowPacket<>(0x3C, PLAY, EntityEquipmentPacket.class, OUT);
            public static final GlowPacket<ExperiencePacket> Experience = new GlowPacket<>(0x3D, PLAY, ExperiencePacket.class, OUT);
            public static final GlowPacket<HealthPacket> Health = new GlowPacket<>(0x3E, PLAY, HealthPacket.class, OUT);
            public static final GlowPacket<ScoreboardObjectivePacket> ScoreboardObjective = new GlowPacket<>(0x3F, PLAY, ScoreboardObjectivePacket.class, OUT);
            public static final GlowPacket<PassengerPacket> Passenger = new GlowPacket<>(0x40, PLAY, PassengerPacket.class, OUT);
            public static final GlowPacket<ScoreboardTeamPacket> ScoreboardTeam = new GlowPacket<>(0x41, PLAY, ScoreboardTeamPacket.class, OUT);
            public static final GlowPacket<ScoreboardScorePacket> ScoreboardScore = new GlowPacket<>(0x42, PLAY, ScoreboardScorePacket.class, OUT);
            public static final GlowPacket<SpawnPositionPacket> SpawnPosition = new GlowPacket<>(0x43, PLAY, SpawnPositionPacket.class, OUT);
            public static final GlowPacket<TimePacket> Time = new GlowPacket<>(0x44, PLAY, TimePacket.class, OUT);
            public static final GlowPacket<TitlePacket> Title = new GlowPacket<>(0x45, PLAY, TitlePacket.class, OUT);
            public static final GlowPacket<SoundEffectPacket> SoundEffect = new GlowPacket<>(0x46, PLAY, SoundEffectPacket.class, OUT);
            public static final GlowPacket<PlayerListHeaderFooterPacket> PlayerListHeader = new GlowPacket<>(0x47, PLAY, PlayerListHeaderFooterPacket.class, OUT);
            public static final GlowPacket<CollectItemPacket> CollectItem = new GlowPacket<>(0x48, PLAY, CollectItemPacket.class, OUT);
            public static final GlowPacket<EntityTeleportPacket> EntityTeleport = new GlowPacket<>(0x49, PLAY, EntityTeleportPacket.class, OUT);
            public static final GlowPacket<EntityPropertyPacket> EntityProperty = new GlowPacket<>(0x4A, PLAY, EntityPropertyPacket.class, OUT);
            public static final GlowPacket<EntityEffectPacket> EntityEffect = new GlowPacket<>(0x4B, PLAY, EntityEffectPacket.class, OUT);
        }
    }

/*
    IN_HANDSHAKE(0x00, HANDSHAKE, HandshakeMessage.class),

    IN_STATUS_REQUEST(0x00, STATUS, StatusRequestMessage.class),
    IN_STATUS_PING(0x01, STATUS, StatusPingMessage.class),
    OUT_STATUS_RESPONSE(0x00, STATUS, StatusResponseMessage.class),
    OUT_STATUS_PONG(0x01, STATUS, StatusPingMessage.class),

    IN_LOGIN_START(0x00, LOGIN, LoginStartMessage.class),
    IN_ENCRYPT_RESPONSE(0x01, LOGIN, EncryptionKeyResponseMessage.class),
    OUT_SESSION_KICK(0x00, LOGIN, KickPacket.class),
    OUT_ENCRYPT_REQUEST(0x01, LOGIN, EncryptionKeyRequestMessage.class),
    OUT_LOGIN_SUCCESS(0x02, LOGIN, LoginSuccessMessage.class),
    OUT_COMPRESSION(0x03, LOGIN, SetCompressionMessage.class),

    IN_TELEPORT_CONFIRM(0x00, PLAY, TeleportConfirmPacket.class),
    IN_TAB_COMPLETE(0x01, PLAY, TabCompletePacket.class),
    IN_CHAT(0x02, PLAY, InboundChatPacket.class),
    IN_CLIENT_STATUS(0x03, PLAY, ClientStatusPacket.class),
    IN_CLIENT_SETTINGS(0x04, PLAY, ClientSettingsPacket.class),
    IN_TRANSACTION(0x05, PLAY, TransactionPacket.class),
    IN_ENCHANT_ITEM(0x06, PLAY, EnchantItemPacket.class),
    IN_WINDOW_CLICK(0x07, PLAY, WindowClickPacket.class),
    IN_WINDOW_CLOSE(0x08, PLAY, WindowClickPacket.class),
    IN_PLUGIN_LOAD(0x09, PLAY, PluginLoadPacket.class),
    IN_INTERACT_ENTITY(0x0A, PLAY, InteractEntityPacket.class),
    IN_PING(0x0B, PLAY, PingPacket.class),
    IN_PLAYER_POSITION(0x0C, PLAY, PlayerPositionPacket.class),
    IN_PLAYER_POSITION_LOOK(0x0D, PLAY, PlayerPositionLookPacket.class),
    IN_PLAYER_LOOK(0x0E, PLAY, PlayerLookPacket.class),
    IN_PLAYER_UPDATE(0x0F, PLAY, PlayerUpdatePacket.class),
    IN_VEHICLE_MOVE(0x10, PLAY, VehicleMovePacket.class),
    //TODO: 0x11 IN_STEER_BOAT
    IN_PLAYER_ABILITIES(0x12, PLAY, PlayerAbilitiesPacket.class),
    IN_BLOCK_DIG(0x13, PLAY, BlockDigPacket.class),
    IN_PLAYER_ACTION(0x14, PLAY, PlayerActionPacket.class),
    IN_STEER_VEHICLE(0x15, PLAY, SteerVehiclePacket.class),
    IN_RESOURCEPACK_STATUS(0x16, PLAY, ResourcePackStatusPacket.class),
    IN_HELD_ITEM(0x17, PLAY, HeldItemPacket.class),
    IN_CREATIVE_ITEM(0x18, PLAY, CreativeItemPacket.class),
    IN_UPDATE_SIGN(0x19, PLAY, UpdateSignPacket.class),
    IN_SWING_ARM(0x1A, PLAY, PlayerSwingArmPacket.class),
    IN_SPECTATE(0x1B, PLAY, SpectatePacket.class),
    IN_BLOCK_PLACE(0x1C, PLAY, BlockPlacePacket.class),
    IN_USE_ITEM(0x1D, PLAY, UseItemPacket.class),
    OUT_SPAWN_OBJECT(0x00, PLAY, SpawnObjectPacket.class),
    OUT_SPAWN_XP(0x01, PLAY, SpawnXpOrbPacket.class),
    OUT_SPAWN_LIGHTNING(0x02, PLAY, SpawnLightningPacket.class),
    OUT_SPAWN_MOB(0x03, PLAY, SpawnMobPacket.class),
    OUT_SPAWN_PAINTING(0x04, PLAY, SpawnPaintingPacket.class),
    OUT_SPAWN_PLAYER(0x05, PLAY, SpawnPlayerPacket.class),
    OUT_ANIMATE_ENTITY(0x06, PLAY, EntityAnimationPacket.class),
    OUT_STATISTIC(0x07, PLAY, StatisticPacket.class),
    //TODO: 0x08 OUT_BLOCK_DIG
    OUT_UPDATE_BLOCKENTITY(0x09, PLAY, UpdateBlockEntityPacket.class),
    OUT_BLOCK_ACTION(0x0A, PLAY, BlockActionPacket.class),
    OUT_BLOCK_CHANGE(0x0B, PLAY, BlockChangePacket.class),
    OUT_BOSS_BAR(0x0C, PLAY, BossBarPacket.class),
    OUT_DIFFICULTY(0x0D, PLAY, ServerDifficultyPacket.class),
    OUT_TAB_COMPLETE(0x0E, PLAY, TabCompleteResponsePacket.class),
    OUT_CHAT(0x0F, PLAY, OutboundChatPacket.class),
    OUT_MULTI_BLOCK_CHANGE(0x10, PLAY, MultiBlockChangePacket.class),
    OUT_TRANSACTION(0x11, PLAY, TransactionPacket.class),
    OUT_WINDOW_CLOSE(0x12, PLAY, WindowClosePacket.class),
    OUT_WINDOW_OPEN(0x13, PLAY, WindowOpenPacket.class),
    OUT_WINDOW_CONTENT(0x14, PLAY, WindowContentPacket.class),
    OUT_WINDOW_PROPERTY(0x15, PLAY, WindowPropertyPacket.class),
    OUT_WINDOW_SLOT(0x16, PLAY, WindowSlotPacket.class),
    OUT_COOLDOWN(0x17, PLAY, CooldownPacket.class),
    OUT_PLUGIN_LOAD(0x18, PLAY, PluginLoadPacket.class),
    OUT_NAMED_SOUND(0x19, PLAY, NamedSoundEffectPacket.class),
    OUT_KICK(0x1A, PLAY, KickPacket.class),
    OUT_ENTITY_STATUS(0x1B, PLAY, EntityStatusPacket.class),
    OUT_EXPLOSION(0x1C, PLAY, ExplosionPacket.class),
    OUT_CHUNK_UNLOAD(0x1D, PLAY, UnloadChunkPacket.class),
    OUT_STATE_CHANGE(0x1E, PLAY, StateChangePacket.class),
    OUT_PONG(0x1F, PLAY, PingPacket.class),
    OUT_CHUNK_DATA(0x20, PLAY, ChunkDataPacket.class),
    OUT_EFFECT(0x21, PLAY, PlayEffectPacket.class),
    OUT_PARTICLE(0x22, PLAY, PlayParticlePacket.class),
    OUT_JOIN(0x23, PLAY, JoinGamePacket.class),
    OUT_MAP(0x24, PLAY, MapDataPacket.class),
    OUT_RELATIVE_ENTITY_POSITION(0x25, PLAY, RelativeEntityPositionPacket.class),
    OUT_RELATIVE_ENTITY_POSITION_ROTATION(0x26, PLAY, RelativeEntityPositionRotationPacket.class),
    OUT_ENTITY_ROTATION(0x27, PLAY, EntityRotationPacket.class),
    //TODO: 0x28 OUT_ENTITY
    OUT_VEHICLE_MOVE(0x29, PLAY, VehicleMovePacket.class),
    OUT_SIGN_EDIT(0x2A, PLAY, SignEditorPacket.class),
    OUT_PLAYER_ABILITIES(0x2B, PLAY, PlayerAbilitiesPacket.class),
    OUT_COMBAT(0x2C, PLAY, CombatEventMessage.class),
    OUT_PLAYER_LIST(0x2D, PLAY, UserListItemMessage.class),
    OUT_POSITION_ROTATION(0x2E, PLAY, PositionRotationMessage.class),
    OUT_BED(0x2F, PLAY, UseBedMessage.class),
    OUT_DESTROY_ENTITIES(0x30, PLAY, DestroyEntitiesMessage.class),
    OUT_ENTITY_REMOVE_EFFECT(0x31, PLAY, EntityRemoveEffectMessage.class),
    OUT_RESOURCEPACK_SEND(0x32, PLAY, ResourcePackSendMessage.class),
    OUT_RESPAWN(0x33, PLAY, RespawnMessage.class),
    OUT_ENTITY_HEADROTATION(0x34, PLAY, EntityHeadRotationMessage.class),
    OUT_WORLD_BORDER(0x35, PLAY, WorldBorderMessage.class),
    OUT_CAMERA(0x36, PLAY, CameraMessage.class),
    OUT_HELD_ITEM(0x37, PLAY, HeldItemPacket.class),
    OUT_SCOREBOARD_DISPLAY(0x38, PLAY, ScoreboardDisplayMessage.class),
    OUT_ENTITY_METADATA(0x39, PLAY, EntityMetadataMessage.class),
    OUT_ENTITY_ATTACH(0x3A, PLAY, AttachEntityMessage.class),
    OUT_ENTITY_VELOCITY(0x3B, PLAY, EntityVelocityMessage.class),
    OUT_ENTITY_EQUIPMENT(0x3C, PLAY, EntityEquipmentMessage.class),
    OUT_EXPERIENCE(0x3D, PLAY, ExperienceMessage.class),
    OUT_HEALTH(0x3E, PLAY, HealthMessage.class),
    OUT_SCOREBOARD_OBJECTIVE(0x3F, PLAY, ScoreboardObjectiveMessage.class),
    OUT_PASSENGER(0x40, PLAY, SetPassengerMessage.class),
    OUT_SCOREBOARD_TEAM(0x41, PLAY, ScoreboardTeamMessage.class),
    OUT_SCOREBOARD_SCORE(0x42, PLAY, ScoreboardScoreMessage.class),
    OUT_SPAWN_POSITION(0x43, PLAY, SpawnPositionMessage.class),
    OUT_TIME(0x44, PLAY, TimeMessage.class),
    OUT_TITLE(0x45, PLAY, TitleMessage.class),
    OUT_SOUND_EFFECT(0x46, PLAY, SoundEffectMessage.class),
    OUT_PLAYER_LIST_HEADER_FOOTER(0x47, PLAY, UserListHeaderFooterMessage.class),
    OUT_COLLECT_ITEM(0x48, PLAY, CollectItemMessage.class),
    OUT_ENTITY_TELEPORT(0x49, PLAY, EntityTeleportMessage.class),
    OUT_ENTITY_PROPERTY(0x4A, PLAY, EntityPropertyMessage.class),
    OUT_ENTITY_EFFECT(0x4B, PLAY, EntityEffectMessage.class);
*/;
    private final int id;
    private final ProtocolType protocol;
    private final Class<M> packetClass;
    private Destination destination;

    private static Set<GlowPacket> packets = new HashSet<>();
    private static boolean init = false;

    GlowPacket(int id, ProtocolType protocol, Class<M> packetClass, Destination destination) {
        this.id = id;
        this.protocol = protocol;
        this.packetClass = packetClass;
        this.destination = destination;
        packets.add(this);
    }

    public Destination getDestination() {
        return destination;
    }

    public int getId() {
        return id;
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public Class<? extends Message> getPacketClass() {
        return packetClass;
    }

    public static GlowPacket getPacket(int id, ProtocolType protocol, Destination destination) {
        for (GlowPacket packet : packets) {
            if (packet.getId() == id && packet.getProtocol() == protocol && packet.getDestination() == destination) {
                return packet;
            }
        }
        return null;
    }

    public static GlowPacket getPacket(Class<? extends Message> clazz, Destination destination) {
        if (destination == null || clazz == null) {
            return null;
        }
        for (GlowPacket packet : packets) {
            if (packet.getPacketClass() == clazz && packet.getDestination() == destination) {
                return packet;
            }
        }
        GlowServer.logger.warning("Could not find a corresponding packet for " + clazz.getName() + ":" + destination.name() + " (" + packets.size() + " registered)");
        return null;
    }

    public M get(Message m) {
        return (M) m;
    }

    public enum Destination {
        IN, OUT
    }

    public static void init() {
        if (init) {
            return;
        }
        init = true;
        new Handshake.In();
        new Handshake.Out();
        new Status.In();
        new Status.Out();
        new Login.In();
        new Login.Out();
        new Play.In();
        new Play.Out();
        GlowServer.logger.info("Initialized " + packets.size() + " packets.");
    }
}
