package net.glowstone.entity;

import com.flowpowered.networking.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.*;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.constants.GlowAchievement;
import net.glowstone.constants.GlowEffect;
import net.glowstone.constants.GlowSound;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.PlayerProfile;
import net.glowstone.inventory.InventoryMonitor;
import net.glowstone.io.PlayerDataService;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.JsonMessage;
import net.glowstone.net.message.login.LoginSuccessMessage;
import net.glowstone.net.message.play.entity.DestroyEntitiesMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import net.glowstone.net.protocol.ProtocolType;
import net.glowstone.util.StatisticMap;
import net.glowstone.util.TextWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

/**
 * Represents an in-game player.
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
    private final long joinTime;

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
     * The bed spawn location of a player
     */
    private Location bedSpawn;

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
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param profile The player's profile with name and UUID information.
     * @param reader The PlayerReader to be used to initialize the player.
     */
    public GlowPlayer(GlowSession session, PlayerProfile profile, PlayerDataService.PlayerReader reader) {
        super(initLocation(session, reader), profile);
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

        // send join game
        // in future, handle hardcore, difficulty, and level type
        String type = world.getWorldType().getName().toLowerCase();
        int gameMode = getGameMode().getValue();
        if (server.isHardcore()) {
            gameMode |= 0x8;
        }
        session.send(new JoinGameMessage(SELF_ID, gameMode, world.getEnvironment().getId(), world.getDifficulty().getValue(), session.getServer().getMaxPlayers(), type, false));
        setAllowFlight(getGameMode() == GameMode.CREATIVE);

        // send server brand and supported plugin channels
        session.send(PluginMessage.fromString("MC|Brand", server.getName()));
        sendSupportedChannels();

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
        joinTime = System.currentTimeMillis();
        reader.readData(this);
        reader.close();

        // save data back out
        saveData();

        streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
        sendTime();
        sendWeather();
        sendAbilities();

        invMonitor = new InventoryMonitor(getOpenInventory());
        updateInventory(); // send inventory contents

        // send initial location
        session.send(new PositionRotationMessage(location, getEyeHeight() + 0.05));
    }

    /**
     * Read the location from a PlayerReader for entity initialization. Will
     * fall back to a reasonable default rather than returning null.
     * @param session The player's session.
     * @param reader The PlayerReader to get the location from.
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
     * @return The GlowSession of the player.
     */
    public GlowSession getSession() {
        return session;
    }

    /**
     * Get the join time in milliseconds, to be saved as last played time.
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
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance) {
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
            if (entity == this)
                continue;
            boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

            if (withinDistance && !knownEntities.contains(entity)) {
                knownEntities.add(entity);
                for (Message msg : entity.createSpawnMessage()) {
                    session.send(msg);
                }
            }
        }
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
            Map<BlockVector, BlockChangeMessage> map = chunks.get(key);
            if (map == null) {
                map = new HashMap<>();
                chunks.put(key, map);
            }
            map.put(new BlockVector(message.getX(), message.getY(), message.getZ()), message);
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

        int radius = server.getViewDistance();
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
        final List<ChunkDataMessage> messages = new LinkedList<>();
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
                messages.clear();
                bulkSize = 6;
            }

            bulkSize += messageSize;
            messages.add(message);
        }

        // send the leftovers
        if (!messages.isEmpty()) {
            session.send(new ChunkBulkMessage(skylight, messages));
            messages.clear();
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
     * @param location The location to place the player.
     */
    private void spawnAt(Location location) {
        // switch worlds
        GlowWorld oldWorld = world;
        world.getEntityManager().deallocate(this);
        world = (GlowWorld) location.getWorld();
        world.getEntityManager().allocate(this);

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
        session.send(new PositionRotationMessage(location, getEyeHeight() + 0.05));
        sendWeather();

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
        boolean spawnAtBed = false;
        Location dest = world.getSpawnLocation();
        if (bedSpawn != null) {
            if (bedSpawn.getBlock().getType() == Material.BED_BLOCK) {
                // todo: spawn next to the bed instead of inside it
                dest = bedSpawn.clone();
                spawnAtBed = true;
            }
        }

        // fire event and perform spawn
        PlayerRespawnEvent event = new PlayerRespawnEvent(this, dest, spawnAtBed);
        EventFactory.callEvent(event);
        spawnAt(event.getRespawnLocation());
    }

    /**
     * Checks whether the player can see the given chunk.
     * @return If the chunk is known to the player's client.
     */
    public boolean canSee(GlowChunk.Key chunk) {
        return knownChunks.contains(chunk);
    }

    /**
     * Checks whether the player can see the given entity.
     * @return If the entity is known to the player's client.
     */
    public boolean canSee(GlowEntity entity) {
        return knownEntities.contains(entity);
    }

    /**
     * Open the sign editor interface at the specified location.
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
     * @return The entry (action ADD_PLAYER) with this player's information.
     */
    public UserListItemMessage.Entry getUserListEntry() {
        JSONObject displayName = null;
        if (playerListName != null && !playerListName.isEmpty()) {
            displayName = JsonMessage.toTextJson(playerListName);
        }
        return UserListItemMessage.add(getProfile(), getGameMode().getValue(), 0, displayName);
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
        return session.isActive();
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
        return server.getWhitelist().containsUUID(getUniqueId());
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getWhitelist().add(this);
        } else {
            server.getWhitelist().remove(getUniqueId());
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
            getServer().getOpsList().remove(getUniqueId());
        }
        permissions.recalculatePermissions();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Editable properties

    @Override
    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
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
        JSONObject displayName = null;
        if (playerListName != null && !playerListName.isEmpty()) {
            displayName = JsonMessage.toTextJson(playerListName);
        }
        Message updateMessage = UserListItemMessage.displayNameOne(getUniqueId(), displayName);
        for (GlowPlayer player : server.getOnlinePlayers()) {
            player.getSession().send(updateMessage);
        }
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

    @Override
    public Location getBedSpawnLocation() {
        return bedSpawn;
    }

    @Override
    public void setBedSpawnLocation(Location bedSpawn) {
        setBedSpawnLocation(bedSpawn, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        this.bedSpawn = location;
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
        boolean changed = getGameMode() != mode;
        super.setGameMode(mode);
        if (changed) session.send(new StateChangeMessage(3, mode.getValue()));

        setAllowFlight(mode == GameMode.CREATIVE);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity status

    @Override
    public boolean isSneaking() {
        return metadata.getBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.SNEAKING);
    }

    @Override
    public void setSneaking(boolean sneak) {
        if (EventFactory.onPlayerToggleSneak(this, sneak).isCancelled()) {
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
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        return teleport(location, TeleportCause.UNKNOWN);
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        if (this.location != null && this.location.getWorld() != null) {
            PlayerTeleportEvent event = EventFactory.onPlayerTeleport(this, getLocation(), location, cause);
            if (event.isCancelled()) return false;
            location = event.getTo();
        }

        if (location.getWorld() != world) {
            spawnAt(location);
        } else {
            // y offset accounts for floating point shenanigans in client physics
            session.send(new PositionRotationMessage(location, getEyeHeight() + 0.05));
            setRawLocation(location);
        }

        teleported = true;
        return true;
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
        // todo: use chat components instead of plain text
        // textwrapper also does not preserve non-color formatting
        for (String line : TextWrapper.wrapText(message)) {
            session.send(new ChatMessage(line));
        }
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
        if (text.startsWith("/")) {
            server.getLogger().info(getName() + " issued command: " + text);
            try {
                PlayerCommandPreprocessEvent event = EventFactory.onPlayerCommand(this, text);
                if (event.isCancelled()) {
                    return;
                }
                getServer().dispatchCommand(event.getPlayer(), event.getMessage().substring(1));
            } catch (Exception ex) {
                sendMessage(ChatColor.RED + "An internal error occured while executing your command.");
                getServer().getLogger().log(Level.SEVERE, "Exception while executing command: " + text, ex);
            }
        } else {
            // todo: async this
            PlayerChatEvent event = EventFactory.onPlayerChat(this, text);
            if (event.isCancelled()) {
                return;
            }

            String message = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
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
        // todo: update for 1.8 if needed
        session.send(PluginMessage.fromString("MC|RPack", url));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Effect and data transmission

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        playNote(loc, instrument.getType(), note.getId());
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        session.send(new BlockActionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note, Material.NOTE_BLOCK.getId()));
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
        int id = effect.getId();
        boolean ignoreDistance = id == 1013; // mob.wither.spawn, not in Bukkit yet
        session.send(new PlayEffectMessage(id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, ignoreDistance));
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
        if (canSee(key)) {
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
        if (hasAchievement(achievement)) return;

        stats.setAchievement(achievement, true);
        sendAchievement(achievement, true);

        // todo: make an announcement if that's enabled
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
            Message open = new OpenWindowMessage(viewId, invMonitor.getType(), title, view.getTopInventory().getSize());
            session.send(open);
        }

        updateInventory();
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
        if (!timeRelative) {
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
    }

    public void sendWeather() {
        boolean stormy = playerWeather == null ? getWorld().hasStorm() : playerWeather == WeatherType.DOWNFALL;
        session.send(new StateChangeMessage(stormy ? 2 : 1, 0));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player visibility

    @Override
    public void hidePlayer(Player player) {

    }

    @Override
    public void showPlayer(Player player) {

    }

    @Override
    public boolean canSee(Player player) {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Scoreboard

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException {

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
     * @param channel The channel to add.
     */
    public void addChannel(String channel) {
        if (listeningChannels.add(channel)) {
            EventFactory.callEvent(new PlayerRegisterChannelEvent(this, channel));
        }
    }

    /**
     * Remove a listening channel from this player.
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
}
