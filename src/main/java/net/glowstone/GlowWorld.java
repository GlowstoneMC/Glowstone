package net.glowstone;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.chunk.ChunkManager;
import net.glowstone.chunk.ChunkManager.ChunkLock;
import net.glowstone.chunk.ChunkSection;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.chunk.GlowChunk.Key;
import net.glowstone.chunk.GlowChunkSnapshot.EmptySnapshot;
import net.glowstone.constants.*;
import net.glowstone.data.CommandFunction;
import net.glowstone.entity.*;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.entity.physics.BoundingBox;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.io.WorldMetadataService.WorldFinalValues;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.anvil.AnvilWorldStorageProvider;
import net.glowstone.net.message.play.entity.EntityStatusMessage;
import net.glowstone.net.message.play.player.ServerDifficultyMessage;
import net.glowstone.util.BlockStateDelegate;
import net.glowstone.util.GameRuleManager;
import net.glowstone.util.RayUtil;
import net.glowstone.util.collection.ConcurrentSet;
import net.glowstone.util.config.WorldConfig;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * A class which represents the in-game world.
 *
 * @author Graham Edgecombe
 */
@ToString(of = "name")
public final class GlowWorld implements World {

    /**
     * The length in ticks of one Minecraft day.
     */
    public static final long DAY_LENGTH = 24000;
    /**
     * The metadata store for world objects.
     */
    private static final MetadataStore<World> metadata = new WorldMetadataStore();
    private static final int TICKS_PER_SECOND = 20;
    private static final int HALF_DAY_IN_TICKS = 12000;
    private static final int WEEK_IN_TICKS = 14 * HALF_DAY_IN_TICKS;
    /**
     * The length in ticks between autosaves (5 minutes).
     */
    private static final int AUTOSAVE_TIME = TICKS_PER_SECOND * 60 * 5;
    /**
     * The maximum height of ocean water.
     */
    private static int seaLevel;
    /**
     * The server of this world.
     */
    private final GlowServer server;
    /**
     * The name of this world.
     */
    private final String name;
    /**
     * The chunk manager.
     */
    private final ChunkManager chunks;
    /**
     * A lock kept on the spawn chunks.
     */
    private ChunkLock spawnChunkLock;
    /**
     * The world metadata service used.
     */
    private final WorldStorageProvider storageProvider;
    /**
     * The world's UUID
     */
    private final UUID uid;
    /**
     * The entity manager.
     */
    private final EntityManager entities = new EntityManager();
    /**
     * This world's Random instance.
     */
    private final Random random = new Random();
    /**
     * The chunk generator for this world.
     */
    private final ChunkGenerator generator;
    /**
     * The world populators for this world.
     */
    private final List<BlockPopulator> populators;
    /**
     * The game rules used in this world.
     */
    private final GameRuleManager gameRules = new GameRuleManager();
    /**
     * The environment.
     */
    private final Environment environment;
    /**
     * The world type.
     */
    @Getter
    @Setter
    private WorldType worldType;
    /**
     * Whether structure generation is enabled.
     */
    private final boolean generateStructures;
    /**
     * The world seed.
     */
    private final long seed;
    /**
     * Contains how regular blocks should be pulsed.
     */
    private final ConcurrentSet<Location> tickMap = new ConcurrentSet<>();
    private final Spigot spigot = new Spigot() {
        @Override
        public void playEffect(Location location, Effect effect) {
            GlowWorld.this.playEffect(location, effect, 0);
        }

        @Override
        public void playEffect(Location location, Effect effect, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int particleCount, int radius) {
            showParticle(location, effect, id, data, offsetX, offsetY, offsetZ, speed, particleCount, radius);
        }

        @Override
        public LightningStrike strikeLightning(Location loc, boolean isSilent) {
            return strikeLightningFireEvent(loc, false, isSilent);
        }

        @Override
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent) {
            return strikeLightningFireEvent(loc, true, isSilent);
        }
    };
    /**
     * The spawn position.
     */
    private Location spawnLocation;
    /**
     * Whether to keep the spawn chunks in memory (prevent them from being unloaded).
     */
    private boolean keepSpawnLoaded = true;
    /**
     * Whether to populate chunks when they are anchored.
     */
    private boolean populateAnchoredChunks;
    /**
     * Whether PvP is allowed in this world.
     */
    private boolean pvpAllowed = true;
    /**
     * Whether animals can spawn in this world.
     */
    private boolean spawnAnimals = true;
    /**
     * Whether monsters can spawn in this world.
     */
    private boolean spawnMonsters = true;
    /**
     * Whether it is currently raining/snowing on this world.
     */
    private boolean currentlyRaining = true;
    /**
     * How many ticks until the rain/snow status is expected to change.
     */
    private int rainingTicks;
    /**
     * Whether it is currently thundering on this world.
     */
    private boolean currentlyThundering = true;
    /**
     * How many ticks until the thundering status is expected to change.
     */
    private int thunderingTicks;
    /**
     * The rain density on the current world tick.
     */
    private float currentRainDensity;
    /**
     * The sky darkness on the current world tick.
     */
    private float currentSkyDarkness;
    /**
     * The age of the world, in ticks.
     */
    private long worldAge;
    /**
     * The current world time.
     */
    private long time;
    /**
     * The time until the next full-save.
     */
    private int saveTimer = AUTOSAVE_TIME;
    /**
     * The check to autosave
     */
    private boolean autosave = true;
    /**
     * The world's gameplay difficulty.
     */
    private Difficulty difficulty = Difficulty.PEACEFUL;
    /**
     * Ticks between when various types of entities are spawned.
     */
    private long ticksPerAnimal, ticksPerMonster;
    /**
     * Per-chunk spawn limits on various types of entities.
     */
    private int monsterLimit, animalLimit, waterAnimalLimit, ambientLimit;
    private Map<Integer, GlowStructure> structures;

    /**
     * The maximum height at which players may place blocks.
     */
    private int maxBuildHeight;

    private Set<Key> activeChunksSet = new HashSet<>();
    /**
     * The ScheduledExecutorService the for entity AI tasks threading.
     */
    private final ScheduledExecutorService aiTaskService;
    /**
     * The world border.
     */
    private final GlowWorldBorder worldBorder;
    /**
     * The functions for this world.
     */
    private final Map<String, CommandFunction> functions;

    /**
     * Creates a new world from the options in the given WorldCreator.
     *
     * @param server  The server for the world.
     * @param creator The WorldCreator to use.
     */
    public GlowWorld(GlowServer server, WorldCreator creator) {
        this.server = server;

        // set up values from WorldCreator
        name = creator.name();
        environment = creator.environment();
        worldType = creator.type();
        generateStructures = creator.generateStructures();

        generator = creator.generator();

        storageProvider = new AnvilWorldStorageProvider(new File(server.getWorldContainer(), name));
        storageProvider.setWorld(this);
        populators = generator.getDefaultPopulators(this);

        // set up values from server defaults
        ticksPerAnimal = server.getTicksPerAnimalSpawns();
        ticksPerMonster = server.getTicksPerMonsterSpawns();
        monsterLimit = server.getMonsterSpawnLimit();
        animalLimit = server.getAnimalSpawnLimit();
        waterAnimalLimit = server.getWaterAnimalSpawnLimit();
        ambientLimit = server.getAmbientSpawnLimit();
        keepSpawnLoaded = server.keepSpawnLoaded();
        populateAnchoredChunks = server.populateAnchoredChunks();
        difficulty = server.getDifficulty();
        maxBuildHeight = server.getMaxBuildHeight();
        seaLevel = GlowServer.getWorldConfig().getInt(WorldConfig.Key.SEA_LEVEL);
        worldBorder = new GlowWorldBorder(this);

        // read in world data
        WorldFinalValues values;
        values = storageProvider.getMetadataService().readWorldData();
        if (values != null) {
            if (values.getSeed() == 0L) {
                seed = creator.seed();
            } else {
                seed = values.getSeed();
            }
            uid = values.getUuid();
        } else {
            seed = creator.seed();
            uid = UUID.randomUUID();
        }

        chunks = new ChunkManager(this, storageProvider.getChunkIoService(), generator);
        structures = storageProvider.getStructureDataService().readStructuresData();
        functions = storageProvider.getFunctionIoService().readFunctions().stream()
                .collect(Collectors.toMap(CommandFunction::getFullName, function -> function));
        server.addWorld(this);
        server.getLogger().info("Preparing spawn for " + name + "...");
        EventFactory.callEvent(new WorldInitEvent(this));

        spawnChunkLock = keepSpawnLoaded ? newChunkLock("spawn") : null;

        setKeepSpawnInMemory(keepSpawnLoaded);

        server.getLogger().info("Preparing spawn for " + name + ": done");
        EventFactory.callEvent(new WorldLoadEvent(this));

        // pulse AI tasks
        aiTaskService = Executors.newScheduledThreadPool(1);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various internal mechanisms

    /**
     * Get the world chunk manager.
     *
     * @return The ChunkManager for the world.
     */
    public ChunkManager getChunkManager() {
        return chunks;
    }

    /**
     * Get the world's parent server.
     *
     * @return The GlowServer for the world.
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Get a new chunk lock object a player or other party can use to keep chunks loaded.
     *
     * @param desc A description for this chunk lock.
     * @return The ChunkLock.
     */
    public ChunkLock newChunkLock(String desc) {
        return new ChunkLock(chunks, name + ": " + desc);
    }

    /**
     * Updates all the entities within this world.
     */
    public void pulse() {
        List<GlowEntity> allEntities = new ArrayList<>(entities.getAll());
        List<GlowPlayer> players = new LinkedList<>();

        activeChunksSet.clear();

        // We should pulse our tickmap, so blocks get updated.
        pulseTickMap();

        // pulse players last so they actually see that other entities have
        // moved. unfortunately pretty hacky. not a problem for players b/c
        // their position is modified by session ticking.
        for (GlowEntity entity : allEntities) {
            if (entity instanceof GlowPlayer) {
                players.add((GlowPlayer) entity);
                updateActiveChunkCollection(entity);
            } else {
                entity.pulse();
            }
        }

        updateBlocksInActiveChunks();
        // why update blocks before Players or Entities? if there is a specific reason we should document it here.

        pulsePlayers(players);
        resetEntities(allEntities);
        worldBorder.pulse();

        updateWorldTime();
        informPlayersOfTime();
        updateOverworldWeather();

        handleSleepAndWake(players);

        saveWorld();
    }

    private void updateActiveChunkCollection(GlowEntity entity) {
        // build a set of chunks around each player in this world, the
        // server view distance is taken here
        int radius = server.getViewDistance();
        Location playerLocation = entity.getLocation();
        if (playerLocation.getWorld() == this) {
            int cx = playerLocation.getBlockX() >> 4;
            int cz = playerLocation.getBlockZ() >> 4;
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    if (isChunkLoaded(cx, cz)) {
                        activeChunksSet.add(new Key(x, z));
                    }
                }
            }
        }
    }

    private void updateBlocksInActiveChunks() {
        for (Key key : activeChunksSet) {
            int cx = key.getX();
            int cz = key.getZ();
            // check the chunk is loaded
            if (isChunkLoaded(cx, cz)) {
                GlowChunk chunk = getChunkAt(cx, cz);

                // thunder
                maybeStrikeLightningInChunk(cx, cz);

                // block ticking
                // we will choose 3 blocks per chunk's section
                ChunkSection[] sections = chunk.getSections();
                for (int i = 0; i < sections.length; i++) {
                    updateBlocksInSection(chunk, sections[i], i);
                }
            }
        }
    }

    private void updateBlocksInSection(GlowChunk chunk, ChunkSection section, int i) {
        if (section != null) {
            for (int j = 0; j < 3; j++) {
                int n = random.nextInt();
                int x = n & 0xF;
                int z = n >> 8 & 0xF;
                int y = n >> 16 & 0xF;
                int type = section.getType(x, y, z) >> 4;
                if (type != 0) { // filter air blocks
                    BlockType blockType = ItemTable.instance().getBlock(type);
                    // does this block needs random tick ?
                    if (blockType != null && blockType.canTickRandomly()) {
                        blockType.updateBlock(chunk.getBlock(x, y + (i << 4), z));
                    }
                }
            }
        }
    }

    private void saveWorld() {
        if (--saveTimer <= 0) {
            saveTimer = AUTOSAVE_TIME;
            chunks.unloadOldChunks();
            if (autosave) {
                save(true);
            }
        }
    }

    private void updateOverworldWeather() {
        // only tick weather in a NORMAL world
        if (environment == Environment.NORMAL) {
            if (--rainingTicks <= 0) {
                setStorm(!currentlyRaining);
            }

            if (--thunderingTicks <= 0) {
                setThundering(!currentlyThundering);
            }

            updateWeather();
        }
    }

    private void informPlayersOfTime() {
        if (worldAge % (30 * TICKS_PER_SECOND) == 0) {
            // Only send the time every 30 seconds; clients are smart.
            getRawPlayers().forEach(GlowPlayer::sendTime);
        }
    }

    // Tick the world age and time of day
    private void updateWorldTime() {
        worldAge++;
        // worldAge is used to determine when to (periodically) update clients of server time (time of day - "time")
        // also used to occasionally pulse some blocks (see "tickMap" and "requestPulse()")

        // Modulus by 24000, the tick length of a day
        if (gameRules.getBoolean("doDaylightCycle")) {
            time = (time + 1) % DAY_LENGTH;
        }
    }

    private void resetEntities(List<GlowEntity> entities) {
        entities.forEach(GlowEntity::reset);
    }

    private void pulsePlayers(List<GlowPlayer> players) {
        players.stream().filter(Objects::nonNull).forEach(GlowEntity::pulse);
    }

    private void handleSleepAndWake(List<GlowPlayer> players) {
        // Skip checking for sleeping players if no one is online
        if (!players.isEmpty()) {
            // If the night is over, wake up all players
            // Tick values for day/night time taken from the minecraft wiki
            if (getTime() < 12541 || getTime() > 23458) {
                wakeUpAllPlayers(players);
                // no need to send them the time - handle that normally
            } else { // otherwise check whether everyone is asleep
                boolean skipNight = gameRules.getBoolean("doDaylightCycle") && areAllPlayersSleeping(players);
                // check gamerule before iterating players (micro-optimization)
                if (skipNight) {
                    skipRestOfNight(players);
                }
            }
        }
    }

    private void skipRestOfNight(List<GlowPlayer> players) {
        worldAge = (worldAge / DAY_LENGTH + 1) * DAY_LENGTH;
        time = 0;
        wakeUpAllPlayers(players, true);
        // true = send time to all players because we just changed it (to 0), above
        setStorm(false);
        setThundering(false);
    }

    private void wakeUpAllPlayers(List<GlowPlayer> players) {
        wakeUpAllPlayers(players, false);
    }

    private void wakeUpAllPlayers(List<GlowPlayer> players, boolean sendTime) {
        for (GlowPlayer player : players) {
            if (sendTime) {
                player.sendTime();
            }
            if (player.isSleeping()) {
                player.leaveBed(true);
            }
        }
    }

    private boolean areAllPlayersSleeping(List<GlowPlayer> players) {
        for (GlowPlayer player : players) {
            if (!(player.isSleeping() && player.getSleepTicks() >= 100) && !player.isSleepingIgnored()) {
                return false;
            }
        }
        return true;
    }

    private void maybeStrikeLightningInChunk(int cx, int cz) {
        if (environment == Environment.NORMAL && currentlyRaining && currentlyThundering) {
            if (random.nextInt(100000) == 0) {
                strikeLightningInChunk(cx, cz);
            }
        }
    }

    private void strikeLightningInChunk(int cx, int cz) {
        int n = random.nextInt();
        // get lightning target block
        int x = (cx << 4) + (n & 0xF);
        int z = (cz << 4) + (n >> 8 & 0xF);
        int y = getHighestBlockYAt(x, z);

        // search for living entities in a 6×6×h (there's an error in the wiki!) region from 3 below the
        // target block up to the world height
        BoundingBox searchBox = BoundingBox.fromPositionAndSize(new Vector(x, y, z), new Vector(0, 0, 0));
        Vector vec = new Vector(3, 3, 3);
        Vector vec2 = new Vector(0, getMaxHeight(), 0);
        searchBox.minCorner.subtract(vec);
        searchBox.maxCorner.add(vec).add(vec2);
        List<LivingEntity> livingEntities = new LinkedList<>();
        // make sure entity can see sky
        getEntityManager().getEntitiesInside(searchBox, null).stream().filter(entity -> entity instanceof LivingEntity && !entity.isDead()).forEach(entity -> {
            Vector pos = entity.getLocation().toVector();
            int minY = getHighestBlockYAt(pos.getBlockX(), pos.getBlockZ());
            if (pos.getBlockY() >= minY) {
                livingEntities.add((LivingEntity) entity);
            }
        });

        // re-target lightning if required
        if (!livingEntities.isEmpty()) {
            // randomly choose an entity
            LivingEntity entity = livingEntities.get(random.nextInt(livingEntities.size()));
            // re-target lightning on this living entity
            Vector newTarget = entity.getLocation().toVector();
            x = newTarget.getBlockX();
            z = newTarget.getBlockZ();
            y = newTarget.getBlockY();
        }

        // lightning strike if the target block is under rain
        if (GlowBiomeClimate.isRainy(getBiome(x, z), x, y, z)) {
            strikeLightning(new Location(this, x, y, z));
        }
    }

    /**
     * Calculates how much the rays from the location to the entity's bounding box is blocked.
     *
     * @param location The location for the rays to start
     * @param entity   The entity that's bounding box is the ray's end point
     * @return a value between 0 and 1, where 0 = all rays blocked and 1 = all rays unblocked
     */
    public float rayTrace(Location location, GlowEntity entity) {
        // TODO: calculate how much of the entity is visible (not blocked by blocks) from the location
        /*
         * To calculate this step through the entity's bounding box and check whether the ray to the point
         * in the bounding box is blocked.
         *
         * Return (unblockedRays / allRays)
         */
        return RayUtil.getExposure(location, entity.getLocation());
    }

    /**
     * Gets the entity manager.
     *
     * @return The entity manager.
     */
    public EntityManager getEntityManager() {
        return entities;
    }

    public Collection<GlowPlayer> getRawPlayers() {
        return entities.getAll(GlowPlayer.class);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity lists

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(entities.getAll(GlowPlayer.class));
    }

    /**
     * Returns a list of entities within a bounding box centered around a Location.
     * <p>
     * Some implementations may impose artificial restrictions on the size of the search bounding box.
     *
     * @param location The center of the bounding box
     * @param x        1/2 the size of the box along x axis
     * @param y        1/2 the size of the box along y axis
     * @param z        1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        Vector minCorner = new Vector(location.getX() - x, location.getY() - y, location.getZ() - z);
        Vector maxCorner = new Vector(location.getX() + x, location.getY() + y, location.getZ() + z);
        BoundingBox searchBox = BoundingBox.fromCorners(minCorner, maxCorner); // TODO: test
        GlowEntity except = null;
        return entities.getEntitiesInside(searchBox, except);
    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>(entities.getAll());
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        return entities.getAll().stream().filter(e -> e instanceof GlowLivingEntity).map(e -> (GlowLivingEntity) e).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T>... classes) {
        return (Collection<T>) getEntitiesByClasses(classes);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls) {
        return entities.getAll().stream().filter(e -> cls.isAssignableFrom(e.getClass())).map(e -> (T) e).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        ArrayList<Entity> result = new ArrayList<>();
        for (Entity e : entities.getAll()) {
            for (Class<?> cls : classes) {
                if (cls.isAssignableFrom(e.getClass())) {
                    result.add(e);
                    break;
                }
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various malleable world properties

    @Override
    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        return setSpawnLocation(x, y, z, true);
    }

    public boolean setSpawnLocation(int x, int y, int z, boolean anchor) {
        Location oldSpawn = spawnLocation;
        Location newSpawn = new Location(this, x, y, z);
        if (newSpawn.equals(oldSpawn)) {
            return false;
        }
        spawnLocation = newSpawn;
        if (anchor) {
            setKeepSpawnInMemory(keepSpawnLoaded);
        }
        EventFactory.callEvent(new SpawnChangeEvent(this, oldSpawn));
        return true;
    }

    @Override
    public boolean getPVP() {
        return pvpAllowed;
    }

    @Override
    public void setPVP(boolean pvp) {
        pvpAllowed = pvp;
    }

    @Override
    public boolean getKeepSpawnInMemory() {
        return keepSpawnLoaded;
    }

    @Override
    public void setKeepSpawnInMemory(boolean keepLoaded) {
        keepSpawnLoaded = keepLoaded;
        boolean needSpawn = spawnLocation == null;

        if (needSpawn) {
            // find a spawn if needed
            Location spawn = generator.getFixedSpawnLocation(this, random);
            // we're already going to anchor if told to, so don't request another anchor
            if (spawn == null) {
                // determine a location randomly
                int spawnX = random.nextInt(256) - 128, spawnZ = random.nextInt(256) - 128;
                getChunkAt(spawnX >> 4, spawnZ >> 4).load(true);  // I'm not sure there's a sane way around this

                for (int tries = 0; tries < 1000 && !generator.canSpawn(this, spawnX, spawnZ); ++tries) {
                    spawnX += random.nextInt(256) - 128;
                    spawnZ += random.nextInt(256) - 128;
                }
                setSpawnLocation(spawnX, getHighestBlockYAt(spawnX, spawnZ), spawnZ);
                needSpawn = false;
            } else {
                setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ(), false);
            }
        }

        if (spawnChunkLock == null) {
            if (keepSpawnLoaded) {
                spawnChunkLock = newChunkLock("spawn");
                prepareSpawn();
            }
        } else {
            // update the chunk lock as needed
            spawnChunkLock.clear();
            if (keepSpawnLoaded) {
                prepareSpawn();
            } else {
                // attempt to immediately unload the spawn
                chunks.unloadOldChunks();
                spawnChunkLock = null;
            }
        }

        if (needSpawn) {
            setSpawnLocation(spawnLocation.getBlockX(), getHighestBlockYAt(spawnLocation.getBlockX(), spawnLocation.getBlockZ()), spawnLocation.getBlockZ(), false);
        }
    }

    private void prepareSpawn() {
        int centerX = spawnLocation.getBlockX() >> 4;
        int centerZ = spawnLocation.getBlockZ() >> 4;
        int radius = 4 * server.getViewDistance() / 3;

        long loadTime = System.currentTimeMillis();

        int total = ((radius << 1) + 1) * ((radius << 1) + 1), current = 0;

        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                ++current;
                if (populateAnchoredChunks) {
                    getChunkManager().forcePopulation(x, z);
                } else {
                    loadChunk(x, z);
                }
                spawnChunkLock.acquire(new Key(x, z));
                if (System.currentTimeMillis() >= loadTime + 1000) {
                    int progress = 100 * current / total;
                    GlowServer.logger.info("Preparing spawn for " + name + ": " + progress + "%");
                    loadTime = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public boolean isAutoSave() {
        return autosave;
    }

    @Override
    public void setAutoSave(boolean value) {
        autosave = value;
    }

    @Override
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        ServerDifficultyMessage message = new ServerDifficultyMessage(difficulty);
        for (GlowPlayer player : getRawPlayers()) {
            player.getSession().send(message);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity spawning properties

    @Override
    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        spawnMonsters = allowMonsters;
        spawnAnimals = allowAnimals;
    }

    @Override
    public boolean getAllowAnimals() {
        return spawnAnimals;
    }

    @Override
    public boolean getAllowMonsters() {
        return spawnMonsters;
    }

    @Override
    public long getTicksPerAnimalSpawns() {
        return ticksPerAnimal;
    }

    @Override
    public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns) {
        ticksPerAnimal = ticksPerAnimalSpawns;
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        return ticksPerMonster;
    }

    @Override
    public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns) {
        ticksPerMonster = ticksPerMonsterSpawns;
    }

    @Override
    public int getMonsterSpawnLimit() {
        return monsterLimit;
    }

    @Override
    public void setMonsterSpawnLimit(int limit) {
        monsterLimit = limit;
    }

    @Override
    public int getAnimalSpawnLimit() {
        return animalLimit;
    }

    @Override
    public void setAnimalSpawnLimit(int limit) {
        animalLimit = limit;
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return waterAnimalLimit;
    }

    @Override
    public void setWaterAnimalSpawnLimit(int limit) {
        waterAnimalLimit = limit;
    }

    @Override
    public int getAmbientSpawnLimit() {
        return ambientLimit;
    }

    @Override
    public void setAmbientSpawnLimit(int limit) {
        ambientLimit = limit;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various fixed world properties

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMaxHeight() {
        return maxBuildHeight;
    }

    @Override
    public int getSeaLevel() {
        if (worldType == WorldType.FLAT) {
            return 4;
        } else if (environment == Environment.THE_END) {
            return 50;
        } else {
            return seaLevel;
        }
    }

    @Override
    public boolean canGenerateStructures() {
        return generateStructures;
    }

    ////////////////////////////////////////////////////////////////////////////
    // force-save

    @Override
    public void save() {
        save(false);
    }

    public void save(boolean async) {
        EventFactory.callEvent(new WorldSaveEvent(this));

        // save metadata
        writeWorldData(async);

        // save chunks
        maybeAsync(async, () -> {
            for (GlowChunk chunk : chunks.getLoadedChunks()) {
                chunks.performSave(chunk);
            }
        });

        // save players
        for (GlowPlayer player : getRawPlayers()) {
            player.saveData(async);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // map generation

    @Override
    public ChunkGenerator getGenerator() {
        return chunks.getGenerator();
    }

    @Override
    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    @Override
    public boolean generateTree(Location location, TreeType type) {
        return generateTree(location, type, null);
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        BlockStateDelegate blockStateDelegate = new BlockStateDelegate();
        if (GlowTree.newInstance(type, random, loc, blockStateDelegate).generate()) {
            List<BlockState> blockStates = new ArrayList<>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent = new StructureGrowEvent(loc, type, false, null, blockStates);
            EventFactory.callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                for (BlockState state : blockStates) {
                    state.update(true);
                    if (delegate != null) {
                        delegate.setTypeIdAndData(state.getX(), state.getY(), state.getZ(), state.getTypeId(), state.getRawData());
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Map<Integer, GlowStructure> getStructures() {
        return structures;
    }

    ////////////////////////////////////////////////////////////////////////////
    // get block, chunk, id, highest methods with coords

    @Override
    public int getEntityCount() {
        return getEntities().size();
    }

    @Override
    @Deprecated
    public int getTileEntityCount() {
        return getBlockEntityCount();
    }

    public int getBlockEntityCount() {
        int length = 0;
        for (GlowChunk chunk : getChunkManager().getLoadedChunks()) {
            length += chunk.getBlockEntities().length;
        }
        return length;
    }

    @Override
    @Deprecated
    public int getTickableTileEntityCount() {
        return getTickableBlockEntityCount();
    }

    public int getTickableBlockEntityCount() {
        // TODO: distinguish between block entity types
        int length = 0;
        for (GlowChunk chunk : getChunkManager().getLoadedChunks()) {
            length += chunk.getBlockEntities().length;
        }
        return length;
    }

    @Override
    public int getChunkCount() {
        return getChunkManager().getLoadedChunks().length;
    }

    @Override
    public int getPlayerCount() {
        return getPlayers().size();
    }

    @Override
    public GlowBlock getBlockAt(int x, int y, int z) {
        return new GlowBlock(getChunkAt(x >> 4, z >> 4), x, y, z);
    }

    @Override
    public int getBlockTypeIdAt(int x, int y, int z) {
        return getChunkAt(x >> 4, z >> 4).getType(x & 0xF, z & 0xF, y);
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return getChunkAt(x >> 4, z >> 4).getHeight(x & 0xf, z & 0xf);
    }

    @Override
    public GlowChunk getChunkAt(int x, int z) {
        return chunks.getChunk(x, z);
    }

    ////////////////////////////////////////////////////////////////////////////
    // get block, chunk, id, highest with locations

    @Override
    public GlowBlock getBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public int getBlockTypeIdAt(Location location) {
        return getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public int getHighestBlockYAt(Location location) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public Block getHighestBlockAt(int x, int z) {
        return getBlockAt(x, getHighestBlockYAt(x, z), z);
    }

    @Override
    public Block getHighestBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), getHighestBlockYAt(location), location.getBlockZ());
    }

    public Block getHighestBlockAt(Location location, Material[] except) {
        Block block = getHighestBlockAt(location);
        List<Material> array = Arrays.asList(except);
        for (int i = 0; i < 6; i++) {
            block = block.getLocation().clone().subtract(0, i == 0 ? 0 : 1, 0).getBlock();
            if (block.getType() == Material.AIR || array.contains(block.getType())) {
                continue;
            }
            return block;
        }
        return getHighestBlockAt(location);
    }

    @Override
    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Override
    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX() >> 4, block.getZ() >> 4);
    }

    @Override
    public void getChunkAtAsync(int x, int z, ChunkLoadCallback cb) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(null, () -> cb.onLoad(chunks.getChunk(x, z)));
    }

    @Override
    public void getChunkAtAsync(Location location, ChunkLoadCallback cb) {
        getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4, cb);
    }

    @Override
    public void getChunkAtAsync(Block block, ChunkLoadCallback cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, cb);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Chunk loading and unloading

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        return chunk.isLoaded();
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return chunks.isChunkLoaded(x, z);
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        return chunks.isChunkInUse(x, z);
    }

    @Override
    public Chunk[] getLoadedChunks() {
        return chunks.getLoadedChunks();
    }

    @Override
    public void loadChunk(Chunk chunk) {
        chunk.load();
    }

    @Override
    public void loadChunk(int x, int z) {
        getChunkAtAsync(x, z, Chunk::load);
    }

    @Override
    public boolean loadChunk(int x, int z, boolean generate) {
        return getChunkAt(x, z).load(generate);
    }

    @Override
    public boolean unloadChunk(Chunk chunk) {
        return chunk.unload();
    }

    @Override
    public boolean unloadChunk(int x, int z) {
        return unloadChunk(x, z, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save) {
        return unloadChunk(x, z, save, true);
    }

    @Override
    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        return !isChunkLoaded(x, z) || getChunkAt(x, z).unload(save, safe);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        return unloadChunkRequest(x, z, true);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if (safe && isChunkInUse(x, z)) return false;

        server.getScheduler().runTask(null, () -> unloadChunk(x, z, safe));

        return true;
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        if (!chunks.forceRegeneration(x, z)) return false;
        refreshChunk(x, z);
        return true;
    }

    @Override
    public boolean refreshChunk(int x, int z) {
        if (!isChunkLoaded(x, z)) {
            return false;
        }

        Key key = new Key(x, z);
        boolean result = false;

        for (GlowPlayer player : getRawPlayers()) {
            if (player.canSeeChunk(key)) {
                player.getSession().send(getChunkAt(x, z).toMessage());
                result = true;
            }
        }

        return result;
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return new EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Biomes

    @Override
    public Biome getBiome(int x, int z) {
        if (environment == Environment.THE_END) {
            return Biome.SKY;
        } else if (environment == Environment.NETHER) {
            return Biome.HELL;
        }

        return GlowBiome.getBiome(getChunkAt(x >> 4, z >> 4).getBiome(x & 0xF, z & 0xF));
    }

    @Override
    public void setBiome(int x, int z, Biome bio) {
        getChunkAtAsync(x >> 4, z >> 4, chunk -> ((GlowChunk) chunk).setBiome(x & 0xF, z & 0xF, GlowBiome.getId(bio)));
    }

    @Override
    public double getTemperature(int x, int z) {
        return GlowBiomeClimate.getBiomeTemperature(getBiome(x, z));
    }

    @Override
    public double getHumidity(int x, int z) {
        return GlowBiomeClimate.getBiomeHumidity(getBiome(x, z));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity spawning

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        return (T) spawn(location, EntityRegistry.getEntity(clazz), SpawnReason.CUSTOM);
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function) throws IllegalArgumentException {
        return null; // TODO: work on type mismatches
    }

    public GlowEntity spawn(Location location, Class<? extends GlowEntity> clazz, SpawnReason reason) throws IllegalArgumentException {
        GlowEntity entity = null;

        if (TNTPrimed.class.isAssignableFrom(clazz)) {
            entity = new GlowTNTPrimed(location, null);
        }

        if (entity == null) {
            try {
                Constructor<? extends GlowEntity> constructor = clazz.getConstructor(Location.class);
                entity = constructor.newInstance(location);
                GlowEntity impl = entity;
                // function.accept(entity); TODO: work on type mismatches
                EntitySpawnEvent spawnEvent;
                if (entity instanceof LivingEntity) {
                    spawnEvent = EventFactory.callEvent(new CreatureSpawnEvent((LivingEntity) entity, reason));
                } else {
                    spawnEvent = EventFactory.callEvent(new EntitySpawnEvent(entity));
                }
                if (!spawnEvent.isCancelled()) {
                    List<Message> spawnMessage = entity.createSpawnMessage();
                    getRawPlayers().stream().filter(player -> player.canSeeEntity(impl)).forEach(player -> player.getSession().sendAll(spawnMessage.toArray(new Message[spawnMessage.size()])));
                } else {
                    // TODO: separate spawning and construction for better event cancellation
                    entity.remove();
                }
            } catch (NoSuchMethodException e) {
                GlowServer.logger.log(Level.WARNING, "Invalid entity spawn: ", e);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                GlowServer.logger.log(Level.SEVERE, "Unable to spawn entity: ", e);
            }
        }

        if (entity != null) {
            return entity;
        }

        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GlowItem dropItem(Location location, ItemStack item) {
        return new GlowItem(location, item);
    }

    @Override
    public GlowItem dropItemNaturally(Location location, ItemStack item) {
        double xs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        location = location.clone().add(xs, ys, zs);
        GlowItem dropItem = new GlowItem(location, item);
        dropItem.setVelocity(new Vector(0, 0.01F, 0));
        return dropItem;
    }

    @Override
    public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
        Arrow arrow = spawn(location, Arrow.class);

        // Transformative magic
        Vector randVec = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
        randVec.multiply(0.0075 * spread);

        velocity.normalize();
        velocity.add(randVec);
        velocity.multiply(speed);

        // yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
        // pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D

        arrow.setVelocity(velocity);
        return arrow;
    }

    @Override
    public <T extends Arrow> T spawnArrow(Location location, Vector vector, float v, float v1, Class<T> aClass) {
        return null;
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException {
        return spawnFallingBlock(location, data.getItemType(), data.getData());
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
        if (location == null || material == null) {
            throw new IllegalArgumentException();
        }
        return new GlowFallingBlock(location, material, data);
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
        Material material = Material.getMaterial(blockId);
        return spawnFallingBlock(location, material, blockData);
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType type) {
        return spawn(loc, type.getEntityClass());
    }

    private GlowLightningStrike strikeLightningFireEvent(Location loc, boolean effect, boolean isSilent) {
        GlowLightningStrike strike = new GlowLightningStrike(loc, effect, isSilent, random);
        LightningStrikeEvent event = new LightningStrikeEvent(this, strike);
        if (EventFactory.callEvent(event).isCancelled()) {
            return null;
        }
        return strike;
    }

    @Override
    public GlowLightningStrike strikeLightning(Location loc) {
        return strikeLightningFireEvent(loc, false, false);
    }

    @Override
    public GlowLightningStrike strikeLightningEffect(Location loc) {
        return strikeLightningFireEvent(loc, true, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Time

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = (time % DAY_LENGTH + DAY_LENGTH) % DAY_LENGTH;

        getRawPlayers().forEach(GlowPlayer::sendTime);
    }

    @Override
    public long getFullTime() {
        return worldAge;
    }

    @Override
    public void setFullTime(long time) {
        worldAge = time;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Weather

    @Override
    public boolean hasStorm() {
        return currentlyRaining;
    }

    @Override
    public void setStorm(boolean hasStorm) {
        // call event
        WeatherChangeEvent event = new WeatherChangeEvent(this, hasStorm);
        if (EventFactory.callEvent(event).isCancelled()) {
            return;
        }

        // change weather
        boolean previouslyRaining = currentlyRaining;
        currentlyRaining = hasStorm;

        // Numbers borrowed from CraftBukkit.
        if (currentlyRaining) {
            setWeatherDuration(random.nextInt(HALF_DAY_IN_TICKS) + HALF_DAY_IN_TICKS);
        } else {
            setWeatherDuration(random.nextInt(WEEK_IN_TICKS) + HALF_DAY_IN_TICKS);
        }

        // update players
        if (previouslyRaining != currentlyRaining) {
            getRawPlayers().forEach(GlowPlayer::sendWeather);
        }
    }

    @Override
    public int getWeatherDuration() {
        return rainingTicks;
    }

    @Override
    public void setWeatherDuration(int duration) {
        rainingTicks = duration;
    }

    @Override
    public boolean isThundering() {
        return currentlyThundering;
    }

    @Override
    public void setThundering(boolean thundering) {
        // call event
        ThunderChangeEvent event = new ThunderChangeEvent(this, thundering);
        if (EventFactory.callEvent(event).isCancelled()) {
            return;
        }

        // change weather
        currentlyThundering = thundering;

        // Numbers borrowed from CraftBukkit.
        if (currentlyThundering) {
            setThunderDuration(random.nextInt(HALF_DAY_IN_TICKS) + 180 * TICKS_PER_SECOND);
        } else {
            setThunderDuration(random.nextInt(WEEK_IN_TICKS) + HALF_DAY_IN_TICKS);
        }
    }

    @Override
    public int getThunderDuration() {
        return thunderingTicks;
    }

    @Override
    public void setThunderDuration(int duration) {
        thunderingTicks = duration;
    }

    public float getRainDensity() {
        return currentRainDensity;
    }

    public float getSkyDarkness() {
        return currentSkyDarkness;
    }

    private void updateWeather() {
        float previousRainDensity = currentRainDensity;
        float previousSkyDarkness = currentSkyDarkness;
        float rainDensityModifier = currentlyRaining ? .01F : -.01F;
        float skyDarknessModifier = currentlyThundering ? .01F : -.01F;
        currentRainDensity = Math.max(0, Math.min(1, previousRainDensity + rainDensityModifier));
        currentSkyDarkness = Math.max(0, Math.min(1, previousSkyDarkness + skyDarknessModifier));

        if (previousRainDensity != currentRainDensity) {
            getRawPlayers().forEach(GlowPlayer::sendRainDensity);
        }

        if (previousSkyDarkness != currentSkyDarkness) {
            getRawPlayers().forEach(GlowPlayer::sendSkyDarkness);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Explosions

    @Override
    public boolean createExplosion(Location loc, float power) {
        return createExplosion(loc, power, false);
    }

    @Override
    public boolean createExplosion(Location loc, float power, boolean setFire) {
        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power) {
        return createExplosion(x, y, z, power, false, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        return createExplosion(x, y, z, power, setFire, true);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(null, x, y, z, power, setFire, breakBlocks);
    }

    /**
     * Create an explosion with a specific entity as the source.
     *
     * @param source      The entity to treat as the source, or null.
     * @param x           X coordinate
     * @param y           Y coordinate
     * @param z           Z coordinate
     * @param power       The power of explosion, where 4F is TNT
     * @param incendiary  Whether or not to set blocks on fire
     * @param breakBlocks Whether or not to have blocks be destroyed
     * @return false if explosion was canceled, otherwise true
     */
    public boolean createExplosion(Entity source, double x, double y, double z, float power, boolean incendiary, boolean breakBlocks) {
        Explosion explosion = new Explosion(source, this, x, y, z, power, incendiary, breakBlocks);
        return explosion.explodeWithEvent();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Effects

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data, int radius) {
        int radiusSquared = radius * radius;
        getRawPlayers().stream().filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared).forEach(player -> player.playEffect(location, effect, data));
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data, int radius) {
        playEffect(location, effect, GlowEffect.getDataValue(effect, data), radius);
    }

    public void playEffectExceptTo(Location location, Effect effect, int data, int radius, Player exclude) {
        int radiusSquared = radius * radius;
        getRawPlayers().stream().filter(player -> !player.equals(exclude) && player.getLocation().distanceSquared(location) <= radiusSquared).forEach(player -> player.playEffect(location, effect, data));
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
    public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch) {
        if (location == null || sound == null) return;

        double radiusSquared = Math.pow(volume * 16, 2);
        getRawPlayers().stream().filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared).forEach(player -> player.playSound(location, sound, category, volume, pitch));
    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch) {
        playSound(location, GlowSound.getVanillaSound(sound), category, volume, pitch);
    }

    @Override
    public Spigot spigot() {
        return spigot;
    }

    //@Override
    public void showParticle(Location loc, Effect particle, float offsetX, float offsetY, float offsetZ, float speed, int amount) {
        int radius;
        if (GlowParticle.isLongDistance(particle)) {
            radius = 48;
        } else {
            radius = 16;
        }

        showParticle(loc, particle, particle.getId(), 0, offsetX, offsetY, offsetZ, speed, amount, radius);
    }

    //@Override
    public void showParticle(Location loc, Effect particle, int id, int data, float offsetX, float offsetY, float offsetZ, float speed, int amount, int radius) {
        if (loc == null || particle == null) return;

        double radiusSquared = radius * radius;


        getRawPlayers().stream().filter(player -> player.getLocation().distanceSquared(loc) <= radiusSquared).forEach(player -> player.spigot().playEffect(loc, particle, id, data, offsetX, offsetY, offsetZ, speed, amount, radius));
    }

    /**
     * Save the world data using the metadata service.
     *
     * @param async Whether to write asynchronously.
     */
    private void writeWorldData(boolean async) {
        maybeAsync(async, () -> {
            try {
                storageProvider.getMetadataService().writeWorldData();
                storageProvider.getScoreboardIoService().save();
            } catch (IOException e) {
                server.getLogger().severe("Could not save metadata for world: " + getName());
                e.printStackTrace();
            }

            storageProvider.getStructureDataService().writeStructuresData(structures);
        });
    }

    ////////////////////////////////////////////////////////////////////////////
    // Level data write

    /**
     * Execute a runnable, optionally asynchronously.
     *
     * @param async    Whether to run the runnable in an asynchronous task.
     * @param runnable The runnable to run.
     */
    private void maybeAsync(boolean async, Runnable runnable) {
        if (async) {
            server.getScheduler().runTaskAsynchronously(null, runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Unloads the world
     *
     * @return true if successful
     */
    public boolean unload() {
        // terminate task service
        //aiTaskService.shutdown();
        if (EventFactory.callEvent(new WorldUnloadEvent(this)).isCancelled()) {
            return false;
        }
        try {
            storageProvider.getChunkIoService().unload();
            storageProvider.getScoreboardIoService().unload();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the storage provider for the world.
     *
     * @return The {@link WorldStorageProvider}.
     */
    public WorldStorageProvider getStorage() {
        return storageProvider;
    }

    /**
     * Get the world folder.
     *
     * @return world folder
     */
    @Override
    public File getWorldFolder() {
        return storageProvider.getFolder();
    }

    @Override
    public String[] getGameRules() {
        return gameRules.getKeys();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Game rules

    @Override
    public String getGameRuleValue(String rule) {
        return gameRules.getString(rule);
    }

    @Override
    public boolean setGameRuleValue(String rule, String value) {
        if (!gameRules.setValue(rule, value)) {
            return false;
        }
        if (rule.equals("doDaylightCycle")) {
            // inform clients about the daylight cycle change
            getRawPlayers().forEach(GlowPlayer::sendTime);
        } else if (rule.equals("reducedDebugInfo")) {
            // inform clients about the debug info change
            EntityStatusMessage message = new EntityStatusMessage(0, gameRules.getBoolean("reducedDebugInfo") ? EntityStatusMessage.ENABLE_REDUCED_DEBUG_INFO : EntityStatusMessage.DISABLE_REDUCED_DEBUG_INFO);
            for (GlowPlayer player : getRawPlayers()) {
                player.getSession().send(message);
            }
        }
        return true;
    }

    @Override
    public boolean isGameRule(String rule) {
        return gameRules.isGameRule(rule);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return worldBorder;
    }

    public Map<String, CommandFunction> getFunctions() {
        return functions;
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        spawnParticle(particle, x, y, z, count, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        if (particle == null) {
            throw new IllegalArgumentException("particle cannot be null!");
        }

        if (data != null && !particle.getDataType().isInstance(data)) {
            throw new IllegalArgumentException("wrong data type " + data.getClass() + " should be " + particle.getDataType());
        }

        for (GlowPlayer player : getRawPlayers()) {
            if (!player.getWorld().equals(this)) {
                continue;
            }
            player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
        }
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(particle, new Location(this, x, y, z), count, offsetX, offsetY, offsetZ, extra, data);
    }

    public GameRuleManager getGameRuleMap() {
        return gameRules;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        metadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return metadata.getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return metadata.hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        metadata.removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        for (Player player : getRawPlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public Set<String> getListeningPluginChannels() {
        HashSet<String> result = new HashSet<>();
        for (Player player : getRawPlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }

    private void pulseTickMap() {
        ItemTable itemTable = ItemTable.instance();
        for (Location location : getTickMap()) {
            if (!location.getChunk().isLoaded()) {
                continue;
            }
            BlockType type = itemTable.getBlock(Material.getMaterial(getBlockTypeIdAt(location)));
            if (type == null) {
                cancelPulse(location);
                continue;
            }
            GlowBlock block = (GlowBlock) location.getBlock();
            Integer speed = type.getPulseTickSpeed(block);
            boolean once = type.isPulseOnce(block);
            if (speed == 0) {
                continue;
            }
            if (worldAge % speed == 0) {
                type.receivePulse(block);
                if (once) {
                    cancelPulse(location);
                }
            }
        }
    }

    public ConcurrentSet<Location> getTickMap() {
        return tickMap;
    }

    public void requestPulse(GlowBlock block) {
        requestPulse(block.getLocation());
    }

    public void requestPulse(Location location) {
        tickMap.add(location);
    }

    public void cancelPulse(GlowBlock block) {
        cancelPulse(block.getLocation());
    }

    public void cancelPulse(Location location) {
        tickMap.remove(location);
    }

    @Override
    public int hashCode() {
        return getUID().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    /**
     * The metadata store class for worlds.
     */
    private static final class WorldMetadataStore extends MetadataStoreBase<World> implements MetadataStore<World> {
        @Override
        protected String disambiguate(World subject, String metadataKey) {
            return subject.getName() + ":" + metadataKey;
        }
    }
}
