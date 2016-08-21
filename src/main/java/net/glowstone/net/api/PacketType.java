package net.glowstone.net.api;

import com.flowpowered.network.Message;
import net.glowstone.net.handler.play.player.UseItemMessage;
import net.glowstone.net.message.KickMessage;
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
import net.glowstone.net.message.play.scoreboard.ScoreboardDisplayMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardObjectiveMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardScoreMessage;
import net.glowstone.net.message.play.scoreboard.ScoreboardTeamMessage;
import net.glowstone.net.message.status.StatusPingMessage;
import net.glowstone.net.message.status.StatusRequestMessage;
import net.glowstone.net.message.status.StatusResponseMessage;
import net.glowstone.net.protocol.ProtocolType;

import static net.glowstone.net.protocol.ProtocolType.*;

public enum PacketType {
    UNKNOWN(-1, null, null),

    IN_HANDSHAKE(0x00, HANDSHAKE, HandshakeMessage.class),

    IN_STATUS_REQUEST(0x00, STATUS, StatusRequestMessage.class),
    IN_STATUS_PING(0x01, STATUS, StatusPingMessage.class),
    OUT_STATUS_RESPONSE(0x00, STATUS, StatusResponseMessage.class),
    OUT_STATUS_PONG(0x01, STATUS, StatusPingMessage.class),

    IN_LOGIN_START(0x00, LOGIN, LoginStartMessage.class),
    IN_ENCRYPT_RESPONSE(0x01, LOGIN, EncryptionKeyResponseMessage.class),
    OUT_SESSION_KICK(0x00, LOGIN, KickMessage.class),
    OUT_ENCRYPT_REQUEST(0x01, LOGIN, EncryptionKeyRequestMessage.class),
    OUT_LOGIN_SUCCESS(0x02, LOGIN, LoginSuccessMessage.class),
    OUT_COMPRESSION(0x03, LOGIN, SetCompressionMessage.class),

    IN_TELEPORT_CONFIRM(0x00, PLAY, TeleportConfirmMessage.class),
    IN_TAB_COMPLETE(0x01, PLAY, TabCompleteMessage.class),
    IN_CHAT(0x02, PLAY, IncomingChatMessage.class),
    IN_CLIENT_STATUS(0x03, PLAY, ClientStatusMessage.class),
    IN_CLIENT_SETTINGS(0x04, PLAY, ClientSettingsMessage.class),
    IN_TRANSACTION(0x05, PLAY, TransactionMessage.class),
    IN_ENCHANT_ITEM(0x06, PLAY, EnchantItemMessage.class),
    IN_WINDOW_CLICK(0x07, PLAY, WindowClickMessage.class),
    IN_WINDOW_CLOSE(0x08, PLAY, WindowClickMessage.class),
    IN_PLUGIN_LOAD(0x09, PLAY, PluginMessage.class),
    IN_INTERACT_ENTITY(0x0A, PLAY, InteractEntityMessage.class),
    IN_PING(0x0B, PLAY, PingMessage.class),
    IN_PLAYER_POSITION(0x0C, PLAY, PlayerPositionMessage.class),
    IN_PLAYER_POSITION_LOOK(0x0D, PLAY, PlayerPositionLookMessage.class),
    IN_PLAYER_LOOK(0x0E, PLAY, PlayerLookMessage.class),
    IN_PLAYER_UPDATE(0x0F, PLAY, PlayerUpdateMessage.class),
    IN_VEHICLE_MOVE(0x10, PLAY, VehicleMoveMessage.class),
    //TODO: 0x11 IN_STEER_BOAT
    IN_PLAYER_ABILITIES(0x12, PLAY, PlayerAbilitiesMessage.class),
    IN_BLOCK_DIG(0x13, PLAY, DiggingMessage.class),
    IN_PLAYER_ACTION(0x14, PLAY, PlayerActionMessage.class),
    IN_STEER_VEHICLE(0x15, PLAY, SteerVehicleMessage.class),
    IN_RESOURCEPACK_STATUS(0x16, PLAY, ResourcePackStatusMessage.class),
    IN_HELD_ITEM(0x17, PLAY, HeldItemMessage.class),
    IN_CREATIVE_ITEM(0x18, PLAY, CreativeItemMessage.class),
    IN_UPDATE_SIGN(0x19, PLAY, UpdateSignMessage.class),
    IN_SWING_ARM(0x1A, PLAY, PlayerSwingArmMessage.class),
    IN_SPECTATE(0x1B, PLAY, SpectateMessage.class),
    IN_BLOCK_PLACE(0x1C, PLAY, BlockPlacementMessage.class),
    IN_USE_ITEM(0x1D, PLAY, UseItemMessage.class),
    OUT_SPAWN_OBJECT(0x00, PLAY, SpawnObjectMessage.class),
    OUT_SPAWN_XP(0x01, PLAY, SpawnXpOrbMessage.class),
    OUT_SPAWN_LIGHTNING(0x02, PLAY, SpawnLightningStrikeMessage.class),
    OUT_SPAWN_MOB(0x03, PLAY, SpawnMobMessage.class),
    OUT_SPAWN_PAINTING(0x04, PLAY, SpawnPaintingMessage.class),
    OUT_SPAWN_PLAYER(0x05, PLAY, SpawnPlayerMessage.class),
    OUT_ANIMATE_ENTITY(0x06, PLAY, AnimateEntityMessage.class),
    OUT_STATISTIC(0x07, PLAY, StatisticMessage.class),
    //TODO: 0x08 OUT_BLOCK_DIG
    OUT_UPDATE_BLOCKENTITY(0x09, PLAY, UpdateBlockEntityMessage.class),
    OUT_BLOCK_ACTION(0x0A, PLAY, BlockActionMessage.class),
    OUT_BLOCK_CHANGE(0x0B, PLAY, BlockChangeMessage.class),
    OUT_BOSS_BAR(0x0C, PLAY, BossBarMessage.class),
    OUT_DIFFICULTY(0x0D, PLAY, ServerDifficultyMessage.class),
    OUT_TAB_COMPLETE(0x0E, PLAY, TabCompleteResponseMessage.class),
    OUT_CHAT(0x0F, PLAY, ChatMessage.class),
    OUT_MULTI_BLOCK_CHANGE(0x10, PLAY, MultiBlockChangeMessage.class),
    OUT_TRANSACTION(0x11, PLAY, TransactionMessage.class),
    OUT_WINDOW_CLOSE(0x12, PLAY, CloseWindowMessage.class),
    OUT_WINDOW_OPEN(0x13, PLAY, OpenWindowMessage.class),
    OUT_WINDOW_CONTENT(0x14, PLAY, SetWindowContentsMessage.class),
    OUT_WINDOW_PROPERTY(0x15, PLAY, WindowPropertyMessage.class),
    OUT_WINDOW_SLOT(0x16, PLAY, SetWindowSlotMessage.class),
    OUT_COOLDOWN(0x17, PLAY, SetCooldownMessage.class),
    OUT_PLUGIN_LOAD(0x18, PLAY, PluginMessage.class),
    OUT_NAMED_SOUND(0x19, PLAY, NamedSoundEffectMessage.class),
    OUT_KICK(0x1A, PLAY, KickMessage.class),
    OUT_ENTITY_STATUS(0x1B, PLAY, EntityStatusMessage.class),
    OUT_EXPLOSION(0x1C, PLAY, ExplosionMessage.class),
    OUT_CHUNK_UNLOAD(0x1D, PLAY, UnloadChunkMessage.class),
    OUT_STATE_CHANGE(0x1E, PLAY, StateChangeMessage.class),
    OUT_PONG(0x1F, PLAY, PingMessage.class),
    OUT_CHUNK_DATA(0x20, PLAY, ChunkDataMessage.class),
    OUT_EFFECT(0x21, PLAY, PlayEffectMessage.class),
    OUT_PARTICLE(0x22, PLAY, PlayParticleMessage.class),
    OUT_JOIN(0x23, PLAY, JoinGameMessage.class),
    OUT_MAP(0x24, PLAY, MapDataMessage.class),
    OUT_RELATIVE_ENTITY_POSITION(0x25, PLAY, RelativeEntityPositionMessage.class),
    OUT_RELATIVE_ENTITY_POSITION_ROTATION(0x26, PLAY, RelativeEntityPositionRotationMessage.class),
    OUT_ENTITY_ROTATION(0x27, PLAY, EntityRotationMessage.class),
    //TODO: 0x28 OUT_ENTITY
    OUT_VEHICLE_MOVE(0x29, PLAY, VehicleMoveMessage.class),
    OUT_SIGN_EDIT(0x2A, PLAY, SignEditorMessage.class),
    OUT_PLAYER_ABILITIES(0x2B, PLAY, PlayerAbilitiesMessage.class),
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
    OUT_HELD_ITEM(0x37, PLAY, HeldItemMessage.class),
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

    private final int id;
    private final ProtocolType protocol;
    private final Class<? extends Message> packetClass;

    PacketType(int id, ProtocolType protocol, Class<? extends Message> packetClass) {
        this.id = id;
        this.protocol = protocol;
        this.packetClass = packetClass;
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

    public static PacketType getType(int id, ProtocolType protocol) {
        for (PacketType packetType : values()) {
            if (packetType.getId() == id && packetType.getProtocol() == protocol) {
                return packetType;
            }
        }
        return UNKNOWN;
    }

    public static PacketType getType(Class<? extends Message> clazz, Destination destination) {
        if (destination == null || clazz == null) {
            return null;
        }
        for (PacketType packetType : values()) {
            if (packetType.getPacketClass() == clazz && packetType.name().startsWith(destination.name())) {
                return packetType;
            }
        }
        return UNKNOWN;
    }

    public enum Destination {
        IN, OUT;
    }
}
