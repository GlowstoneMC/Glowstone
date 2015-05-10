package net.glowstone.entity;

import com.flowpowered.networking.Message;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.*;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockBed;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.constants.*;
import net.glowstone.entity.meta.ClientSettings;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.InventoryMonitor;
import net.glowstone.io.PlayerDataService;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.CloseWindowMessage;
import net.glowstone.net.message.play.inv.OpenWindowMessage;
import net.glowstone.net.message.play.inv.SetWindowContentsMessage;
import net.glowstone.net.message.play.inv.SetWindowSlotMessage;
import net.glowstone.net.message.play.inv.WindowPropertyMessage;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import net.glowstone.net.message.play.player.ResourcePackSendMessage;
import net.glowstone.net.message.play.player.UseBedMessage;
import net.glowstone.net.protocol.ProtocolType;
import net.glowstone.scoreboard.GlowScoreboard;
import net.glowstone.scoreboard.GlowTeam;
import net.glowstone.util.StatisticMap;
import net.glowstone.util.TextMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.title.Title;
import org.bukkit.title.TitleOptions;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

/**
 * Represents an in-game player.
 *
 * @author Graham Edgecombe
 */
@DelegateDeserialization(GlowOfflinePlayer.class)
public final class GlowPlayer extends GlowHumanEntity implements Player {

    /**
     * A static entity id to use when telling the client about itself.
     */
    private static final int SELF_ID = 0;

    /**
     * This player's session.
     */
    private final GlowSession session;

    /**
     * The entities that the client knows about.
     */
    private final Set<GlowEntity> knownEntities = new HashSet<>();

    /**
     * The entities that are hidden from the client.
     */
    private final Set<UUID> hiddenEntities = new HashSet<>();

    /**
     * The chunks that the client knows about.
     */
    private final Set<GlowChunk.Key> knownChunks = new HashSet<>();

    /**
     * A queue of BlockChangeMessages to be sent.
     */
    private final List<BlockChangeMessage> blockChanges = new LinkedList<>();

    /**
     * A queue of messages that should be sent after block changes are processed.
     * Used for sign updates and other situations where the block must be sent first.
     */
    private final List<Message> afterBlockChanges = new LinkedList<>();

    /**
     * The set of plugin channels this player is listening on
     */
    private final Set<String> listeningChannels = new HashSet<>();

    /**
     * The player's statistics, achievements, and related data.
     */
    private final StatisticMap stats = new StatisticMap();

    /**
     * Whether the player has played before (will be false on first join).
     */
    private final boolean hasPlayedBefore;

    /**
     * The time the player first played, or 0 if unknown.
     */
    private final long firstPlayed;

    /**
     * The time the player last played, or 0 if unknown.
     */
    private final long lastPlayed;

    /**
     * The time the player joined.
     */
    private long joinTime;

    /**
     * The settings sent by the client.
     */
    private ClientSettings settings = ClientSettings.DEFAULT;

    /**
     * The lock used to prevent chunks from unloading near the player.
     */
    private ChunkManager.ChunkLock chunkLock;

    /**
     * The tracker for changes to the currently open inventory.
     */
    private InventoryMonitor invMonitor;

    /**
     * The display name of this player, for chat purposes.
     */
    private String displayName;

    /**
     * The name a player has in the player list
     */
    private String playerListName;

    /**
     * Cumulative amount of experience points the player has collected.
     */
    private int totalExperience = 0;

    /**
     * The current level (or skill point amount) of the player.
     */
    private int level = 0;

    /**
     * The progress made to the next level, from 0 to 1.
     */
    private float experience = 0;

    /**
     * The human entity's current food level
     */
    private int food = 20;

    /**
     * The player's current exhaustion level.
     */
    private float exhaustion = 0;

    /**
     * The player's current saturation level.
     */
    private float saturation = 0;

    /**
     * Whether to perform special scaling of the player's health.
     */
    private boolean healthScaled = false;

    /**
     * The scale at which to display the player's health.
     */
    private double healthScale = 20;

    /**
     * This player's current time offset.
     */
    private long timeOffset = 0;

    /**
     * Whether the time offset is relative.
     */
    private boolean timeRelative = true;

    /**
     * The player-specific weather, or null for normal weather.
     */
    private WeatherType playerWeather = null;

    /**
     * The player's compass target.
     */
    private Location compassTarget;

    /**
     * Whether this player's sleeping state is ignored when changing time.
     */
    private boolean sleepingIgnored;

    /**
     * The bed in which the player currently lies
     */
    private GlowBlock bed;

    /**
     * The bed spawn location of a player
     */
    private Location bedSpawn;

    /**
     * Whether to use the bed spawn even if there is no bed block.
     */
    private boolean bedSpawnForced;

    /**
     * The location of the sign the player is currently editing, or null.
     */
    private Location signLocation;

    /**
     * Whether the player is permitted to fly.
     */
    private boolean canFly;

    /**
     * Whether the player is currently flying.
     */
    private boolean flying;

    /**
     * The player's base flight speed.
     */
    private float flySpeed = 0.1f;

    /**
     * The player's base walking speed.
     */
    private float walkSpeed = 0.2f;

    /**
     * The scoreboard the player is currently subscribed to.
     */
    private GlowScoreboard scoreboard;

    /**
     * The player's current title, if any
     */
    private Title currentTitle = new Title();

    /**
     * The player's current title options
     */
    private TitleOptions titleOptions = new TitleOptions();

    /**
     * Creates a new player and adds it to the world.
     *
     * @param session The player's session.
     * @param profile The player's profile with name and UUID information.
     * @param reader  The PlayerReader to be used to initialize the player.
     */
    public GlowPlayer(GlowSession session, PlayerProfile profile, PlayerDataService.PlayerReader reader) {
        super(initLocation(session, reader), profile);
        setBoundingBox(0.6, 1.8);
        this.session = session;

        chunkLock = world.newChunkLock(getName());

        // enable compression if needed
        int compression = session.getServer().getCompressionThreshold();
        if (compression > 0) {
            session.enableCompression(compression);
        }

        // send login response
        session.send(new LoginSuccessMessage(profile.getUniqueId().toString(), profile.getName()));
        session.setProtocol(ProtocolType.PLAY);

        // read data from player reader
        hasPlayedBefore = reader.hasPlayedBefore();
        if (hasPlayedBefore) {
            firstPlayed = reader.getFirstPlayed();
            lastPlayed = reader.getLastPlayed();
            bedSpawn = reader.getBedSpawnLocation();
        } else {
            firstPlayed = 0;
            lastPlayed = 0;
        }

        //creates InventoryMonitor to avoid NullPointerException
        invMonitor = new InventoryMonitor(getOpenInventory());
        updateInventory(); // send inventory contents
    }

    public void join(GlowSession session, PlayerDataService.PlayerReader reader) {
        // send join game
        // in future, handle hardcore, difficulty, and level type
        String type = world.getWorldType().getName().toLowerCase();
        int gameMode = getGameMode().getValue();
        if (server.isHardcore()) {
            gameMode |= 0x8;
        }
        session.send(new JoinGameMessage(SELF_ID, gameMode, world.getEnvironment().getId(), world.getDifficulty().getValue(), session.getServer().getMaxPlayers(), type, world.getGameRuleMap().getBoolean("reducedDebugInfo")));
        setGameModeDefaults();

        // send server brand and supported plugin channels
        session.send(PluginMessage.fromString("MC|Brand", server.getName()));
        sendSupportedChannels();

        joinTime = System.currentTimeMillis();
        reader.readData(this);
        reader.close();

        // Add player to list of online players
        getServer().setPlayerOnline(this, true);

        // save data back out
        saveData();

        streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        sendTime();
        sendWeather();
        sendRainDensity();
        sendSkyDarkness();
        sendAbilities();

        scoreboard = server.getScoreboardManager().getMainScoreboard();
        scoreboard.subscribe(this);

        invMonitor = new InventoryMonitor(getOpenInventory());
        updateInventory(); // send inventory contents

        // send initial location
        session.send(new PositionRotationMessage(location));

        if (!server.getResourcePackURL().isEmpty()) {
            setResourcePack(server.getResourcePackURL(), server.getResourcePackHash());
        }
    }

    /**
     * Read the location from a PlayerReader for entity initialization. Will
     * fall back to a reasonable default rather than returning null.
     *
     * @param session The player's session.
     * @param reader  The PlayerReader to get the location from.
     * @return The location to spawn the player.
     */
    private static Location initLocation(GlowSession session, PlayerDataService.PlayerReader reader) {
        if (reader.hasPlayedBefore()) {
            Location loc = reader.getLocation();
            if (loc != null) {
                return loc;
            }
        }
        return session.getServer().getWorlds().get(0).getSpawnLocation();
    }

    @Override
    public String toString() {
        return "GlowPlayer{name=" + getName() + "}";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Get the network session attached to this player.
     *
     * @return The GlowSession of the player.
     */
    public GlowSession getSession() {
        return session;
    }

    /**
     * Get the join time in milliseconds, to be saved as last played time.
     *
     * @return The player's join time.
     */
    public long getJoinTime() {
        return joinTime;
    }

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    @Override
    public void remove() {
        knownChunks.clear();
        chunkLock.clear();
        saveData();
        getInventory().removeViewer(this);
        getInventory().getCraftingInventory().removeViewer(this);
        permissions.clearPermissions();
        getServer().setPlayerOnline(this, false);

        if (scoreboard != null) {
            scoreboard.unsubscribe(this);
            scoreboard = null;
        }
        super.remove();
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public void pulse() {
        super.pulse();

        // stream world
        streamBlocks();
        processBlockChanges();

        // add to playtime
        incrementStatistic(Statistic.PLAY_ONE_TICK);

        // update inventory
        for (InventoryMonitor.Entry entry : invMonitor.getChanges()) {
            sendItemChange(entry.slot, entry.item);
        }

        // send changed metadata
        List<MetadataMap.Entry> changes = metadata.getChanges();
        if (changes.size() > 0) {
            session.send(new EntityMetadataMessage(SELF_ID, changes));
        }

        // update or remove entities
        List<Integer> destroyIds = new LinkedList<>();
        for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
            GlowEntity entity = it.next();
            if (isWithinDistance(entity)) {
                for (Message msg : entity.createUpdateMessage()) {
                    session.send(msg);
                }
            } else {
                destroyIds.add(entity.getEntityId());
                it.remove();
            }
        }
        if (destroyIds.size() > 0) {
            session.send(new DestroyEntitiesMessage(destroyIds));
        }

        // add entities
        for (GlowEntity entity : world.getEntityManager()) {
            if (entity != this && isWithinDistance(entity) &&
                    !knownEntities.contains(entity) && !hiddenEntities.contains(entity.getUniqueId())) {
                knownEntities.add(entity);
                for (Message msg : entity.createSpawnMessage()) {
                    session.send(msg);
                }
            }
        }

        getAttributeManager().sendMessages(session);
    }

    /**
     * Process and send pending BlockChangeMessages.
     */
    private void processBlockChanges() {
        List<BlockChangeMessage> messages = new ArrayList<>(blockChanges);
        blockChanges.clear();

        // separate messages by chunk
        // inner map is used to only send one entry for same coordinates
        Map<GlowChunk.Key, Map<BlockVector, BlockChangeMessage>> chunks = new HashMap<>();
        for (BlockChangeMessage message : messages) {
            GlowChunk.Key key = new GlowChunk.Key(message.getX() >> 4, message.getZ() >> 4);
            if (canSeeChunk(key)) {
                Map<BlockVector, BlockChangeMessage> map = chunks.get(key);
                if (map == null) {
                    map = new HashMap<>();
                    chunks.put(key, map);
                }
                map.put(new BlockVector(message.getX(), message.getY(), message.getZ()), message);
            }
        }

        // send away
        for (Map.Entry<GlowChunk.Key, Map<BlockVector, BlockChangeMessage>> entry : chunks.entrySet()) {
            GlowChunk.Key key = entry.getKey();
            List<BlockChangeMessage> value = new ArrayList<>(entry.getValue().values());

            if (value.size() == 1) {
                session.send(value.get(0));
            } else if (value.size() > 1) {
                session.send(new MultiBlockChangeMessage(key.getX(), key.getZ(), value));
            }
        }

        // now send post-block-change messages
        List<Message> postMessages = new ArrayList<>(afterBlockChanges);
        afterBlockChanges.clear();
        for (Message message : postMessages) {
            session.send(message);
        }
    }

    /**
     * Streams chunks to the player's client.
     */
    private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<>(knownChunks);
        ArrayList<GlowChunk.Key> newChunks = new ArrayList<>();

        int centralX = location.getBlockX() >> 4;
        int centralZ = location.getBlockZ() >> 4;

        int radius = Math.min(server.getViewDistance(), 1 + settings.getViewDistance());
        for (int x = (centralX - radius); x <= (centralX + radius); x++) {
            for (int z = (centralZ - radius); z <= (centralZ + radius); z++) {
                GlowChunk.Key key = new GlowChunk.Key(x, z);
                if (knownChunks.contains(key)) {
                    previousChunks.remove(key);
                } else {
                    newChunks.add(key);
                }
            }
        }

        // early end if there's no changes
        if (newChunks.size() == 0 && previousChunks.size() == 0) {
            return;
        }

        // sort chunks by distance from player - closer chunks sent first
        Collections.sort(newChunks, new Comparator<GlowChunk.Key>() {
            @Override
            public int compare(GlowChunk.Key a, GlowChunk.Key b) {
                double dx = 16 * a.getX() + 8 - location.getX();
                double dz = 16 * a.getZ() + 8 - location.getZ();
                double da = dx * dx + dz * dz;
                dx = 16 * b.getX() + 8 - location.getX();
                dz = 16 * b.getZ() + 8 - location.getZ();
                double db = dx * dx + dz * dz;
                return Double.compare(da, db);
            }
        });

        // populate then send chunks to the player
        // done in two steps so that all the new chunks are finalized before any of them are sent
        // this prevents sending a chunk then immediately sending block changes in it because
        // one of its neighbors has populated

        // first step: force population then acquire lock on each chunk
        for (GlowChunk.Key key : newChunks) {
            world.getChunkManager().forcePopulation(key.getX(), key.getZ());
            knownChunks.add(key);
            chunkLock.acquire(key);
        }

        // second step: package chunks into bulk packets
        final int maxSize = 0x1fff00;  // slightly under protocol max size of 0x200000
        final boolean skylight = world.getEnvironment() == World.Environment.NORMAL;
        List<ChunkDataMessage> messages = new LinkedList<>();
        int bulkSize = 6; // size of bulk header

        // split the chunks into bulk packets based on how many fit
        for (GlowChunk.Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            ChunkDataMessage message = chunk.toMessage(skylight);
            // 10 bytes of header in bulk packet, plus data length
            int messageSize = 10 + message.getData().length;

            // if this chunk would make the message too big,
            if (bulkSize + messageSize > maxSize) {
                // send out what we have so far
                session.send(new ChunkBulkMessage(skylight, messages));
                messages = new LinkedList<>();
                bulkSize = 6;
            }

            bulkSize += messageSize;
            messages.add(message);
        }

        // send the leftovers
        if (!messages.isEmpty()) {
            session.send(new ChunkBulkMessage(skylight, messages));
        }

        // send visible tile entity data
        for (GlowChunk.Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            for (TileEntity entity : chunk.getRawTileEntities()) {
                entity.update(this);
            }
        }

        // and remove old chunks
        for (GlowChunk.Key key : previousChunks) {
            session.send(ChunkDataMessage.empty(key.getX(), key.getZ()));
            knownChunks.remove(key);
            chunkLock.release(key);
        }

        previousChunks.clear();
    }

    /**
     * Spawn the player at the given location after they have already joined.
     * Used for changing worlds and respawning after death.
     *
     * @param location The location to place the player.
     */
    private void spawnAt(Location location) {
        // switch worlds
        GlowWorld oldWorld = world;
        world.getEntityManager().unregister(this);
        world = (GlowWorld) location.getWorld();
        world.getEntityManager().register(this);

        // switch chunk set
        // no need to send chunk unload messages - respawn unloads all chunks
        knownChunks.clear();
        chunkLock.clear();
        chunkLock = world.newChunkLock(getName());

        // spawn into world
        String type = world.getWorldType().getName().toLowerCase();
        session.send(new RespawnMessage(world.getEnvironment().getId(), world.getDifficulty().getValue(), getGameMode().getValue(), type));
        setRawLocation(location); // take us to spawn position
        streamBlocks(); // stream blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        session.send(new PositionRotationMessage(location));
        sendWeather();
        sendRainDensity();
        sendSkyDarkness();
        sendTime();
        updateInventory();

        // fire world change if needed
        if (oldWorld != world) {
            EventFactory.callEvent(new PlayerChangedWorldEvent(this, oldWorld));
        }
    }

    /**
     * Respawn the player after they have died.
     */
    public void respawn() {
        // restore health
        setHealth(getMaxHealth());

        // determine spawn destination
        boolean spawnAtBed = true;
        Location dest = getBedSpawnLocation();
        if (dest == null) {
            dest = world.getSpawnLocation();
            spawnAtBed = false;
            if (bedSpawn != null) {
                setBedSpawnLocation(null);
                sendMessage("Your home bed was missing or obstructed");
            }
        }

        // fire event and perform spawn
        PlayerRespawnEvent event = new PlayerRespawnEvent(this, dest, spawnAtBed);
        EventFactory.callEvent(event);
        if (event.getRespawnLocation().getWorld().equals(getWorld()) && knownEntities.size() > 0) {
            // we need to manually reset all known entities if the player respawns in the same world
            List<Integer> entityIds = new ArrayList<>(knownEntities.size());
            for (GlowEntity e : knownEntities) {
                entityIds.add(e.getEntityId());
            }
            session.send(new DestroyEntitiesMessage(entityIds));
            knownEntities.clear();
        }
        spawnAt(event.getRespawnLocation());

        // just in case any items are left in their inventory after they respawn
        updateInventory();
    }

    /**
     * Checks whether the player can see the given chunk.
     *
     * @return If the chunk is known to the player's client.
     */
    public boolean canSeeChunk(GlowChunk.Key chunk) {
        return knownChunks.contains(chunk);
    }

    /**
     * Checks whether the player can see the given entity.
     *
     * @return If the entity is known to the player's client.
     */
    public boolean canSeeEntity(GlowEntity entity) {
        return knownEntities.contains(entity);
    }

    /**
     * Open the sign editor interface at the specified location.
     *
     * @param loc The location to open the editor at
     */
    public void openSignEditor(Location loc) {
        signLocation = loc.clone();
        signLocation.setX(loc.getBlockX());
        signLocation.setY(loc.getBlockY());
        signLocation.setZ(loc.getBlockZ());
        session.send(new SignEditorMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    /**
     * Check that the specified location matches that of the last opened sign
     * editor, and if so, clears the last opened sign editor.
     *
     * @param loc The location to check
     * @return Whether the location matched.
     */
    public boolean checkSignLocation(Location loc) {
        if (loc.equals(signLocation)) {
            signLocation = null;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a UserListItemMessage entry representing adding this player.
     *
     * @return The entry (action ADD_PLAYER) with this player's information.
     */
    public UserListItemMessage.Entry getUserListEntry() {
        TextMessage displayName = null;
        if (playerListName != null && !playerListName.isEmpty()) {
            displayName = new TextMessage(playerListName);
        }
        return UserListItemMessage.add(getProfile(), getGameMode().getValue(), 0, displayName);
    }

    /**
     * Send a UserListItemMessage to every player that can see this player.
     * @param updateMessage The message to send.
     */
    private void updateUserListEntries(UserListItemMessage updateMessage) {
        for (GlowPlayer player : server.getOnlinePlayers()) {
            if (player.canSee(this)) {
                player.getSession().send(updateMessage);
            }
        }
    }

    @Override
    public void setVelocity(Vector velocity) {
        PlayerVelocityEvent event = EventFactory.callEvent(new PlayerVelocityEvent(this, velocity));
        if (!event.isCancelled()) {
            velocity = event.getVelocity();
            super.setVelocity(velocity);
            session.send(new EntityVelocityMessage(SELF_ID, velocity));
        }
    }

    /**
     * Set the client settings for this player.
     *
     * @param settings The new client settings.
     */
    public void setSettings(ClientSettings settings) {
        this.settings = settings;
        metadata.set(MetadataIndex.PLAYER_SKIN_FLAGS, settings.getSkinFlags());
    }

    /**
     * Get this player's client settings.
     *
     * @return The player's client settings.
     */
    public ClientSettings getSettings() {
        return settings;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", getName());
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic stuff

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public InetSocketAddress getAddress() {
        return session.getAddress();
    }

    @Override
    public boolean isOnline() {
        return session.isActive() && session.isOnline();
    }

    @Override
    public boolean isBanned() {
        return server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    @Override
    @Deprecated
    public void setBanned(boolean banned) {
        server.getBanList(BanList.Type.NAME).addBan(getName(), null, null, null);
    }

    @Override
    public boolean isWhitelisted() {
        return server.getWhitelist().containsProfile(new PlayerProfile(getName(), getUniqueId()));
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(this);
        } else {
            server.getWhitelist().remove(new PlayerProfile(getName(), getUniqueId()));
        }
    }

    @Override
    public Player getPlayer() {
        return this;
    }

    @Override
    public boolean hasPlayedBefore() {
        return hasPlayedBefore;
    }

    @Override
    public long getFirstPlayed() {
        return firstPlayed;
    }

    @Override
    public long getLastPlayed() {
        return lastPlayed;
    }

    ////////////////////////////////////////////////////////////////////////////
    // HumanEntity overrides

    @Override
    public boolean isOp() {
        return getServer().getOpsList().containsUUID(getUniqueId());
    }

    @Override
    public void setOp(boolean value) {
        if (value) {
            getServer().getOpsList().add(this);
        } else {
            getServer().getOpsList().remove(new PlayerProfile(getName(), getUniqueId()));
        }
        permissions.recalculatePermissions();
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = super.createSpawnMessage();
        if (bed != null) {
            result.add(new UseBedMessage(getEntityId(), bed.getX(), bed.getY(), bed.getZ()));
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Editable properties

    @Override
    public String getDisplayName() {
        if (displayName != null) {
            return displayName;
        }
        GlowTeam team = (GlowTeam) getScoreboard().getPlayerTeam(this);
        if (team != null) {
            return team.getPlayerDisplayName(getName());
        }
        return getName();
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public String getPlayerListName() {
        return playerListName == null || playerListName.isEmpty() ? getName() : playerListName;
    }

    @Override
    public void setPlayerListName(String name) {
        // update state
        playerListName = name;

        // send update message
        TextMessage displayName = null;
        if (playerListName != null && !playerListName.isEmpty()) {
            displayName = new TextMessage(playerListName);
        }
        updateUserListEntries(UserListItemMessage.displayNameOne(getUniqueId(), displayName));
    }

    @Override
    public Location getCompassTarget() {
        return compassTarget;
    }

    @Override
    public void setCompassTarget(Location loc) {
        compassTarget = loc;
        session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    /**
     * Returns whether the player spawns at their bed even if there is no bed block.
     * @return Whether the player is forced to spawn at their bed.
     */
    public boolean isBedSpawnForced() {
        return bedSpawnForced;
    }

    @Override
    public Location getBedSpawnLocation() {
        if (bedSpawn == null) {
            return null;
        }

        // Find head of bed
        GlowBlock block = (GlowBlock) bedSpawn.getBlock();
        GlowBlock head = BlockBed.getHead(block);
        GlowBlock foot = BlockBed.getFoot(block);
        // If there is a bed, try to find an empty spot next to the bed
        if (head != null && head.getType() == Material.BED_BLOCK) {
            Block spawn = BlockBed.getExitLocation(head, foot);
            return spawn == null ? null : spawn.getLocation().add(0.5, 0.1, 0.5);
        } else {
            // If there is no bed and spawning is forced and there is space to spawn
            if (bedSpawnForced) {
                Material bottom = head.getType();
                Material top = head.getRelative(BlockFace.UP).getType();
                // Do not check floor when forcing spawn
                if (BlockBed.isValidSpawn(bottom) && BlockBed.isValidSpawn(top)) {
                    return bedSpawn.clone().add(0.5, 0.1, 0.5); // No blocks are blocking the spawn
                }
            }
            return null;
        }
    }

    @Override
    public void setBedSpawnLocation(Location bedSpawn) {
        setBedSpawnLocation(bedSpawn, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        this.bedSpawn = location;
        this.bedSpawnForced = force;
    }

    @Override
    public boolean isSleepingIgnored() {
        return sleepingIgnored;
    }

    @Override
    public void setSleepingIgnored(boolean isSleeping) {
        sleepingIgnored = isSleeping;
    }

    @Override
    public void setGameMode(GameMode mode) {
        if (getGameMode() != mode) {
            PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(this, mode);
            if (EventFactory.callEvent(event).isCancelled()) {
                return;
            }

            super.setGameMode(mode);
            updateUserListEntries(UserListItemMessage.gameModeOne(getUniqueId(), mode.getValue()));
            session.send(new StateChangeMessage(StateChangeMessage.Reason.GAMEMODE, mode.getValue()));
        }
        setGameModeDefaults();
    }

    private void setGameModeDefaults() {
        GameMode mode = getGameMode();
        setAllowFlight(mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR);
        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.INVISIBLE, mode == GameMode.SPECTATOR);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity status

    @Override
    public boolean isSneaking() {
        return metadata.getBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.SNEAKING);
    }

    @Override
    public void setSneaking(boolean sneak) {
        if (EventFactory.callEvent(new PlayerToggleSneakEvent(this, sneak)).isCancelled()) {
            return;
        }

        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.SNEAKING, sneak);
    }

    @Override
    public boolean isSprinting() {
        return metadata.getBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.SPRINTING);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        if (EventFactory.callEvent(new PlayerToggleSprintEvent(this, sprinting)).isCancelled()) {
            return;
        }

        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.SPRINTING, sprinting);
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        // Height of player's eyes above feet. Matches CraftBukkit.
        if (ignoreSneaking || !isSneaking()) {
            return 1.62;
        } else {
            return 1.54;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player capabilities

    @Override
    public boolean getAllowFlight() {
        return canFly;
    }

    @Override
    public void setAllowFlight(boolean flight) {
        canFly = flight;
        if (!canFly) flying = false;
        sendAbilities();
    }

    @Override
    public boolean isFlying() {
        return flying;
    }

    @Override
    public void setFlying(boolean value) {
        flying = value && canFly;
        sendAbilities();
    }

    @Override
    public float getFlySpeed() {
        return flySpeed;
    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        flySpeed = value;
        sendAbilities();
    }

    @Override
    public float getWalkSpeed() {
        return walkSpeed;
    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        walkSpeed = value;
        sendAbilities();
    }

    private void sendAbilities() {
        boolean creative = getGameMode() == GameMode.CREATIVE;
        int flags = (creative ? 8 : 0) | (canFly ? 4 : 0) | (flying ? 2 : 0) | (creative ? 1 : 0);
        // division is conversion from Bukkit to MC units
        session.send(new PlayerAbilitiesMessage(flags, flySpeed / 2f, walkSpeed / 2f));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Experience and levelling

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = Math.max(level, 0);
        sendExperience();
    }

    @Override
    public int getTotalExperience() {
        return totalExperience;
    }

    @Override
    public void setTotalExperience(int exp) {
        this.totalExperience = Math.max(exp, 0);
        sendExperience();
    }

    @Override
    public void giveExp(int xp) {
        totalExperience += xp;

        // gradually award levels based on xp points
        float value = 1.0f / getExpToLevel();
        for (int i = 0; i < xp; ++i) {
            experience += value;
            if (experience >= 1) {
                experience -= 1;
                value = 1.0f / getExpToLevel(++level);
            }
        }
        sendExperience();
    }

    @Override
    public float getExp() {
        return experience;
    }

    @Override
    public void setExp(float percentToLevel) {
        experience = Math.min(Math.max(percentToLevel, 0), 1);
        sendExperience();
    }

    @Override
    public int getExpToLevel() {
        return getExpToLevel(level);
    }

    private int getExpToLevel(int level) {
        if (level >= 30) {
            return 62 + (level - 30) * 7;
        } else if (level >= 15) {
            return 17 + (level - 15) * 3;
        } else {
            return 17;
        }
    }

    @Override
    public void giveExpLevels(int amount) {
        setLevel(getLevel() + amount);
    }

    private void sendExperience() {
        session.send(new ExperienceMessage(getExp(), getLevel(), getTotalExperience()));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Health and food handling

    @Override
    public void setHealth(double health) {
        super.setHealth(health);
        sendHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        super.setMaxHealth(health);
        sendHealth();
    }

    @Override
    public boolean isHealthScaled() {
        return healthScaled;
    }

    @Override
    public void setHealthScaled(boolean scale) {
        healthScaled = scale;
        sendHealth();
    }

    @Override
    public double getHealthScale() {
        return healthScale;
    }

    @Override
    public void setHealthScale(double scale) throws IllegalArgumentException {
        healthScaled = true;
        healthScale = scale;
        sendHealth();
    }

    @Override
    public int getFoodLevel() {
        return food;
    }

    @Override
    public void setFoodLevel(int food) {
        this.food = Math.min(food, 20);
        sendHealth();
    }

    @Override
    public float getExhaustion() {
        return exhaustion;
    }

    @Override
    public void setExhaustion(float value) {
        exhaustion = value;
    }

    @Override
    public float getSaturation() {
        return saturation;
    }

    @Override
    public void setSaturation(float value) {
        saturation = value;
        sendHealth();
    }

    private void sendHealth() {
        float finalHealth = (float) (getHealth() / getMaxHealth() * getHealthScale());
        session.send(new HealthMessage(finalHealth, getFoodLevel(), getSaturation()));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    /**
     * Teleport the player.
     *
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.UNKNOWN);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        Validate.notNull(location, "location cannot be null");
        Validate.notNull(location.getWorld(), "location's world cannot be null");
        Validate.notNull(cause, "cause cannot be null");

        if (this.location != null && this.location.getWorld() != null) {
            PlayerTeleportEvent event = new PlayerTeleportEvent(this, this.location, location, cause);
            if (EventFactory.callEvent(event).isCancelled()) {
                return false;
            }
            location = event.getTo();
        }

        if (location.getWorld() != world) {
            spawnAt(location);
        } else {
            session.send(new PositionRotationMessage(location));
            setRawLocation(location);
        }

        teleported = true;
        return true;
    }

    @Override
    protected boolean teleportToSpawn() {
        Location target = getBedSpawnLocation();
        if (target == null) {
            target = server.getWorlds().get(0).getSpawnLocation();
        }

        PlayerPortalEvent event = EventFactory.callEvent(new PlayerPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        spawnAt(target);
        teleported = true;

        awardAchievement(Achievement.THE_END, false);
        return true;
    }

    @Override
    protected boolean teleportToEnd() {
        if (!server.getAllowEnd()) {
            return false;
        }
        Location target = null;
        for (World world : server.getWorlds()) {
            if (world.getEnvironment() == Environment.THE_END) {
                target = world.getSpawnLocation();
                break;
            }
        }
        if (target == null) {
            return false;
        }

        PlayerPortalEvent event = EventFactory.callEvent(new PlayerPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        spawnAt(target);
        teleported = true;

        awardAchievement(Achievement.END_PORTAL, false);
        return true;
    }

    /**
     * This player enters the specified bed and is marked as sleeping.
     * @param block the bed
     */
    public void enterBed(GlowBlock block) {
        Validate.notNull(block, "Bed block cannot be null");
        Preconditions.checkState(bed == null, "Player already in bed");

        GlowBlock head = BlockBed.getHead(block);
        GlowBlock foot = BlockBed.getFoot(block);
        if (EventFactory.callEvent(new PlayerBedEnterEvent(this, head)).isCancelled()) {
            return;
        }

        // Occupy the bed
        BlockBed.setOccupied(head, foot, true);
        bed = head;
        sleeping = true;
        setRawLocation(head.getLocation());

        getSession().send(new UseBedMessage(SELF_ID, head.getX(), head.getY(), head.getZ()));
        UseBedMessage msg = new UseBedMessage(getEntityId(), head.getX(), head.getY(), head.getZ());
        for (GlowPlayer p : world.getRawPlayers()) {
            if (p != this && p.canSeeEntity(this)) {
                p.getSession().send(msg);
            }
        }
    }

    /**
     * This player leaves their bed causing them to quit sleeping.
     * @param setSpawn Whether to set the bed spawn of the player
     */
    public void leaveBed(boolean setSpawn) {
        Preconditions.checkState(bed != null, "Player is not in bed");
        GlowBlock head = BlockBed.getHead(bed);;
        GlowBlock foot = BlockBed.getFoot(bed);

        // Determine exit location
        Block exitBlock = BlockBed.getExitLocation(head, foot);
        if (exitBlock == null) { // If no empty blocks were found fallback to block above bed
            exitBlock = head.getRelative(BlockFace.UP);
        }
        Location exitLocation = exitBlock.getLocation().add(0.5, 0.1, 0.5); // Use center of block

        // Set their spawn (normally omitted if their bed gets destroyed instead of them leaving it)
        if (setSpawn) {
            setBedSpawnLocation(head.getLocation());
        }

        // Empty the bed
        BlockBed.setOccupied(head, foot, false);
        bed = null;
        sleeping = false;

        // And eject the player
        setRawLocation(exitLocation);
        teleported = true;

        // Call event
        EventFactory.callEvent(new PlayerBedLeaveEvent(this, head));

        getSession().send(new AnimateEntityMessage(SELF_ID, AnimateEntityMessage.OUT_LEAVE_BED));
        AnimateEntityMessage msg = new AnimateEntityMessage(getEntityId(), AnimateEntityMessage.OUT_LEAVE_BED);
        for (GlowPlayer p : world.getRawPlayers()) {
            if (p != this && p.canSeeEntity(this)) {
                p.getSession().send(msg);
            }
        }
    }

    @Override
    public void sendMessage(String message) {
        sendRawMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        for (String line : messages) {
            sendMessage(line);
        }
    }

    @Override
    public void sendRawMessage(String message) {
        // old-style formatting to json conversion is in TextMessage
        session.send(new ChatMessage(message));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sendActionBarMessage(String message) {
        // "old" formatting workaround because apparently "new" styling doesn't work as of 01/18/2015
        JSONObject json = new JSONObject();
        json.put("text", message);
        session.send(new ChatMessage(new TextMessage(json), 2));
    }

    @Override
    public void kickPlayer(String message) {
        session.disconnect(message == null ? "" : message);
    }

    @Override
    public boolean performCommand(String command) {
        return getServer().dispatchCommand(this, command);
    }

    @Override
    public void chat(String text) {
        chat(text, false);
    }

    /**
     * Says a message (or runs a command).
     *
     * @param text  message sent by the player.
     * @param async whether the message was received asynchronously.
     */
    public void chat(final String text, boolean async) {
        if (text.startsWith("/")) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    server.getLogger().info(getName() + " issued command: " + text);
                    try {
                        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(GlowPlayer.this, text);
                        if (!EventFactory.callEvent(event).isCancelled()) {
                            server.dispatchCommand(GlowPlayer.this, event.getMessage().substring(1));
                        }
                    } catch (Exception ex) {
                        sendMessage(ChatColor.RED + "An internal error occurred while executing your command.");
                        server.getLogger().log(Level.SEVERE, "Exception while executing command: " + text, ex);
                    }
                }
            };

            // if async is true, this task should happen synchronously
            // otherwise, we're sync already, it can happen here
            if (async) {
                server.getScheduler().runTask(null, task);
            } else {
                task.run();
            }
        } else {
            AsyncPlayerChatEvent event = EventFactory.onPlayerChat(async, this, text);
            if (event.isCancelled()) {
                return;
            }

            String message = String.format(event.getFormat(), getDisplayName(), event.getMessage());
            getServer().getLogger().info(message);
            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(message);
            }
        }
    }

    @Override
    public void saveData() {
        saveData(true);
    }

    public void saveData(boolean async) {
        if (async) {
            server.getScheduler().runTaskAsynchronously(null, new Runnable() {
                @Override
                public void run() {
                    server.getPlayerDataService().writeData(GlowPlayer.this);
                }
            });
        } else {
            server.getPlayerDataService().writeData(this);
        }
    }

    @Override
    public void loadData() {
        server.getPlayerDataService().readData(this);
    }

    @Override
    @Deprecated
    public void setTexturePack(String url) {
        setResourcePack(url);
    }

    @Override
    public void setResourcePack(String url) {
        setResourcePack(url, "");
    }

    @Override
    public void setResourcePack(String url, String hash) {
        session.send(new ResourcePackSendMessage(url, hash));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Effect and data transmission

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        Sound sound;
        switch (instrument) {
            case PIANO:
                sound = Sound.NOTE_PIANO;
                break;
            case BASS_DRUM:
                sound = Sound.NOTE_BASS_DRUM;
                break;
            case SNARE_DRUM:
                sound = Sound.NOTE_SNARE_DRUM;
                break;
            case STICKS:
                sound = Sound.NOTE_STICKS;
                break;
            case BASS_GUITAR:
                sound = Sound.NOTE_BASS_GUITAR;
                break;
            default:
                throw new IllegalArgumentException("Invalid instrument");
        }
        playSound(loc, sound, 3.0f, note.getId());
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        playNote(loc, Instrument.getByType(instrument), new Note(note));
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
        int id = effect.getId();
        boolean ignoreDistance = effect.isDistanceIgnored();
        session.send(new PlayEffectMessage(id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, ignoreDistance));
    }

    private void playEffect_(Location loc, Effect effect, int data) { // fix name collision with Spigot below
        this.playEffect(loc, effect, data);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        playEffect(loc, effect, GlowEffect.getDataValue(effect, data));
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        playSound(location, GlowSound.getName(sound), volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        if (location == null || sound == null) return;
        // the loss of precision here is a bit unfortunate but it's what CraftBukkit does
        double x = location.getBlockX() + 0.5;
        double y = location.getBlockY() + 0.5;
        double z = location.getBlockZ() + 0.5;
        session.send(new PlaySoundMessage(sound, x, y, z, volume, pitch));
    }

    private final Player.Spigot spigot = new Player.Spigot() {
        @Override
        public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius)  {
            if (effect.getType() == Effect.Type.PARTICLE) {
                MaterialData material = new MaterialData(id, (byte) data);
                showParticle(location, effect, material, offsetX, offsetY, offsetZ, speed, particleCount);
            } else {
                playEffect_(location, effect, data);
            }
        }

    };

    @Override
    public Player.Spigot spigot() {
        return spigot;
    }


    //@Override
    public void showParticle(Location loc, Effect particle, MaterialData material, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        if (location == null || particle == null || particle.getType() != Effect.Type.PARTICLE) return;

        int id = GlowParticle.getId(particle);
        boolean longDistance = GlowParticle.isLongDistance(particle);
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        float z = (float) loc.getZ();
        int[] extData = GlowParticle.getData(particle, material);
        session.send(new PlayParticleMessage(id, longDistance, x, y, z, offsetX, offsetY, offsetZ, speed, amount, extData));
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    @Override
    public void sendBlockChange(Location loc, int material, byte data) {
        sendBlockChange(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
    }

    public void sendBlockChange(BlockChangeMessage message) {
        // only send message if the chunk is within visible range
        GlowChunk.Key key = new GlowChunk.Key(message.getX() >> 4, message.getZ() >> 4);
        if (canSeeChunk(key)) {
            blockChanges.add(message);
        }
    }

    @Override
    public boolean sendChunkChange(Location loc, int sx, int sy, int sz, byte[] data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendSignChange(Location location, String[] lines) throws IllegalArgumentException {
        Validate.notNull(location, "location cannot be null");
        Validate.notNull(lines, "lines cannot be null");
        Validate.isTrue(lines.length == 4, "lines.length must equal 4");

        afterBlockChanges.add(UpdateSignMessage.fromPlainText(location.getBlockX(), location.getBlockY(), location.getBlockZ(), lines));
    }

    /**
     * Send a sign change, similar to {@link #sendSignChange(Location, String[])},
     * but using complete TextMessages instead of strings.
     * @param location the location of the sign
     * @param lines the new text on the sign or null to clear it
     * @throws IllegalArgumentException if location is null
     * @throws IllegalArgumentException if lines is non-null and has a length less than 4
     */
    public void sendSignChange(Location location, TextMessage[] lines) {
        Validate.notNull(location, "location cannot be null");
        Validate.notNull(lines, "lines cannot be null");
        Validate.isTrue(lines.length == 4, "lines.length must equal 4");

        afterBlockChanges.add(new UpdateSignMessage(location.getBlockX(), location.getBlockY(), location.getBlockZ(), lines));
    }

    /**
     * Send a block entity change to the given location.
     * @param location The location of the block entity.
     * @param type The type of block entity being sent.
     * @param nbt The NBT structure to send to the client.
     */
    public void sendBlockEntityChange(Location location, GlowBlockEntity type, CompoundTag nbt) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(type, "Type cannot be null");
        Validate.notNull(nbt, "NBT cannot be null");

        afterBlockChanges.add(new UpdateBlockEntityMessage(location.getBlockX(), location.getBlockY(), location.getBlockZ(), type.getValue(), nbt));
    }

    @Override
    public void sendMap(MapView map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Achievements and statistics

    @Override
    public boolean hasAchievement(Achievement achievement) {
        return stats.hasAchievement(achievement);
    }

    @Override
    public void awardAchievement(Achievement achievement) {
        awardAchievement(achievement, true);
    }

    /**
     * Awards the given achievement if the player already has the parent achievement,
     * otherwise does nothing. If {@code awardParents} is true, award the player all
     * parent achievements and the given achievement, making this method equivalent
     * to {@link #awardAchievement(Achievement)}.
     * @param achievement the achievement to award.
     * @param awardParents whether parent achievements should be awarded.
     * @return {@code true} if the achievement was awarded, {@code false} otherwise
     */
    public boolean awardAchievement(Achievement achievement, boolean awardParents) {
        if (hasAchievement(achievement)) return false;

        Achievement parent = achievement.getParent();
        if (parent != null && !hasAchievement(parent)) {
            if (!awardParents || !awardAchievement(parent, true)) {
                // does not have or failed to award required parent achievement
                return false;
            }
        }

        PlayerAchievementAwardedEvent event = new PlayerAchievementAwardedEvent(this, achievement);
        if (EventFactory.callEvent(event).isCancelled()) {
            return false; // event was cancelled
        }

        stats.setAchievement(achievement, true);
        sendAchievement(achievement, true);

        if (server.getAnnounceAchievements()) {
            // todo: make message fancier (hover, translated names)
            server.broadcastMessage(getName() + " earned achievement " + ChatColor.GREEN + "[" + achievement.name() + "]");
        }
        return true;
    }

    @Override
    public void removeAchievement(Achievement achievement) {
        if (!hasAchievement(achievement)) return;

        stats.setAchievement(achievement, false);
        sendAchievement(achievement, false);
    }

    private void sendAchievement(Achievement achievement, boolean has) {
        Map<String, Integer> values = new HashMap<>();
        values.put(GlowAchievement.getName(achievement), has ? 1 : 0);
        session.send(new StatisticMessage(values));
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException {
        return stats.get(statistic);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        return stats.get(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        return stats.get(statistic, entityType);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) throws IllegalArgumentException {
        stats.set(statistic, newValue);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) throws IllegalArgumentException {
        stats.set(statistic, material, newValue);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        stats.set(statistic, entityType, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        stats.add(statistic, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        stats.add(statistic, amount);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        stats.add(statistic, material, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        stats.add(statistic, material, amount);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        stats.add(statistic, entityType, 1);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) throws IllegalArgumentException {
        stats.add(statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException {
        stats.add(statistic, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) throws IllegalArgumentException {
        stats.add(statistic, -amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException {
        stats.add(statistic, material, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) throws IllegalArgumentException {
        stats.add(statistic, material, -amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException {
        stats.add(statistic, entityType, -1);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        stats.add(statistic, entityType, -amount);
    }

    public void sendStats() {
        session.send(stats.toMessage());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory

    @Override
    public void updateInventory() {
        session.send(new SetWindowContentsMessage(invMonitor.getId(), invMonitor.getContents()));
    }

    public void sendItemChange(int slot, ItemStack item) {
        session.send(new SetWindowSlotMessage(invMonitor.getId(), slot, item));
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        super.setItemOnCursor(item);
        session.send(new SetWindowSlotMessage(-1, -1, item));
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        if (!super.setWindowProperty(prop, value)) return false;
        session.send(new WindowPropertyMessage(invMonitor.getId(), prop.getId(), value));
        return true;
    }

    @Override
    public void openInventory(InventoryView view) {
        session.send(new CloseWindowMessage(invMonitor.getId()));

        super.openInventory(view);

        invMonitor = new InventoryMonitor(getOpenInventory());
        int viewId = invMonitor.getId();
        if (viewId != 0) {
            String title = view.getTitle();
            boolean defaultTitle = view.getType().getDefaultTitle().equals(title);
            if (view.getTopInventory() instanceof PlayerInventory && defaultTitle) {
                title = ((PlayerInventory) view.getTopInventory()).getHolder().getName();
            }
            Message open = new OpenWindowMessage(viewId, invMonitor.getType(), title, ((GlowInventory) view.getTopInventory()).getRawSlots());
            session.send(open);
        }

        updateInventory();
    }

    @Override
    public GlowItem drop(ItemStack stack) {
        GlowItem dropping = super.drop(stack);
        if (dropping != null) {
            PlayerDropItemEvent event = new PlayerDropItemEvent(this, dropping);
            EventFactory.callEvent(event);
            if (event.isCancelled()) {
                dropping.remove();
                dropping = null;
            }
        }
        return dropping;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player-specific time and weather

    @Override
    public void setPlayerTime(long time, boolean relative) {
        timeOffset = (time % GlowWorld.DAY_LENGTH + GlowWorld.DAY_LENGTH) % GlowWorld.DAY_LENGTH;
        timeRelative = relative;
        sendTime();
    }

    @Override
    public long getPlayerTime() {
        if (timeRelative) {
            // add timeOffset ticks to current time
            return (world.getTime() + timeOffset) % GlowWorld.DAY_LENGTH;
        } else {
            // return time offset
            return timeOffset;
        }
    }

    @Override
    public long getPlayerTimeOffset() {
        return timeOffset;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        return timeRelative;
    }

    @Override
    public void resetPlayerTime() {
        setPlayerTime(0, true);
    }

    public void sendTime() {
        long time = getPlayerTime();
        if (!timeRelative || !world.getGameRuleMap().getBoolean("doDaylightCycle")) {
            time = -time; // negative value indicates fixed time
        }
        session.send(new TimeMessage(world.getFullTime(), time));
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        playerWeather = type;
        sendWeather();
    }

    @Override
    public WeatherType getPlayerWeather() {
        return playerWeather;
    }

    @Override
    public void resetPlayerWeather() {
        playerWeather = null;
        sendWeather();
        sendRainDensity();
        sendSkyDarkness();
    }

    public void sendWeather() {
        boolean stormy = playerWeather == null ? getWorld().hasStorm() : playerWeather == WeatherType.DOWNFALL;
        session.send(new StateChangeMessage(stormy ? StateChangeMessage.Reason.START_RAIN : StateChangeMessage.Reason.STOP_RAIN, 0));
    }

    public void sendRainDensity() {
        session.send(new StateChangeMessage(StateChangeMessage.Reason.RAIN_DENSITY, getWorld().getRainDensity()));
    }

    public void sendSkyDarkness() {
        session.send(new StateChangeMessage(StateChangeMessage.Reason.SKY_DARKNESS, getWorld().getSkyDarkness()));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player visibility

    @Override
    public void hidePlayer(Player player) {
        Validate.notNull(player, "player cannot be null");
        if (equals(player) || !player.isOnline() || !session.isActive()) return;
        if (hiddenEntities.contains(player.getUniqueId())) return;

        hiddenEntities.add(player.getUniqueId());
        if (knownEntities.remove((GlowEntity) player)) {
            session.send(new DestroyEntitiesMessage(Arrays.asList(player.getEntityId())));
        }
        session.send(UserListItemMessage.removeOne(player.getUniqueId()));
    }

    @Override
    public void showPlayer(Player player) {
        Validate.notNull(player, "player cannot be null");
        if (equals(player) || !player.isOnline() || !session.isActive()) return;
        if (!hiddenEntities.contains(player.getUniqueId())) return;

        hiddenEntities.remove(player.getUniqueId());
        session.send(new UserListItemMessage(UserListItemMessage.Action.ADD_PLAYER, ((GlowPlayer) player).getUserListEntry()));
    }

    @Override
    public boolean canSee(Player player) {
        return !hiddenEntities.contains(player.getUniqueId());
    }

    /**
     * Called when a player hidden to this player disconnects.
     * This is necessary so the player is visible again after they reconnected.
     *
     * @param player The disconnected player
     */
    public void stopHidingDisconnectedPlayer(Player player) {
        hiddenEntities.remove(player.getUniqueId());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Scoreboard

    @Override
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {
        Validate.notNull(scoreboard, "Scoreboard must not be null");
        if (!(scoreboard instanceof GlowScoreboard)) {
            throw new IllegalArgumentException("Scoreboard must be GlowScoreboard");
        }
        if (this.scoreboard == null) {
            throw new IllegalStateException("Player has not loaded or is already offline");
        }
        this.scoreboard.unsubscribe(this);
        this.scoreboard = (GlowScoreboard) scoreboard;
        this.scoreboard.subscribe(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Conversable

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String input) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(getServer().getMessenger(), source, channel, message);
        if (listeningChannels.contains(channel)) {
            // only send if player is listening for it
            session.send(new PluginMessage(channel, message));
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return Collections.unmodifiableSet(listeningChannels);
    }

    /**
     * Add a listening channel to this player.
     *
     * @param channel The channel to add.
     */
    public void addChannel(String channel) {
        if (listeningChannels.add(channel)) {
            EventFactory.callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    /**
     * Remove a listening channel from this player.
     *
     * @param channel The channel to remove.
     */
    public void removeChannel(String channel) {
        if (listeningChannels.remove(channel)) {
            EventFactory.callEvent(new PlayerUnregisterChannelEvent(this, channel));
        }
    }

    /**
     * Send the supported plugin channels to the client.
     */
    private void sendSupportedChannels() {
        Set<String> listening = server.getMessenger().getIncomingChannels();

        if (!listening.isEmpty()) {
            // send NUL-separated list of channels we support
            ByteBuf buf = Unpooled.buffer(16 * listening.size());
            for (String channel : listening) {
                buf.writeBytes(channel.getBytes(StandardCharsets.UTF_8));
                buf.writeByte(0);
            }
            session.send(new PluginMessage("REGISTER", buf.array()));
        }
    }

    public void enchanted(int clicked) {
        this.level -= clicked + 1;
        if (level < 0) {
            this.level = 0;
            this.experience = 0;
            this.totalExperience = 0;
        }
        setLevel(level);
        setXpSeed(new Random().nextInt()); //TODO use entity's random instance?
    }

    ////////////////////////////////////////////////////////////////////////////
    // Titles

    @Override
    public Title getTitle() {
        return currentTitle.clone();
    }

    @Override
    public TitleOptions getTitleOptions() {
        return titleOptions.clone();
    }

    @Override
    public void setTitle(Title title) {
        setTitle(title, false);
    }

    @Override
    public void setTitle(Title title, boolean forceUpdate) {
        Validate.notNull(title, "Title cannot be null");

        String oldHeading = currentTitle.getHeading();
        currentTitle = title;

        if (forceUpdate || !StringUtils.equals(oldHeading, currentTitle.getHeading())) {
            session.sendAll(TitleMessage.fromTitle(currentTitle));
        }
    }

    @Override
    public void setTitleOptions(TitleOptions options) {
        if (options == null) {
            options = new TitleOptions();
        }
        titleOptions = options;
        session.send(TitleMessage.fromOptions(titleOptions));
    }

    @Override
    public void clearTitle() {
        currentTitle = new Title();
        session.send(new TitleMessage(TitleMessage.Action.CLEAR));
    }

    @Override
    public void resetTitle() {
        currentTitle = new Title(currentTitle.getHeading());
        titleOptions = new TitleOptions();
        session.send(new TitleMessage(TitleMessage.Action.RESET));
    }
}
