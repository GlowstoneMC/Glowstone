package net.glowstone.entity;

import com.destroystokyo.paper.Title;
import com.flowpowered.network.Message;
import com.flowpowered.network.util.ByteBufUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.*;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockBed;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.SignEntity;
import net.glowstone.block.itemtype.ItemFood;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.chunk.ChunkManager.ChunkLock;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.chunk.GlowChunk.Key;
import net.glowstone.constants.*;
import net.glowstone.entity.meta.ClientSettings;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataIndex.StatusFlags;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.InventoryMonitor;
import net.glowstone.io.PlayerDataService.PlayerReader;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.game.*;
import net.glowstone.net.message.play.game.StateChangeMessage.Reason;
import net.glowstone.net.message.play.game.TitleMessage.Action;
import net.glowstone.net.message.play.game.UserListItemMessage.Entry;
import net.glowstone.net.message.play.inv.*;
import net.glowstone.net.message.play.player.PlayerAbilitiesMessage;
import net.glowstone.net.message.play.player.ResourcePackSendMessage;
import net.glowstone.net.message.play.player.UseBedMessage;
import net.glowstone.scoreboard.GlowScoreboard;
import net.glowstone.scoreboard.GlowTeam;
import net.glowstone.util.Convert;
import net.glowstone.util.Position;
import net.glowstone.util.StatisticMap;
import net.glowstone.util.TextMessage;
import net.glowstone.util.nbt.CompoundTag;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.*;
import org.bukkit.Effect.Type;
import org.bukkit.World.Environment;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.*;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents an in-game player.
 *
 * @author Graham Edgecombe
 */
@DelegateDeserialization(GlowOfflinePlayer.class)
public class GlowPlayer extends GlowHumanEntity implements Player {

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
    private final Set<Key> knownChunks = new HashSet<>();

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
    private ChunkLock chunkLock;
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
    private int totalExperience;
    /**
     * The current level (or skill point amount) of the player.
     */
    private int level;
    /**
     * The progress made to the next level, from 0 to 1.
     */
    private float experience;
    /**
     * The human entity's current food level
     */
    private int food = 20;
    /**
     * The player's current exhaustion level.
     */
    private float exhaustion;
    /**
     * The player's current saturation level.
     */
    private float saturation;
    /**
     * Whether to perform special scaling of the player's health.
     */
    private boolean healthScaled;
    /**
     * The scale at which to display the player's health.
     */
    private double healthScale = 20;
    /**
     * If this player has seen the end credits.
     */
    @Getter
    @Setter
    private boolean seenCredits;
    /**
     * Recipes this player has unlocked.
     */
    private Collection<Recipe> recipes = new HashSet<>();
    /**
     * This player's current time offset.
     */
    private long timeOffset;
    /**
     * Whether the time offset is relative.
     */
    private boolean timeRelative = true;
    /**
     * The player-specific weather, or null for normal weather.
     */
    private WeatherType playerWeather;
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
    private Title.Builder currentTitle = new Title.Builder();
    /**
     * The one block the player is currently digging.
     */
    private GlowBlock digging;
    /**
     * The number of ticks elapsed since the player started digging.
     */
    private long diggingTicks = 0;

    public Location teleportedTo = null;
    /**
     * The one itemstack the player is currently usage and associated time.
     */
    @Getter
    @Setter
    private ItemStack usageItem;
    @Getter
    @Setter
    private long usageTime;

    /**
     * Creates a new player and adds it to the world.
     *
     * @param session The player's session.
     * @param profile The player's profile with name and UUID information.
     * @param reader  The PlayerReader to be used to initialize the player.
     */
    public GlowPlayer(GlowSession session, PlayerProfile profile, PlayerReader reader) {
        super(initLocation(session, reader), profile);
        setBoundingBox(0.6, 1.8);
        this.session = session;

        chunkLock = world.newChunkLock(getName());

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
        server.getPlayerStatisticIoService().readStats(this);
    }

    /**
     * Read the location from a PlayerReader for entity initialization. Will
     * fall back to a reasonable default rather than returning null.
     *
     * @param session The player's session.
     * @param reader  The PlayerReader to get the location from.
     * @return The location to spawn the player.
     */
    private static Location initLocation(GlowSession session, PlayerReader reader) {
        if (reader.hasPlayedBefore()) {
            Location loc = reader.getLocation();
            if (loc != null) {
                return loc;
            }
        }

        return findSafeSpawnLocation(session.getServer().getWorlds().get(0).getSpawnLocation());
    }

    /**
     * Find a a Location obove or below the specified Location, which
     * is on ground.
     * The returned Location will be at the center of the block, X and Y wise.
     *
     * @param spawn The Location a safe spawn position should be found at.
     * @return The location to spawn the player at.
     */
    private static Location findSafeSpawnLocation(Location spawn) {
        World world = spawn.getWorld();
        int blockX = spawn.getBlockX();
        int blockY = spawn.getBlockY();
        int blockZ = spawn.getBlockZ();

        int highestY = world.getHighestBlockYAt(blockX, blockZ);

        int y = blockY;
        boolean wasPreviousSafe = false;
        for (; y <= highestY; y++) {
            Material type = world.getBlockAt(blockX, y, blockZ).getType();
            boolean safe = Material.AIR.equals(type);

            if (wasPreviousSafe && safe) {
                y--;
                break;
            }
            wasPreviousSafe = safe;
        }

        return new Location(
            world,
            blockX + 0.5,
            y,
            blockZ + 0.5
        );
    }

    public void join(GlowSession session, PlayerReader reader) {
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
        sendWeather();
        sendRainDensity();
        sendSkyDarkness();
        sendAbilities();

        // send initial location
        session.send(new PositionRotationMessage(location));

        session.send(((GlowWorldBorder) world.getWorldBorder()).createMessage());
        sendTime();
        setCompassTarget(world.getSpawnLocation()); // set our compass target

        scoreboard = server.getScoreboardManager().getMainScoreboard();
        scoreboard.subscribe(this);

        invMonitor = new InventoryMonitor(getOpenInventory());
        updateInventory(); // send inventory contents

        if (!server.getResourcePackURL().isEmpty()) {
            setResourcePack(server.getResourcePackURL(), server.getResourcePackHash());
        }
    }


    ////////////////////////////////////////////////////////////////////////////
    // Damages

    @Override
    public String toString() {
        return "GlowPlayer{name=" + getName() + "}";
    }

    @Override
    public void damage(double amount) {
        damage(amount, DamageCause.CUSTOM);
    }

    @Override
    public void damage(double amount, Entity cause) {
        super.damage(amount, cause);
        sendHealth();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private final Player.Spigot spigot = new Player.Spigot() {
        @Override
        public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius) {
            if (effect.getType() == Type.PARTICLE) {
                MaterialData material = new MaterialData(id, (byte) data);
                showParticle(location, effect, material, offsetX, offsetY, offsetZ, speed, particleCount);
            } else {
                playEffect_(location, effect, data);
            }
        }

        @Override
        public InetSocketAddress getRawAddress() {
            return session.getAddress();
        }

        @Override
        public void respawn() {
            if (isDead()) {
                GlowPlayer.this.respawn();
            }
        }

        @Override
        public boolean getCollidesWithEntities() {
            return isCollidable();
        }

        @Override
        public void setCollidesWithEntities(boolean collides) {
            setCollidable(collides);
        }
    };

    @Override
    public void damage(double amount, DamageCause cause) {
        // todo: better idea
        double old = getHealth();
        super.damage(amount, cause);
        if (old != getHealth()) {
            addExhaustion(0.1f);
            sendHealth();
            incrementStatistic(Statistic.DAMAGE_TAKEN, (int) Math.round(amount));
        }
    }

    @Override
    public boolean canTakeDamage(DamageCause damageCause) {
        return damageCause == DamageCause.FALL ? !getAllowFlight() && super.canTakeDamage(damageCause) : super.canTakeDamage(damageCause);
    }

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
     * Kicks this player
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
        getWorld().getRawPlayers().remove(this);

        if (scoreboard != null) {
            scoreboard.unsubscribe(this);
            scoreboard = null;
        }
        super.remove();
    }

    public void remove(boolean async) {
        knownChunks.clear();
        chunkLock.clear();
        saveData(async);
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

        if (usageItem != null) {
            if (usageItem == getItemInHand()) {
                if (--usageTime == 0) {
                    ItemType item = ItemTable.instance().getItem(usageItem.getType());
                    if (item instanceof ItemFood) {
                        ((ItemFood) item).eat(this, usageItem);
                    }
                }
            } else {
                usageItem = null;
                usageTime = 0;
            }
        }

        if (digging != null) {
            pulseDigging();
        }

        if (exhaustion > 4.0f) {
            exhaustion -= 4.0f;

            if (saturation > 0f) {
                saturation = Math.max(saturation - 1f, 0f);
                sendHealth();
            } else if (world.getDifficulty() != Difficulty.PEACEFUL) {
                FoodLevelChangeEvent event = EventFactory.callEvent(new FoodLevelChangeEvent(this, Math.max(food - 1, 0)));
                if (!event.isCancelled()) {
                    food = event.getFoodLevel();
                }
                sendHealth();
            }
        }

        if (getHealth() < getMaxHealth() && !isDead()) {
            if (food > 18 && ticksLived % 80 == 0 || world.getDifficulty() == Difficulty.PEACEFUL) {

                EntityRegainHealthEvent event1 = new EntityRegainHealthEvent(this, 1f, RegainReason.SATIATED);
                EventFactory.callEvent(event1);
                if (!event1.isCancelled()) {
                    setHealth(getHealth() + 1);
                }
                exhaustion = Math.min(exhaustion + 3.0f, 40.0f);

                saturation -= 3;
            }
        }


        if (food == 0 && getHealth() > 1 && ticksLived % 80 == 0) {
            damage(1, DamageCause.STARVATION);
        }

        // stream world
        streamBlocks();
        processBlockChanges();

        // add to playtime
        incrementStatistic(Statistic.PLAY_ONE_TICK);
        if (isSneaking()) {
            incrementStatistic(Statistic.SNEAK_TIME);
        }

        // update inventory
        for (InventoryMonitor.Entry entry : invMonitor.getChanges()) {
            sendItemChange(entry.slot, entry.item);
        }

        // send changed metadata
        List<MetadataMap.Entry> changes = metadata.getChanges();
        if (!changes.isEmpty()) {
            session.send(new EntityMetadataMessage(SELF_ID, changes));
        }

        // update or remove entities
        List<Integer> destroyIds = new LinkedList<>();
        for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
            GlowEntity entity = it.next();
            if (!isWithinDistance(entity) || entity.isRemoved()) {
                destroyIds.add(entity.getEntityId());
                it.remove();
            } else {
                entity.createUpdateMessage().forEach(session::send);
            }
        }
        if (!destroyIds.isEmpty()) {
            session.send(new DestroyEntitiesMessage(destroyIds));
        }

        // add entities
        knownChunks.parallelStream().forEach(key -> {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            chunk.getRawEntities().stream()
                    .filter(entity -> this != entity)
                    .filter(this::isWithinDistance)
                    .filter(entity -> !entity.isDead())
                    .filter(entity -> !knownEntities.contains(entity))
                    .filter(entity -> !hiddenEntities.contains(entity.getUniqueId()))
                    .forEach((entity) -> {
                        knownEntities.add(entity);
                        entity.createSpawnMessage().forEach(session::send);
                    });
        });

        if (passengerChanged) {
            session.send(new SetPassengerMessage(SELF_ID, getPassengers().stream().mapToInt(Entity::getEntityId).toArray()));
        }
        getAttributeManager().sendMessages(session);
    }

    @Override
    protected void pulsePhysics() {
        // trust the client with physics
        // just update the bounding box
        updateBoundingBox();
    }

    @Override
    protected void jump() {
        // don't make the client jump, please
    }

    /**
     * Process and send pending BlockChangeMessages.
     */
    private void processBlockChanges() {
        synchronized (blockChanges) {
            List<BlockChangeMessage> messages = new ArrayList<>(blockChanges);
            blockChanges.clear();
            // separate messages by chunk
            // inner map is used to only send one entry for same coordinates
            Map<Key, Map<BlockVector, BlockChangeMessage>> chunks = new HashMap<>();
            for (BlockChangeMessage message : messages) {
                if (message != null) {
                    Key key = new Key(message.getX() >> 4, message.getZ() >> 4);
                    if (canSeeChunk(key)) {
                        Map<BlockVector, BlockChangeMessage> map = chunks.computeIfAbsent(key, k -> new HashMap<>());
                        map.put(new BlockVector(message.getX(), message.getY(), message.getZ()), message);
                    }
                }
            }
            // send away
            for (Map.Entry<Key, Map<BlockVector, BlockChangeMessage>> entry : chunks.entrySet()) {
                Key key = entry.getKey();
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
            postMessages.forEach(session::send);
        }
    }

    /**
     * Streams chunks to the player's client.
     */
    private void streamBlocks() {
        Set<Key> previousChunks = new HashSet<>(knownChunks);
        ArrayList<Key> newChunks = new ArrayList<>();

        int centralX = location.getBlockX() >> 4;
        int centralZ = location.getBlockZ() >> 4;

        int radius = Math.min(server.getViewDistance(), 1 + settings.getViewDistance());
        for (int x = centralX - radius; x <= centralX + radius; x++) {
            for (int z = centralZ - radius; z <= centralZ + radius; z++) {
                Key key = new Key(x, z);
                if (knownChunks.contains(key)) {
                    previousChunks.remove(key);
                } else {
                    newChunks.add(key);
                }
            }
        }

        // early end if there's no changes
        if (newChunks.isEmpty() && previousChunks.isEmpty()) {
            return;
        }

        // sort chunks by distance from player - closer chunks sent first
        newChunks.sort((a, b) -> {
            double dx = 16 * a.getX() + 8 - location.getX();
            double dz = 16 * a.getZ() + 8 - location.getZ();
            double da = dx * dx + dz * dz;
            dx = 16 * b.getX() + 8 - location.getX();
            dz = 16 * b.getZ() + 8 - location.getZ();
            double db = dx * dx + dz * dz;
            return Double.compare(da, db);
        });

        // populate then send chunks to the player
        // done in two steps so that all the new chunks are finalized before any of them are sent
        // this prevents sending a chunk then immediately sending block changes in it because
        // one of its neighbors has populated

        // first step: force population then acquire lock on each chunk
        for (Key key : newChunks) {
            world.getChunkManager().forcePopulation(key.getX(), key.getZ());
            knownChunks.add(key);
            chunkLock.acquire(key);
        }

        boolean skylight = world.getEnvironment() == Environment.NORMAL;

        for (Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            session.send(chunk.toMessage(skylight));
        }

        // send visible block entity data
        for (Key key : newChunks) {
            GlowChunk chunk = world.getChunkAt(key.getX(), key.getZ());
            for (BlockEntity entity : chunk.getRawBlockEntities()) {
                entity.update(this);
            }
        }

        // and remove old chunks
        for (Key key : previousChunks) {
            session.send(new UnloadChunkMessage(key.getX(), key.getZ()));
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

        setRawLocation(location, false); // take us to spawn position
        session.send(new PositionRotationMessage(location));
        teleportedTo = location.clone();
        setCompassTarget(world.getSpawnLocation()); // set our compass target

        streamBlocks(); // stream blocks

        sendWeather();
        sendRainDensity();
        sendSkyDarkness();
        sendTime();
        updateInventory();

        // fire world change if needed
        if (oldWorld != world) {
            session.send(((GlowWorldBorder) world.getWorldBorder()).createMessage());
            EventFactory.callEvent(new PlayerChangedWorldEvent(this, oldWorld));
        }
    }

    /**
     * Respawn the player after they have died.
     */
    public void respawn() {
        // restore health
        setHealth(getMaxHealth());
        setFoodLevel(20);

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

        if (!spawnAtBed) {
            dest = findSafeSpawnLocation(dest);
        }

        // fire event and perform spawn
        PlayerRespawnEvent event = new PlayerRespawnEvent(this, dest, spawnAtBed);
        EventFactory.callEvent(event);
        if (event.getRespawnLocation().getWorld().equals(getWorld()) && !knownEntities.isEmpty()) {
            // we need to manually reset all known entities if the player respawns in the same world
            List<Integer> entityIds = new ArrayList<>(knownEntities.size());
            entityIds.addAll(knownEntities.stream().map(GlowEntity::getEntityId).collect(Collectors.toList()));
            session.send(new DestroyEntitiesMessage(entityIds));
            knownEntities.clear();
        }
        active = true;
        deathTicks = 0;
        spawnAt(event.getRespawnLocation());

        // just in case any items are left in their inventory after they respawn
        updateInventory();
    }

    /**
     * Checks whether the player can see the given chunk.
     *
     * @param chunk The chunk to check.
     * @return If the chunk is known to the player's client.
     */
    public boolean canSeeChunk(Key chunk) {
        return knownChunks.contains(chunk);
    }

    /**
     * Checks whether the player can see the given entity.
     *
     * @param entity The entity to check.
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
    public Entry getUserListEntry() {
        TextMessage displayName = null;
        if (playerListName != null && !playerListName.isEmpty()) {
            displayName = new TextMessage(playerListName);
        }
        return UserListItemMessage.add(getProfile(), getGameMode().getValue(), 0, displayName);
    }

    /**
     * Send a UserListItemMessage to every player that can see this player.
     *
     * @param updateMessage The message to send.
     */
    private void updateUserListEntries(UserListItemMessage updateMessage) {
        server.getRawOnlinePlayers().stream().filter(player -> player.canSee(this)).forEach(player -> player.getSession().send(updateMessage));
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
     * Set this player's client settings.
     *
     * @param settings The settings to set.
     */
    public void setSettings(ClientSettings settings) {
        this.settings = settings;
        metadata.set(MetadataIndex.PLAYER_SKIN_PARTS, settings.getSkinFlags());
        metadata.set(MetadataIndex.PLAYER_MAIN_HAND, settings.getMainHand());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic stuff

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("name", getName());
        return ret;
    }

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

    ////////////////////////////////////////////////////////////////////////////
    // HumanEntity overrides

    @Override
    public long getLastPlayed() {
        return lastPlayed;
    }

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

    ////////////////////////////////////////////////////////////////////////////
    // Editable properties

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> result = super.createSpawnMessage();
        if (bed != null) {
            result.add(new UseBedMessage(getEntityId(), bed.getX(), bed.getY(), bed.getZ()));
        }
        return result;
    }

    @Override
    public String getDisplayName() {
        if (displayName != null) {
            return displayName;
        }
        if (scoreboard != null) {
            GlowTeam team = (GlowTeam) scoreboard.getEntryTeam(getName());
            if (team != null) {
                return team.getPlayerDisplayName(getName());
            }
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
     *
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
        if (head != null) {
            // If there is a bed, try to find an empty spot next to the bed
            if (head.getType() == Material.BED_BLOCK) {
                Block spawn = BlockBed.getExitLocation(head, foot);
                return spawn == null ? null : spawn.getLocation().add(0.5, 0.1, 0.5);
            }
            if (bedSpawnForced) {
                Material bottom = head.getType();
                Material top = head.getRelative(BlockFace.UP).getType();
                // Do not check floor when forcing spawn
                if (BlockBed.isValidSpawn(bottom) && BlockBed.isValidSpawn(top)) {
                    return bedSpawn.clone().add(0.5, 0.1, 0.5); // No blocks are blocking the spawn
                }
            }
        }
        return null;
    }

    @Override
    public void setBedSpawnLocation(Location bedSpawn) {
        setBedSpawnLocation(bedSpawn, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean force) {
        bedSpawn = location;
        bedSpawnForced = force;
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
            session.send(new StateChangeMessage(Reason.GAMEMODE, mode.getValue()));
        }
        setGameModeDefaults();
    }

    @Override
    public boolean isHandRaised() {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity status

    private void setGameModeDefaults() {
        GameMode mode = getGameMode();
        setAllowFlight(mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR);
        metadata.setBit(MetadataIndex.STATUS, StatusFlags.INVISIBLE, mode == GameMode.SPECTATOR);
    }

    @Override
    public boolean isSneaking() {
        return metadata.getBit(MetadataIndex.STATUS, StatusFlags.SNEAKING);
    }

    @Override
    public void setSneaking(boolean sneak) {
        if (EventFactory.callEvent(new PlayerToggleSneakEvent(this, sneak)).isCancelled()) {
            return;
        }

        metadata.setBit(MetadataIndex.STATUS, StatusFlags.SNEAKING, sneak);
    }

    @Override
    public boolean isSprinting() {
        return metadata.getBit(MetadataIndex.STATUS, StatusFlags.SPRINTING);
    }

    @Override
    public void setSprinting(boolean sprinting) {
        if (EventFactory.callEvent(new PlayerToggleSprintEvent(this, sprinting)).isCancelled()) {
            return;
        }

        metadata.setBit(MetadataIndex.STATUS, StatusFlags.SPRINTING, sprinting);
    }

    @Override
    public double getEyeHeight() {
        return getEyeHeight(false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player capabilities

    @Override
    public double getEyeHeight(boolean ignoreSneaking) {
        // Height of player's eyes above feet. Matches CraftBukkit.
        if (ignoreSneaking || !isSneaking()) {
            return 1.62;
        } else {
            return 1.54;
        }
    }

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

    ////////////////////////////////////////////////////////////////////////////
    // Experience and levelling

    private void sendAbilities() {
        boolean creative = getGameMode() == GameMode.CREATIVE;
        int flags = (creative ? 8 : 0) | (canFly ? 4 : 0) | (flying ? 2 : 0) | (creative ? 1 : 0);
        // division is conversion from Bukkit to MC units
        session.send(new PlayerAbilitiesMessage(flags, flySpeed / 2f, walkSpeed / 2f));
    }

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
        totalExperience = Math.max(exp, 0);
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

    @Override
    public Entity getShoulderEntityLeft() {
        CompoundTag tag = getLeftShoulderTag();
        if (tag.isEmpty()) {
            return null;
        }
        UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
        return server.getEntity(uuid);
    }

    @Override
    public void setShoulderEntityLeft(Entity entity) {
        CompoundTag tag;
        if (entity == null) {
            tag = getLeftShoulderTag();
            if (!tag.isEmpty()) {
                EntityStorage.loadEntity(world, tag);
            }
        } else {
            tag = new CompoundTag();
            setLeftShoulderTag(tag);
        }
    }

    @Override
    public Entity getShoulderEntityRight() {
        CompoundTag tag = getRightShoulderTag();
        if (tag.isEmpty()) {
            return null;
        }
        UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
        return server.getEntity(uuid);
    }

    @Override
    public void setShoulderEntityRight(Entity entity) {
        CompoundTag tag;
        if (entity == null) {
            tag = getRightShoulderTag();
            if (!tag.isEmpty()) {
                EntityStorage.loadEntity(world, tag);
            }
        } else {
            tag = new CompoundTag();
            EntityStorage.save((GlowEntity) entity, tag);
            setRightShoulderTag(tag);
        }
    }

    public CompoundTag getLeftShoulderTag() {
        Object tag = metadata.get(MetadataIndex.PLAYER_LEFT_SHOULDER);
        return tag == null ? new CompoundTag() : (CompoundTag) tag;
    }

    public CompoundTag getRightShoulderTag() {
        Object tag = metadata.get(MetadataIndex.PLAYER_RIGHT_SHOULDER);
        return tag == null ? new CompoundTag() : (CompoundTag) tag;
    }

    public void setLeftShoulderTag(CompoundTag tag) {
        metadata.set(MetadataIndex.PLAYER_LEFT_SHOULDER, tag == null ? new CompoundTag() : tag);
    }

    public void setRightShoulderTag(CompoundTag tag) {
        metadata.set(MetadataIndex.PLAYER_RIGHT_SHOULDER, tag == null ? new CompoundTag() : tag);
    }

    /**
     * Recipes this player has unlocked.
     *
     * @return An immutable list of unlocked recipes.
     */
    public Collection<Recipe> getUnlockedRecipes() {
        return ImmutableList.copyOf(recipes);
    }

    /**
     * Teach the player a new recipe.
     *
     * @param recipe The recipe to be added to learnt recipes
     * @param notify If the player should be notified of the recipes learnt
     * @return If this recipe was not learned already.
     */
    public boolean learnRecipe(Recipe recipe, boolean notify) {
        return recipe != null && recipes.add(recipe);
    }

    /**
     * Remove a recipe from the player's known recipes.
     *
     * @param recipe The recipe to be removed from learnt recipes
     * @return If this recipe was learned before it was removed.
     */
    public boolean unlearnRecipe(Recipe recipe) {
        return recipes.remove(recipe);
    }

    /**
     * Checks to see if the player knows this recipe.
     *
     * @param recipe The recipe to check
     * @return If the player knows the recipe
     */
    public boolean knowsRecipe(Recipe recipe) {
        return recipes.contains(recipe);
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

    ////////////////////////////////////////////////////////////////////////////
    // Health and food handling

    private void sendExperience() {
        session.send(new ExperienceMessage(getExp(), getLevel(), getTotalExperience()));
    }

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

    private Entity spectating;

    @Override
    public Entity getSpectatorTarget() {
        return spectating;
    }

    @Override
    public void setSpectatorTarget(Entity entity) {
        teleport(entity.getLocation(), PlayerTeleportEvent.TeleportCause.SPECTATE);
        spectating = entity;
    }

    @Override
    public void sendTitle(String title, String subtitle) {
        sendTitle(new Title(title, subtitle));
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(new Title(title, subtitle, fadeIn, stay, fadeOut));
    }

    public void setFoodLevelAndSaturation(int food, float saturation) {
        this.food = Math.max(Math.min(food, 20), 0);
        this.saturation = Math.min(this.saturation + food * saturation * 2.0F, this.food);
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

    private boolean shouldCalculateExhaustion() {
        return getGameMode() == GameMode.SURVIVAL | getGameMode() == GameMode.ADVENTURE;
    }

    // todo: effects
    // todo: swim
    // todo: jump
    // todo: food poisoning
    // todo: jump and sprint
    public void addExhaustion(float exhaustion) {
        if (shouldCalculateExhaustion()) {
            this.exhaustion = Math.min(this.exhaustion + exhaustion, 40f);
        }
    }

    public void addMoveExhaustion(Location move) {
        if (shouldCalculateExhaustion() && !teleported) {
            double distanceSquared = location.distanceSquared(move);
            if (distanceSquared > 0) { // update packet and rotation
                double distance = Math.sqrt(distanceSquared);
                if (isSprinting()) {
                    addExhaustion((float) (0.1f * distance));
                }
            }
        }
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
        saturation = Math.min(value, food);
        sendHealth();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Actions

    private void sendHealth() {
        float finalHealth = (float) (getHealth() / getMaxHealth() * getHealthScale());
        session.send(new HealthMessage(finalHealth, getFoodLevel(), getSaturation()));
    }

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
        checkNotNull(location, "location cannot be null");
        checkNotNull(location.getWorld(), "location's world cannot be null");
        checkNotNull(cause, "cause cannot be null");

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

            world.getEntityManager().move(this, location);
            //Position.copyLocation(location, this.previousLocation);
            //Position.copyLocation(location, this.location);
            session.send(new PositionRotationMessage(location));
            teleportedTo = location.clone();
        }

        teleportedTo = location.clone();
        return true;
    }

    public void endTeleport() {
        Position.copyLocation(teleportedTo, location);
        teleportedTo = null;
        teleported = true;
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
     *
     * @param block the bed
     */
    public void enterBed(GlowBlock block) {
        checkNotNull(block, "Bed block cannot be null");
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
        setRawLocation(head.getLocation(), false);

        getSession().send(new UseBedMessage(SELF_ID, head.getX(), head.getY(), head.getZ()));
        UseBedMessage msg = new UseBedMessage(getEntityId(), head.getX(), head.getY(), head.getZ());
        world.getRawPlayers().stream().filter(p -> p != this && p.canSeeEntity(this)).forEach(p -> p.getSession().send(msg));
    }

    /**
     * This player leaves their bed causing them to quit sleeping.
     *
     * @param setSpawn Whether to set the bed spawn of the player
     */
    public void leaveBed(boolean setSpawn) {
        Preconditions.checkState(bed != null, "Player is not in bed");
        GlowBlock head = BlockBed.getHead(bed);
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
        setRawLocation(exitLocation, false);
        teleported = true;

        // Call event
        EventFactory.callEvent(new PlayerBedLeaveEvent(this, head));

        getSession().send(new AnimateEntityMessage(SELF_ID, AnimateEntityMessage.LEAVE_BED));
        AnimateEntityMessage msg = new AnimateEntityMessage(getEntityId(), AnimateEntityMessage.LEAVE_BED);
        world.getRawPlayers().stream().filter(p -> p != this && p.canSeeEntity(this)).forEach(p -> p.getSession().send(msg));
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
    public void sendActionBar(String message) {
        // "old" formatting workaround because apparently "new" styling doesn't work as of 01/18/2015
        JSONObject json = new JSONObject();
        json.put("text", message);
        session.send(new ChatMessage(new TextMessage(json), 2));
    }

    @Override
    public void sendActionBar(char alternateChar, String message) {
        sendActionBar(message); // TODO: don't ignore formatting codes
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        spawnParticle(particle, location, count, null);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        spawnParticle(particle, x, y, z, count, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        spawnParticle(particle, location, count, 0, 0, 0, 1, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(particle, x, y, z, count, 0, 0, 0, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, 1, null);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetY, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        double distance = getLocation().distanceSquared(location);
        boolean isLongDistance = GlowParticle.isLongDistance(particle);

        int particleId = GlowParticle.getId(particle);
        int[] particleData = GlowParticle.getExtData(particle, data);

        if (distance <= 1024.0D || isLongDistance && distance <= 262144.0D) {
            getSession().send(new PlayParticleMessage(particleId, isLongDistance, (float) location.getX(), (float) location.getY(), (float) location.getZ(), (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count, particleData));
        }
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(particle, new Location(world, x, y, z), count, offsetX, offsetY, offsetZ, extra, data);
    }

    private HashMap<Advancement, AdvancementProgress> advancements;

    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement) {
        return advancements.get(advancement);
    }

    @Override
    public String getLocale() {
        return null;
    }

    public boolean affectsSpawning = true;

    @Override
    public boolean getAffectsSpawning() {
        return affectsSpawning;
    }

    @Override
    public void setAffectsSpawning(boolean affects) {
        affectsSpawning = affects;
    }

    @Override
    public int getViewDistance() {
        return settings.getViewDistance();
    }

    @Override
    public void setViewDistance(int viewDistance) {
        settings.setViewDistance(viewDistance);
    }

    @Override
    public void kickPlayer(String message) {
        remove();
        session.disconnect(message == null ? "" : message);
    }

    public void kickPlayer(String message, boolean async) {
        remove(async);
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
    public void chat(String text, boolean async) {
        if (text.charAt(0) == '/') {
            Runnable task = () -> {
                server.getLogger().info(getName() + " issued command: " + text);
                try {
                    PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(this, text);
                    if (!EventFactory.callEvent(event).isCancelled()) {
                        server.dispatchCommand(this, event.getMessage().substring(1));
                    }
                } catch (Exception ex) {
                    sendMessage(ChatColor.RED + "An internal error occurred while executing your command.");
                    server.getLogger().log(Level.SEVERE, "Exception while executing command: " + text, ex);
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
            Bukkit.getScheduler().runTaskAsynchronously(null, () -> {
                server.getPlayerDataService().writeData(GlowPlayer.this);
                server.getPlayerStatisticIoService().writeStats(GlowPlayer.this);
            });
        } else {
            server.getPlayerDataService().writeData(this);
            server.getPlayerStatisticIoService().writeStats(this);
        }
    }

    @Override
    public void loadData() {
        server.getPlayerDataService().readData(this);
        server.getPlayerStatisticIoService().readStats(this);
    }

    private String resourcePackHash;
    private PlayerResourcePackStatusEvent.Status resourcePackStatus;

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
    public void setResourcePack(String url, byte[] hash) {
        if (hash == null) {
            throw new IllegalArgumentException("Resource pack hash must not be null!");
        }
        if (hash.length != 20) {
            throw new IllegalArgumentException("Resource pack hash is of an invalid length!");
        }
        setResourcePack(url, Convert.fromBytes(hash));
    }

    @Override
    public void setResourcePack(String url, String hash) {
        session.send(new ResourcePackSendMessage(url, hash));
        resourcePackHash = hash;
    }

    public void setResourcePackStatus(PlayerResourcePackStatusEvent.Status status) {
        resourcePackStatus = status;
    }

    @Override
    public PlayerResourcePackStatusEvent.Status getResourcePackStatus() {
        return resourcePackStatus;
    }

    @Override
    public String getResourcePackHash() {
        return resourcePackHash;
    }

    @Override
    public boolean hasResourcePack() {
        return resourcePackStatus == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Effect and data transmission

    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        Sound sound;
        switch (instrument) {
            case PIANO:
                sound = Sound.BLOCK_NOTE_HARP;
                break;
            case BASS_DRUM:
                sound = Sound.BLOCK_NOTE_BASEDRUM;
                break;
            case SNARE_DRUM:
                sound = Sound.BLOCK_NOTE_SNARE;
                break;
            case STICKS:
                sound = Sound.BLOCK_NOTE_HAT;
                break;
            case BASS_GUITAR:
                sound = Sound.BLOCK_NOTE_BASS;
                break;
            default:
                sound = null;
        }
        byte step = note.getId();
        int octave = note.getOctave();
        float pitch = (float) Math.pow(2, octave) / 2f;
        for (int i = 1; i <= step; i++) {
            if (i < 7) {
                pitch += 1f / 3f;
            } else if (step < 18) {
                pitch += 0.05f;
            } else {
                pitch += 0.1f;
            }
        }
        playSound(loc, sound, SoundCategory.MUSIC, 3.0f, pitch);
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        playNote(loc, Instrument.getByType(instrument), new Note(note));
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
        int id = effect.getId();
        session.send(new PlayEffectMessage(id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, false));
    }

    private void playEffect_(Location loc, Effect effect, int data) { // fix name collision with Spigot below
        playEffect(loc, effect, data);
    }

    @Override
    public <T> void playEffect(Location loc, Effect effect, T data) {
        playEffect(loc, effect, GlowEffect.getDataValue(effect, data));
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        playSound(location, sound, GlowSound.getSoundCategory(GlowSound.getVanillaId(sound)), volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        playSound(location, GlowSound.getVanillaSound(sound), volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
        if (location == null || sound == null) return;
        // the loss of precision here is a bit unfortunate but it's what CraftBukkit does
        double x = location.getBlockX() + 0.5;
        double y = location.getBlockY() + 0.5;
        double z = location.getBlockZ() + 0.5;
        session.send(new NamedSoundEffectMessage(sound, category, x, y, z, volume, pitch));
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        playSound(location, GlowSound.getVanillaId(sound), category, volume, pitch);
    }

    @Override
    public void stopSound(Sound sound) {
        stopSound(null, sound);
    }

    public void stopAllSounds() {
        stopSound("");
    }

    public void stopSound(SoundCategory category) {
        stopSound("", category);
    }

    @Override
    public void stopSound(Sound sound, SoundCategory soundCategory) {
        stopSound(GlowSound.getVanillaId(sound), soundCategory);
    }

    @Override
    public void stopSound(String sound, SoundCategory category) {
        String source = "";
        if (category != null) {
            source = category.name().toLowerCase();
        }
        if (sound == null || sound.equalsIgnoreCase("all")) {
            sound = "";
        }
        ByteBuf buffer = Unpooled.buffer();
        try {
            ByteBufUtils.writeUTF8(buffer, source); //Source
            ByteBufUtils.writeUTF8(buffer, sound); //Sound
            session.send(new PluginMessage("MC|StopSound", buffer.array()));
            buffer.release();
        } catch (IOException e) {
            GlowServer.logger.info("Failed to send stop-sound event.");
            e.printStackTrace();
        }
    }

    public void stopSound(SoundCategory category, Sound sound) {
        stopSound(sound == null ? "" : GlowSound.getVanillaId(sound), category);
    }

    @Override
    public void stopSound(String sound) {
        if (sound == null || sound.equalsIgnoreCase("all")) {
            sound = "";
        }
        stopSound(sound, null);
    }

    @Override
    public Player.Spigot spigot() {
        return spigot;
    }

    @Override
    public Location getOrigin() {
        return null;
    }


    //@Override
    public void showParticle(Location loc, Effect particle, MaterialData material, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        if (location == null || particle == null || particle.getType() != Type.PARTICLE) return;

        int id = GlowParticle.getId(particle);
        boolean longDistance = GlowParticle.isLongDistance(particle);
        float x = (float) loc.getX();
        float y = (float) loc.getY();
        float z = (float) loc.getZ();
        int[] extData = GlowParticle.getExtData(particle, material);
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
        Key key = new Key(message.getX() >> 4, message.getZ() >> 4);
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
        checkNotNull(location, "location cannot be null");
        checkNotNull(lines, "lines cannot be null");
        checkArgument(lines.length == 4, "lines.length must equal 4");

        afterBlockChanges.add(UpdateSignMessage.fromPlainText(location.getBlockX(), location.getBlockY(), location.getBlockZ(), lines));
    }

    /**
     * Send a sign change, similar to {@link #sendSignChange(Location, String[])},
     * but using complete TextMessages instead of strings.
     *
     * @param sign     the sign
     * @param location the location of the sign
     * @param lines    the new text on the sign or null to clear it
     * @throws IllegalArgumentException if location is null
     * @throws IllegalArgumentException if lines is non-null and has a length less than 4
     */
    public void sendSignChange(SignEntity sign, Location location, TextMessage[] lines) throws IllegalArgumentException {
        checkNotNull(location, "location cannot be null");
        checkNotNull(lines, "lines cannot be null");
        checkArgument(lines.length == 4, "lines.length must equal 4");

        CompoundTag tag = new CompoundTag();
        sign.saveNbt(tag);
        afterBlockChanges.add(new UpdateBlockEntityMessage(location.getBlockX(), location.getBlockY(), location.getBlockZ(), GlowBlockEntity.SIGN.getValue(), tag));
    }

    /**
     * Send a block entity change to the given location.
     *
     * @param location The location of the block entity.
     * @param type     The type of block entity being sent.
     * @param nbt      The NBT structure to send to the client.
     */
    public void sendBlockEntityChange(Location location, GlowBlockEntity type, CompoundTag nbt) {
        checkNotNull(location, "Location cannot be null");
        checkNotNull(type, "Type cannot be null");
        checkNotNull(nbt, "NBT cannot be null");

        afterBlockChanges.add(new UpdateBlockEntityMessage(location.getBlockX(), location.getBlockY(), location.getBlockZ(), type.getValue(), nbt));
    }

    @Override
    public void sendMap(MapView map) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sendMessage(BaseComponent component) {
        sendMessage(ChatMessageType.CHAT, component);
    }

    @Override
    public void sendMessage(BaseComponent... components) {
        sendMessage(ChatMessageType.CHAT, components);
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {
        session.send(new ChatMessage(TextMessage.decode(ComponentSerializer.toString(baseComponents)), chatMessageType.ordinal()));
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent[] header, BaseComponent[] footer) {
        TextMessage h = TextMessage.decode(ComponentSerializer.toString(header)), f = TextMessage.decode(ComponentSerializer.toString(footer));
        session.send(new UserListHeaderFooterMessage(h, f));
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent header, BaseComponent footer) {
        setPlayerListHeaderFooter(new BaseComponent[]{header}, new BaseComponent[]{footer});
    }

    @Override
    public void setTitleTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        currentTitle.fadeIn(fadeInTicks);
        currentTitle.stay(stayTicks);
        currentTitle.fadeOut(fadeOutTicks);
    }

    @Override
    public void setSubtitle(BaseComponent[] subtitle) {
        currentTitle.subtitle(subtitle);
    }

    @Override
    public void setSubtitle(BaseComponent subtitle) {
        currentTitle.subtitle(subtitle);
    }

    @Override
    public void showTitle(BaseComponent[] title) {
        sendTitle(new Title(title));
    }

    @Override
    public void showTitle(BaseComponent title) {
        sendTitle(new Title(title));
    }

    @Override
    public void showTitle(BaseComponent[] title, BaseComponent[] subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        sendTitle(new Title(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks));
    }

    @Override
    public void showTitle(BaseComponent title, BaseComponent subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        sendTitle(new Title(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks));
    }

    @Override
    public void sendTitle(Title title) {
        session.sendAll(TitleMessage.fromTitle(title));
    }

    /**
     * Send the player a title base on a {@link Title.Builder}
     *
     * @param title the {@link Title.Builder} to send the player
     */
    public void sendTitle(Title.Builder title) {
        sendTitle(title.build());
    }

    /**
     * Send the player their current title
     */
    public void sendTitle() {
        sendTitle(currentTitle);
        currentTitle = new Title.Builder();
    }

    @Override
    public void updateTitle(Title title) {
        Title builtTitle = currentTitle.build();

        if (title.getTitle().length != 0) {
            currentTitle.title(title.getTitle());
        }

        if (title.getSubtitle() != null) {
            currentTitle.subtitle(title.getSubtitle());
        }

        if (builtTitle.getFadeIn() != title.getFadeIn() && title.getFadeIn() != Title.DEFAULT_FADE_IN) {
            currentTitle.fadeIn(title.getFadeIn());
        }

        if (builtTitle.getStay() != title.getStay() && title.getStay() != Title.DEFAULT_STAY) {
            currentTitle.stay(title.getStay());
        }

        if (builtTitle.getFadeOut() != title.getFadeOut() && title.getFadeOut() != Title.DEFAULT_FADE_OUT) {
            currentTitle.fadeOut(title.getFadeOut());
        }
    }

    /**
     * Update a specific attribute of the player's title
     *
     * @param action the attribute to update
     * @param value  the value of the attribute
     */
    public void updateTitle(TitleMessage.Action action, Object... value) {
        Preconditions.checkArgument(value.length > 0, "Expected at least one argument. Got " + value.length);

        switch (action) {
            case TITLE:
                Preconditions.checkArgument(!(value instanceof String[] || value instanceof BaseComponent[]), "Value is not of the correct type");

                if (value[0] instanceof String) {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < value.length; i++) {
                        if (i > 0) builder.append(" ");
                        builder.append(value[i]);
                    }

                    currentTitle.title(builder.toString());
                } else {
                    BaseComponent[] formattedValue = (BaseComponent[]) value;
                    currentTitle.title(formattedValue);
                }

                break;
            case SUBTITLE:
                Preconditions.checkArgument(!(value instanceof String[] || value instanceof BaseComponent[]), "Value is not of the correct type");

                if (value[0] instanceof String) {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < value.length; i++) {
                        if (i > 0) builder.append(" ");
                        builder.append(value[i]);
                    }

                    currentTitle.subtitle(builder.toString());
                } else {
                    BaseComponent[] formattedValue = (BaseComponent[]) value;
                    currentTitle.subtitle(formattedValue);
                }

                break;
            case TIMES:
                Preconditions.checkArgument(!(value instanceof Integer[]), "Value is not of the correct type");
                Preconditions.checkArgument(value.length == 3, "Expected 3 values. Got " + value.length);

                currentTitle.fadeIn((int) value[0]);
                currentTitle.stay((int) value[1]);
                currentTitle.fadeOut((int) value[2]);

                break;
            default:
                Preconditions.checkArgument(true, "Action is something other than a title, subtitle, or times");
        }
    }

    @Override
    public void hideTitle() {
        currentTitle = new Title.Builder();
        session.send(new TitleMessage(Action.CLEAR));
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
     *
     * @param achievement  the achievement to award.
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
            server.broadcastMessage(getName() + " has just earned the achievement " + ChatColor.GREEN + "[" + GlowAchievement.getFancyName(achievement) + "]");
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

    public StatisticMap getStatisticMap() {
        return stats;
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
        if (invMonitor != null) {
            session.send(new SetWindowSlotMessage(invMonitor.getId(), slot, item));
        }
    }

    @Override
    public void setItemOnCursor(ItemStack item) {
        super.setItemOnCursor(item);
        session.send(new SetWindowSlotMessage(-1, -1, item));
    }

    @Override
    public boolean hasCooldown(Material material) {
        return false;
    }

    @Override
    public int getCooldown(Material material) {
        return 0;
    }

    @Override
    public void setCooldown(Material material, int ticks) {

    }

    @Override
    public MainHand getMainHand() {
        return metadata.getByte(MetadataIndex.PLAYER_MAIN_HAND) == 0 ? MainHand.LEFT : MainHand.RIGHT;
    }

    @Override
    public boolean setWindowProperty(Property prop, int value) {
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
            boolean defaultTitle = Objects.equals(view.getType().getDefaultTitle(), title);
            if (view.getTopInventory() instanceof PlayerInventory && defaultTitle) {
                title = ((PlayerInventory) view.getTopInventory()).getHolder().getName();
            }
            Message open = new OpenWindowMessage(viewId, invMonitor.getType(), title, ((GlowInventory) view.getTopInventory()).getRawSlots());
            session.send(open);
        }

        updateInventory();
    }

    @Override
    public InventoryView openMerchant(Villager villager, boolean b) {
        return null;
    }

    @Override
    public InventoryView openMerchant(Merchant merchant, boolean b) {
        return null;
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
            } else {
                incrementStatistic(Statistic.DROP, stack.getAmount());
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
            time *= -1; // negative value indicates fixed time
        }
        session.send(new TimeMessage(world.getFullTime(), time));
    }

    @Override
    public WeatherType getPlayerWeather() {
        return playerWeather;
    }

    @Override
    public void setPlayerWeather(WeatherType type) {
        playerWeather = type;
        sendWeather();
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
        session.send(new StateChangeMessage(stormy ? Reason.START_RAIN : Reason.STOP_RAIN, 0));
    }

    public void sendRainDensity() {
        session.send(new StateChangeMessage(Reason.RAIN_DENSITY, getWorld().getRainDensity()));
    }

    public void sendSkyDarkness() {
        session.send(new StateChangeMessage(Reason.SKY_DARKNESS, getWorld().getSkyDarkness()));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player visibility

    @Override
    public void hidePlayer(Player player) {
        checkNotNull(player, "player cannot be null");
        if (equals(player) || !player.isOnline() || !session.isActive()) return;
        if (hiddenEntities.contains(player.getUniqueId())) return;

        hiddenEntities.add(player.getUniqueId());
        if (knownEntities.remove(player)) {
            session.send(new DestroyEntitiesMessage(Collections.singletonList(player.getEntityId())));
        }
        session.send(UserListItemMessage.removeOne(player.getUniqueId()));
    }

    @Override
    public void showPlayer(Player player) {
        checkNotNull(player, "player cannot be null");
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
        checkNotNull(scoreboard, "Scoreboard must not be null");
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

    private List<Conversation> conversations = new ArrayList<>();

    @Override
    public boolean isConversing() {
        return !conversations.isEmpty();
    }

    @Override
    public void acceptConversationInput(String input) {
        conversations.get(0).acceptInput(input);
    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        boolean noQueue = conversations.isEmpty();
        conversations.add(conversation);
        if (noQueue) {
            conversation.begin();
        }
        return noQueue;
    }

    @Override
    public void abandonConversation(Conversation conversation) {
        abandonConversation(conversation, null);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent details) {
        conversations.remove(conversation);
        if (details == null) {
            conversation.abandon();
        } else {
            conversation.abandon(details);
        }
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
        checkArgument(listeningChannels.size() < 128, "Cannot add more than 127 channels!");
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
            buf.release();
        }
    }

    public void enchanted(int clicked) {
        level -= clicked + 1;
        if (level < 0) {
            level = 0;
            experience = 0;
            totalExperience = 0;
        }
        setLevel(level);
        setXpSeed(ThreadLocalRandom.current().nextInt()); //TODO use entity's random instance?
    }

    ////////////////////////////////////////////////////////////////////////////
    // Titles
    public Title getTitle() {
        return currentTitle.build();
    }

    public void clearTitle() {
        session.send(new TitleMessage(Action.CLEAR));
    }

    @Override
    public void resetTitle() {
        currentTitle = new Title.Builder();
        session.send(new TitleMessage(Action.RESET));
    }

    public GlowBlock getDigging() {
        return digging;
    }


    private void sendBlockBreakAnimation(Location loc, int destroyStage) {
        afterBlockChanges.add(new BlockBreakAnimationMessage(this.getEntityId(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), destroyStage));
    }

    private void broadcastBlockBreakAnimation(GlowBlock block, int destroyStage) {
        GlowChunk.Key key = new GlowChunk.Key(block.getChunk().getX(), block.getChunk().getZ());
        block.getWorld().getRawPlayers().stream()
                .filter(player -> player.canSeeChunk(key) && player != this)
                .forEach(player -> player.sendBlockBreakAnimation(block.getLocation(), destroyStage));
    }

    public void setDigging(GlowBlock block) {
        if (block == null) {
            if (digging != null) {
                // remove the animation
                broadcastBlockBreakAnimation(digging, 10);
            }
        } else {
            // show other clients the block is beginning to crack
            broadcastBlockBreakAnimation(block, 0);
        }

        diggingTicks = 0;
        digging = block;
    }

    private void pulseDigging() {
        ++diggingTicks;

        float hardness = digging.getMaterialValues().getHardness() * 20; // seconds to ticks

        // TODO: take into account the tool used to mine (ineffective=5x, effective=1.5x, material multiplier, etc.)
        // for now, assuming hands are used and the block is not dropped
        hardness *= 5;

        double completion = (double) diggingTicks / hardness;
        int stage = (int) (completion * 10);
        if (stage > 9) stage = 9;

        broadcastBlockBreakAnimation(digging, stage);
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute) {
        return null;
    }

    /**
     * Returns true if the player is inside a water block
     *
     * @return True if entity is in water.
     */
    public boolean isInWater() {
        Material mat = getLocation().getBlock().getType();
        return mat == Material.WATER || mat == Material.STATIONARY_WATER;
    }
}
