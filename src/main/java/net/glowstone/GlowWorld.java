package net.glowstone;

import com.destroystokyo.paper.HeightmapType;
import com.flowpowered.network.Message;
import io.papermc.paper.world.MoonPhase;
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
import net.glowstone.constants.GameRules;
import net.glowstone.constants.GlowBiome;
import net.glowstone.constants.GlowBiomeClimate;
import net.glowstone.constants.GlowEffect;
import net.glowstone.constants.GlowParticle;
import net.glowstone.constants.GlowSound;
import net.glowstone.constants.GlowTree;
import net.glowstone.data.CommandFunction;
import net.glowstone.entity.CustomEntityDescriptor;
import net.glowstone.entity.EntityManager;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowLightningStrike;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.entity.physics.BoundingBox;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.io.WorldMetadataService.WorldFinalValues;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.net.message.play.entity.EntityStatusMessage;
import net.glowstone.net.message.play.game.BlockChangeMessage;
import net.glowstone.net.message.play.game.ChunkDataMessage;
import net.glowstone.net.message.play.player.ServerDifficultyMessage;
import net.glowstone.util.BlockStateDelegate;
import net.glowstone.util.GameRuleManager;
import net.glowstone.util.RayUtil;
import net.glowstone.util.TickUtil;
import net.glowstone.util.collection.ConcurrentSet;
import net.glowstone.util.config.WorldConfig;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameEvent;
import org.bukkit.GameRule;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Raid;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.StructureType;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.StructureSearchResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A class which represents the in-game world.
 *
 * @author Graham Edgecombe
 */
@ToString(of = "name")
public class GlowWorld implements World {

    /**
     * The metadata store for world objects.
     */
    private static final MetadataStore<World> metadata = new WorldMetadataStore();
    /**
     * The length in ticks between autosaves (5 minutes).
     */
    private static final int AUTOSAVE_TIME = TickUtil.minutesToTicks(5);
    /**
     * The maximum height of ocean water.
     */
    private static int seaLevel;
    /**
     * Get the world's parent server.
     *
     * @return The GlowServer for the world.
     */
    @Getter
    private final GlowServer server;
    /**
     * The name of this world.
     */
    @Getter
    private final String name;
    /**
     * The chunk manager.
     *
     * @return The ChunkManager for the world.
     */
    @Getter
    private final ChunkManager chunkManager;
    /**
     * The storage provider for the world.
     *
     * @return The {@link WorldStorageProvider}.
     */
    @Getter
    private final WorldStorageProvider storage;
    /**
     * The world's UUID.
     */
    private final UUID uid;
    /**
     * The entity manager.
     *
     * @return the entity manager
     */
    @Getter
    private final EntityManager entityManager = new EntityManager();
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
    @Getter
    private final GameRuleManager gameRuleMap = new GameRuleManager();
    /**
     * The environment.
     */
    @Getter
    private final Environment environment;
    /**
     * Whether structure generation is enabled.
     */
    private final boolean generateStructures;
    /**
     * The world seed.
     */
    @Getter
    private final long seed;
    /**
     * The SHA-256 hash of the world seed.
     */
    @Getter
    private final byte[] seedHash;
    /**
     * Contains how regular blocks should be pulsed.
     */
    private final ConcurrentSet<Location> tickMap = new ConcurrentSet<>();
    private final Spigot spigot = new Spigot() {
        @Override
        public LightningStrike strikeLightning(Location loc, boolean isSilent) {
            return strikeLightningFireEvent(loc, false, isSilent);
        }

        @Override
        public LightningStrike strikeLightningEffect(Location loc, boolean isSilent) {
            return strikeLightningFireEvent(loc, true, isSilent);
        }
    };
    /*/**
     * The ScheduledExecutorService the for entity AI tasks threading.
     */
    //private final ScheduledExecutorService aiTaskService;
    /**
     * The world border.
     */
    @Getter
    private final GlowWorldBorder worldBorder;
    /**
     * The functions for this world.
     */
    private final Map<String, CommandFunction> functions;
    /**
     * A lock kept on the spawn chunkManager.
     */
    private ChunkLock spawnChunkLock;
    /**
     * The world type.
     */
    @Getter
    @Setter
    private WorldType worldType;
    /**
     * The spawn position.
     */
    private Location spawnLocation;
    /**
     * Whether to keep the spawn chunkManager in memory (prevent them from being unloaded).
     */
    private boolean keepSpawnLoaded = true;
    /**
     * Whether to populate chunkManager when they are anchored.
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
    @Getter
    @Setter
    private int weatherDuration;
    /**
     * Whether it is currently thundering on this world.
     */
    @Getter
    private boolean thundering = true;
    /**
     * How many ticks until the thundering status is expected to change.
     */
    @Getter
    @Setter
    private int thunderDuration;
    /**
     * The rain density on the current world tick.
     */
    @Getter
    private float rainDensity;
    /**
     * The sky darkness on the current world tick.
     */
    @Getter
    private float skyDarkness;
    /**
     * The age of the world, in ticks.
     */
    @Getter
    @Setter
    private long fullTime;
    /**
     * The current world time.
     */
    @Getter
    private long time;
    /**
     * The time until the next full-save.
     */
    private int saveTimer = AUTOSAVE_TIME;
    /**
     * The check to autosave.
     */
    @Getter
    @Setter
    private boolean autoSave = true;
    /**
     * The world's gameplay difficulty.
     */
    @Getter
    private Difficulty difficulty;
    /**
     * Ticks between when passive mobs are spawned.
     */
    @Setter
    private int ticksPerAnimalSpawns;
    /**
     * Ticks between when hostile mobs are spawned.
     */
    @Setter
    private int ticksPerMonsterSpawns;
    /**
     * Ticks between when water mobs are spawned.
     */
    @Setter
    private int ticksPerWaterSpawns;
    /**
     * Ticks between when ambient water mobs are spawned.
     */
    @Setter
    private int ticksPerWaterAmbientSpawns;
    /**
     * Ticks between when ambient mobs are spawned.
     */
    @Setter
    private int ticksPerAmbientSpawns;
    /**
     * Per-world spawn limits on hostile mobs.
     */
    @Getter
    @Setter
    private int monsterSpawnLimit;
    /**
     * Per-world spawn limits on passive mobs.
     */
    @Getter
    @Setter
    private int animalSpawnLimit;
    /**
     * Per-world spawn limits on water mobs.
     */
    @Getter
    @Setter
    private int waterAnimalSpawnLimit;
    /**
     * Per-world spawn limits on water ambient mobs.
     */
    @Getter
    @Setter
    private int waterAmbientSpawnLimit;
    /**
     * Per-world spawn limits on ambient mobs (bats).
     */
    @Getter
    @Setter
    private int ambientSpawnLimit;
    private Map<Integer, GlowStructure> structures;
    /**
     * The maximum height at which players may place blocks.
     */
    @Getter
    private int maxHeight;
    private Set<Key> activeChunksSet = new HashSet<>();
    /**
     * Whether the world has been initialized (i.e. loading/spawn generation is completed).
     */
    @Getter
    private boolean initialized;
    /**
     * Per-world view distance.
     */
    @Getter
    @Setter
    private int viewDistance;
    /**
     * Per-world hardcore setting.
     */
    @Getter
    @Setter
    private boolean hardcore;

    /**
     * Creates a new world from the options in the given WorldCreator.
     *
     * @param server               The server for the world.
     * @param creator              The WorldCreator to use.
     * @param worldStorageProvider The storage provider to use.
     */
    public GlowWorld(GlowServer server, WorldCreator creator,
                     WorldStorageProvider worldStorageProvider) {
        this.server = server;

        // set up values from WorldCreator
        name = creator.name();
        environment = creator.environment();
        worldType = creator.type();
        generateStructures = creator.generateStructures();

        generator = creator.generator();

        storage = worldStorageProvider;
        storage.setWorld(this);
        populators = generator.getDefaultPopulators(this);

        // set up values from server defaults
        ticksPerAnimalSpawns = server.getTicksPerAnimalSpawns();
        ticksPerMonsterSpawns = server.getTicksPerMonsterSpawns();
        ticksPerAmbientSpawns = server.getTicksPerAmbientSpawns();
        ticksPerWaterAmbientSpawns = server.getTicksPerWaterAmbientSpawns();
        ticksPerWaterSpawns = server.getTicksPerWaterSpawns();
        monsterSpawnLimit = server.getMonsterSpawnLimit();
        animalSpawnLimit = server.getAnimalSpawnLimit();
        waterAnimalSpawnLimit = server.getWaterAnimalSpawnLimit();
        ambientSpawnLimit = server.getAmbientSpawnLimit();
        waterAmbientSpawnLimit = server.getWaterAnimalSpawnLimit();
        keepSpawnLoaded = server.keepSpawnLoaded();
        populateAnchoredChunks = server.populateAnchoredChunks() && !server.isGenerationDisabled();
        difficulty = server.getDifficulty();
        maxHeight = server.getMaxBuildHeight();
        seaLevel = GlowServer.getWorldConfig().getInt(WorldConfig.Key.SEA_LEVEL);
        worldBorder = new GlowWorldBorder(this);
        viewDistance = server.getViewDistance();
        hardcore = server.isHardcore();

        // read in world data
        WorldFinalValues values;
        values = storage.getMetadataService().readWorldData();
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

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (digest != null) {
            seedHash = digest.digest(String.valueOf(seed).getBytes(StandardCharsets.UTF_8));
        } else {
            seedHash = new byte[32];
        }

        chunkManager = new ChunkManager(this, storage.getChunkIoService(), generator);
        structures = storage.getStructureDataService().readStructuresData();
        functions = storage.getFunctionIoService().readFunctions().stream()
            .collect(Collectors.toMap(CommandFunction::getFullName, function -> function));
        server.addWorld(this);
        server.getLogger().info("Preparing spawn for " + name + "...");
        EventFactory.getInstance().callEvent(new WorldInitEvent(this));

        spawnChunkLock = keepSpawnLoaded ? newChunkLock("spawn") : null;

        setKeepSpawnInMemory(keepSpawnLoaded);

        server.getLogger().info("Preparing spawn for " + name + ": done");
        initialized = true;
        EventFactory.getInstance().callEvent(new WorldLoadEvent(this));

        // pulse AI tasks
        //aiTaskService = Executors.newScheduledThreadPool(1);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various internal mechanisms

    /**
     * Get a new chunk lock object a player or other party can use to keep chunkManager loaded.
     *
     * @param desc A description for this chunk lock.
     * @return The ChunkLock.
     */
    public ChunkLock newChunkLock(@NonNls String desc) {
        return new ChunkLock(chunkManager, name + ": " + desc);
    }

    /**
     * Updates all the entities within this world.
     */
    public void pulse() {
        List<GlowEntity> allEntities = new ArrayList<>(entityManager.getAll());
        List<GlowPlayer> players = new LinkedList<>();

        activeChunksSet.clear();

        // We should pulse our tickmap, so blocks get updated.
        pulseTickMap();

        // pulse players last so they actually see that other entities have
        // moved. unfortunately pretty hacky. not a problem for players b/c
        // their position is modified by session ticking.
        for (GlowEntity entity : entityManager) {
            if (entity instanceof GlowPlayer) {
                players.add((GlowPlayer) entity);
                updateActiveChunkCollection(entity);
            } else {
                entity.pulse();
            }
        }

        updateBlocksInActiveChunks();
        // why update blocks before Players or Entities? if there is a specific reason we should
        // document it here.

        pulsePlayers(players);
        chunkManager.clearChunkBlockChanges();
        resetEntities(allEntities);
        worldBorder.pulse();

        updateWorldTime();
        informPlayersOfTime();
        updateOverworldWeather();

        handleSleepAndWake(players);

        saveWorld();
    }

    private void updateActiveChunkCollection(GlowEntity entity) {
        // build a set of chunkManager around each player in this world, the
        // server view distance is taken here
        int radius = server.getViewDistance();
        Location playerLocation = entity.getLocation();
        if (playerLocation.getWorld() == this) {
            int cx = playerLocation.getBlockX() >> 4;
            int cz = playerLocation.getBlockZ() >> 4;
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    if (isChunkLoaded(cx, cz)) {
                        activeChunksSet.add(GlowChunk.Key.of(x, z));
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

                // chunk tick
                chunk.addTick();

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
                int n = ThreadLocalRandom.current().nextInt();
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
            chunkManager.unloadOldChunks();
            if (autoSave) {
                save(true);
            }
        }
    }

    private void updateOverworldWeather() {
        // only tick weather in a NORMAL world
        if (environment == Environment.NORMAL) {
            if (--weatherDuration <= 0) {
                setStorm(!currentlyRaining);
            }

            if (--thunderDuration <= 0) {
                setThundering(!thundering);
            }

            updateWeather();
        }
    }

    private void informPlayersOfTime() {
        if (fullTime % (30 * TickUtil.TICKS_PER_SECOND) == 0) {
            // Only send the time every 30 seconds; clients are smart.
            getRawPlayers().forEach(GlowPlayer::sendTime);
        }
    }

    // Tick the world age and time of day
    private void updateWorldTime() {
        fullTime++;
        // fullTime is used to determine when to (periodically) update clients of server time
        // (time of day - "time")
        // also used to occasionally pulse some blocks (see "tickMap" and "requestPulse()")

        // Modulus by 24000, the tick length of a day
        if (gameRuleMap.getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            time = (time + 1) % TickUtil.TICKS_PER_DAY;
        }
    }

    private void resetEntities(List<GlowEntity> entities) {
        entities.forEach(GlowEntity::reset);
    }

    private void pulsePlayers(List<GlowPlayer> players) {
        for (GlowPlayer player : players) {
            player.pulse();
        }
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
                boolean skipNight = gameRuleMap.getBoolean(GameRules.DO_DAYLIGHT_CYCLE)
                    && areAllPlayersSleeping(players);
                // check gamerule before iterating players (micro-optimization)
                if (skipNight) {
                    skipRestOfNight(players);
                }
            }
        }
    }

    private void skipRestOfNight(List<GlowPlayer> players) {
        fullTime = (fullTime / TickUtil.TICKS_PER_DAY + 1) * TickUtil.TICKS_PER_DAY;
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
            if (!(player.isSleeping() && player.getSleepTicks() >= 100) && !player
                .isSleepingIgnored()) {
                return false;
            }
        }
        return true;
    }

    public void broadcastBlockChangeInRange(GlowChunk.Key chunkKey, BlockChangeMessage message) {
        getChunkManager().broadcastBlockChange(chunkKey, message);
    }

    private void maybeStrikeLightningInChunk(int cx, int cz) {
        if (environment == Environment.NORMAL && currentlyRaining && thundering) {
            if (ThreadLocalRandom.current().nextInt(100000) == 0) {
                strikeLightningInChunk(cx, cz);
            }
        }
    }

    private void strikeLightningInChunk(int cx, int cz) {
        int n = ThreadLocalRandom.current().nextInt();
        // get lightning target block
        int x = (cx << 4) + (n & 0xF);
        int z = (cz << 4) + (n >> 8 & 0xF);
        int y = getHighestBlockYAt(x, z);

        // search for living entities in a 6×6×h (there's an error in the wiki!) region from 3
        // below the
        // target block up to the world height
        BoundingBox searchBox = BoundingBox
            .fromPositionAndSize(new Vector(x, y, z), new Vector(0, 0, 0));
        Vector vec = new Vector(3, 3, 3);
        Vector vec2 = new Vector(0, getMaxHeight(), 0);
        searchBox.minCorner.subtract(vec);
        searchBox.maxCorner.add(vec).add(vec2);
        List<LivingEntity> livingEntities = new LinkedList<>();
        // make sure entity can see sky
        getEntityManager().getEntitiesInside(searchBox, null).stream()
            .filter(entity -> entity instanceof LivingEntity && !entity.isDead())
            .forEach(entity -> {
                Vector pos = entity.getLocation().toVector();
                int minY = getHighestBlockYAt(pos.getBlockX(), pos.getBlockZ());
                if (pos.getBlockY() >= minY) {
                    livingEntities.add((LivingEntity) entity);
                }
            });

        // re-target lightning if required
        if (!livingEntities.isEmpty()) {
            // randomly choose an entity
            LivingEntity entity = livingEntities
                .get(ThreadLocalRandom.current().nextInt(livingEntities.size()));
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
        // TODO: calculate how much of the entity is visible (not blocked by blocks) from the
        // location
        /*
         * To calculate this step through the entity's bounding box and check whether the ray to
         * the point
         * in the bounding box is blocked.
         *
         * Return (unblockedRays / allRays)
         */
        return RayUtil.getExposure(location, entity.getLocation());
    }

    @Override
    public @Nullable RayTraceResult rayTrace(@NotNull Location location, @NotNull Vector vector,
            double v, @NotNull FluidCollisionMode fluidCollisionMode, boolean b, double v1,
            @Nullable Predicate<Entity> predicate) {
        return null; // TODO
    }

    @Override
    public MoonPhase getMoonPhase() {
        long actualPhase = (fullTime / TickUtil.TICKS_PER_DAY) % 8;
        return MoonPhase.getPhase(actualPhase);
    }

    /**
     * Returns the fraction of the moon that is illuminated, ranging from 0.0 at new moon to 1.0 at
     * full moon. Always a multiple of 0.25. See
     * <a href="https://minecraft.gamepedia.com/Moon#Phases">Moon Phases</a> at Gamepedia.
     *
     * @return the fraction of the moon that is illuminated
     */
    public double getMoonPhaseFraction() {
        MoonPhase phase = getMoonPhase();
        return 0.25 * Math.abs(phase.ordinal() - 4);
    }

    public Collection<GlowPlayer> getRawPlayers() {
        return entityManager.getAll(GlowPlayer.class);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity lists

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(entityManager.getAll(GlowPlayer.class));
    }

    @Override
    public Entity getEntity(UUID uuid) {
        for (Entity entity : getEntities()) {
            if (entity.getUniqueId().equals(uuid)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Returns a list of entities within a bounding box centered around a Location.
     *
     * <p>Some implementations may impose artificial restrictions on the size of the search bounding
     * box.
     *
     * @param location The center of the bounding box
     * @param x        1/2 the size of the box along x axis
     * @param y        1/2 the size of the box along y axis
     * @param z        1/2 the size of the box along z axis
     * @return the collection of entities near location. This will always be a non-null collection.
     */
    @Override
    public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z) {
        Vector minCorner = new Vector(
                location.getX() - x, location.getY() - y, location.getZ() - z);
        Vector maxCorner = new Vector(
                location.getX() + x, location.getY() + y, location.getZ() + z);
        BoundingBox searchBox = BoundingBox.fromCorners(minCorner, maxCorner); // TODO: test
        GlowEntity except = null;
        return entityManager.getEntitiesInside(searchBox, except);
    }

    @Override
    public @NotNull Collection<Entity> getNearbyEntities(@NotNull Location location, double v,
            double v1, double v2, @Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public @NotNull Collection<Entity> getNearbyEntities(
            org.bukkit.util.@NotNull BoundingBox boundingBox) {
        return null;
    }

    @Override
    public @NotNull Collection<Entity> getNearbyEntities(
            org.bukkit.util.@NotNull BoundingBox boundingBox,
            @Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(@NotNull Location location,
            @NotNull Vector vector, double v) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(@NotNull Location location,
            @NotNull Vector vector, double v, double v1) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(@NotNull Location location,
            @NotNull Vector vector, double v, @Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceEntities(@NotNull Location location,
            @NotNull Vector vector, double v, double v1, @Nullable Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(@NotNull Location location,
            @NotNull Vector vector, double v) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(@NotNull Location location,
            @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    public @Nullable RayTraceResult rayTraceBlocks(@NotNull Location location,
            @NotNull Vector vector, double v, @NotNull FluidCollisionMode fluidCollisionMode,
            boolean b) {
        return null;
    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>(entityManager.getAll());
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        return entityManager.getAll().stream().filter(e -> e instanceof GlowLivingEntity)
            .map(e -> (GlowLivingEntity) e).collect(Collectors.toCollection(LinkedList::new));
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
        return entityManager.getAll().stream().filter(e -> cls.isAssignableFrom(e.getClass()))
            .map(e -> (T) e).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Collection<Entity> getEntitiesByClasses(Class<?>... classes) {
        ArrayList<Entity> result = new ArrayList<>();
        for (Entity e : entityManager.getAll()) {
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
    public boolean setSpawnLocation(Location newSpawn) {
        return setSpawnLocation(newSpawn, true);
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z, float angle) {
        // TODO: Not clear how to split angle in pitch & yaw
        return setSpawnLocation(new Location(this, x, y, z, angle, angle), true);
    }

    @Override
    public boolean setSpawnLocation(int x, int y, int z) {
        return setSpawnLocation(new Location(this, x, y, z), true);
    }

    /**
     * Sets the spawn location of the world.
     *
     * @param newSpawn the new spawn location
     * @param anchor   if true, the spawn is never unloaded while the world is running
     * @return true if the spawn location has changed
     */
    public boolean setSpawnLocation(Location newSpawn, boolean anchor) {
        Location oldSpawn = spawnLocation;
        if (newSpawn.equals(oldSpawn)) {
            return false;
        }
        spawnLocation = newSpawn;
        if (anchor) {
            setKeepSpawnInMemory(keepSpawnLoaded);
        }
        EventFactory.getInstance().callEvent(new SpawnChangeEvent(this, oldSpawn));
        return true;
    }

    public boolean setSpawnLocation(int x, int y, int z, boolean anchor) {
        return setSpawnLocation(new Location(this, x, y, z), anchor);
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
            Location spawn = generator.getFixedSpawnLocation(this, ThreadLocalRandom.current());
            // we're already going to anchor if told to, so don't request another anchor
            if (spawn == null) {
                // determine a location randomly
                int spawnX = ThreadLocalRandom.current().nextInt(256) - 128;
                int spawnZ = ThreadLocalRandom.current().nextInt(256) - 128;
                getChunkAt(spawnX >> 4, spawnZ >> 4)
                    .load(true);  // I'm not sure there's a sane way around this

                for (int tries = 0; tries < 1000 && !generator.canSpawn(this, spawnX, spawnZ);
                     ++tries) {
                    spawnX += ThreadLocalRandom.current().nextInt(256) - 128;
                    spawnZ += ThreadLocalRandom.current().nextInt(256) - 128;
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
                chunkManager.unloadOldChunks();
                spawnChunkLock = null;
            }
        }

        if (needSpawn) {
            setSpawnLocation(spawnLocation.getBlockX(), getHighestBlockYAt(spawnLocation
                .getBlockX(), spawnLocation.getBlockZ()), spawnLocation.getBlockZ(), false);
        }
    }

    private void prepareSpawn() {
        int centerX = spawnLocation.getBlockX() >> 4;
        int centerZ = spawnLocation.getBlockZ() >> 4;
        int radius = 4 * server.getViewDistance() / 3;

        long loadTime = System.currentTimeMillis();

        int total = ((radius << 1) + 1) * ((radius << 1) + 1);
        int current = 0;

        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                ++current;
                if (populateAnchoredChunks) {
                    getChunkManager().forcePopulation(x, z);
                } else {
                    loadChunk(x, z);
                }
                spawnChunkLock.acquire(GlowChunk.Key.of(x, z));
                if (System.currentTimeMillis() >= loadTime + 1000) {
                    int progress = 100 * current / total;
                    GlowServer.logger.info("Preparing spawn for " + name + ": " + progress + "%");
                    loadTime = System.currentTimeMillis();
                }
            }
        }
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

    ////////////////////////////////////////////////////////////////////////////
    // Various fixed world properties

    @Override
    public UUID getUID() {
        return uid;
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

    @Override
    public long getTicksPerAnimalSpawns() {
        // Can't be lombokified because inherited return type is long, not int
        return ticksPerAnimalSpawns;
    }

    @Override
    public long getTicksPerMonsterSpawns() {
        // Can't be lombokified because inherited return type is long, not int
        return ticksPerMonsterSpawns;
    }

    @Override
    public long getTicksPerWaterSpawns() {
        // Can't be lombokified because inherited return type is long, not int
        return ticksPerWaterSpawns;
    }

    @Override
    public long getTicksPerWaterAmbientSpawns() {
        // Can't be lombokified because inherited return type is long, not int
        return ticksPerWaterAmbientSpawns;
    }

    @Override
    public long getTicksPerWaterUndergroundCreatureSpawns() {
        return 0;
    }

    @Override
    public void setTicksPerWaterUndergroundCreatureSpawns(int ticksPerWaterUndergroundCreatureSpawns) {

    }

    @Override
    public long getTicksPerAmbientSpawns() {
        // Can't be lombokified because inherited return type is long, not int
        return ticksPerAmbientSpawns;
    }

    @Override
    public long getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Override
    public void setTicksPerSpawns(@NotNull SpawnCategory spawnCategory, int ticksPerCategorySpawn) {

    }

    @Override
    public int getWaterUndergroundCreatureSpawnLimit() {
        return 0;
    }

    @Override
    public void setWaterUndergroundCreatureSpawnLimit(int limit) {

    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Override
    public void setSpawnLimit(@NotNull SpawnCategory spawnCategory, int limit) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // force-save

    @Override
    public void save() {
        save(false);
    }

    /**
     * Saves world to disk synchronously or asynchronously.
     *
     * @param async if true, save asynchronously
     */
    public void save(boolean async) {
        EventFactory.getInstance().callEvent(new WorldSaveEvent(this));

        // save metadata
        writeWorldData(async);

        // save chunkManager
        maybeAsync(async, () -> {
            for (GlowChunk chunk : chunkManager.getLoadedChunks()) {
                chunkManager.performSave(chunk);
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
        return chunkManager.getGenerator();
    }

    @Override
    public @Nullable BiomeProvider getBiomeProvider() {
        return null;
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
        if (GlowTree.newInstance(type, ThreadLocalRandom.current(), blockStateDelegate)
            .generate(loc)) {
            List<BlockState> blockStates = new ArrayList<>(blockStateDelegate.getBlockStates());
            StructureGrowEvent growEvent
                = new StructureGrowEvent(loc, type, false, null, blockStates);
            EventFactory.getInstance().callEvent(growEvent);
            if (!growEvent.isCancelled()) {
                for (BlockState state : blockStates) {
                    state.update(true);
                    if (delegate != null) {
                        delegate.setBlockData(state.getX(), state.getY(), state.getZ(),
                                state.getBlockData());
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Map<Integer, GlowStructure> getStructures() {
        // TODO: Replace with a facade
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

    /**
     * Returns the number of block entities in loaded chunkManager.
     *
     * @return the number of block entities
     */
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

    /**
     * Returns the number of tickable block entities in loaded chunkManager.
     *
     * @return the number of tickable block entities
     */
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

    ////////////////////////////////////////////////////////////////////////////
    // get block, chunk, id, highest with locations

    @Override
    public GlowBlock getBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public GlowBlock getBlockAt(int x, int y, int z) {
        return new GlowBlock(getChunkAt(x >> 4, z >> 4), x, y, z);
    }

    public Material getBlockTypeAt(int x, int y, int z) {
        return getBlockDataAt(x, y, z).getMaterial();
    }

    public BlockData getBlockDataAt(int x, int y, int z) {
        GlowChunk chunk = getChunkAt(x >> 4, z >> 4);
        return chunk.getBlockData(x & 0xf, z & 0xf, y);
    }

    @Override
    public int getHighestBlockYAt(Location location) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public int getHighestBlockYAt(int x, int z, @NotNull HeightmapType heightmapType) {
        return getHighestBlockAt(x, z, heightmapType).getY();
    }

    @Override
    public int getHighestBlockYAt(int x, int z, @NotNull HeightMap heightMap) {
        return getHighestBlockAt(x, z, heightMap).getY();
    }

    @Override
    public int getHighestBlockYAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        return getHighestBlockAt(location, heightMap).getY();
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        return getChunkAt(x >> 4, z >> 4).getHeight(x & 0xf, z & 0xf);
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(int x, int z, @NotNull HeightMap heightMap) {
        // TODO: Support height maps
        return getHighestBlockAt(x, z);
    }

    @NotNull
    @Override
    public Block getHighestBlockAt(@NotNull Location location, @NotNull HeightMap heightMap) {
        // TODO: Support height maps
        return getHighestBlockAt(location);
    }

    @Override
    public Block getHighestBlockAt(int x, int z) {
        return getBlockAt(x, getHighestBlockYAt(x, z), z);
    }

    @Override
    public Block getHighestBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), getHighestBlockYAt(location), location.getBlockZ());
    }

    /**
     * Gets the lowest block at the given {@link Location} such that the block
     * and all blocks above it are either air or one of the given materials.
     *
     * @param location Coordinates to get the highest block
     * @param except   Blocks to exclude in addition to air
     * @return Highest non-empty block
     */
    public Block getHighestBlockAt(Location location, Material... except) {
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
    public GlowChunk getChunkAt(long chunkKey) {
        return getChunk(GlowChunk.Key.of(chunkKey));
    }

    @Override
    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Override
    public GlowChunk getChunkAt(int x, int z) {
        return chunkManager.getChunk(x, z);
    }

    @Override
    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX() >> 4, block.getZ() >> 4);
    }

    public GlowChunk getChunk(GlowChunk.Key key) {
        return chunkManager.getChunk(key);
    }

    @Override
    public void getChunkAtAsync(int x, int z, ChunkLoadCallback cb) {
        ServerProvider.getServer().getScheduler()
            .runTaskAsynchronously(null, () -> cb.onLoad(chunkManager.getChunk(x, z)));
    }

    @Override
    public void getChunkAtAsync(Location location, ChunkLoadCallback cb) {
        getChunkAtAsync(location.getBlockX() >> 4, location.getBlockZ() >> 4, cb);
    }

    @Override
    public void getChunkAtAsync(Block block, ChunkLoadCallback cb) {
        getChunkAtAsync(block.getX() >> 4, block.getZ() >> 4, cb);
    }

    @Override
    public @NotNull CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen) {
        return getChunkAtAsync(x, z, gen, false);
    }

    @NotNull
    @Override
    public CompletableFuture<Chunk> getChunkAtAsync(int x, int z, boolean gen, boolean urgent) {
        // TODO: Support 'urgent'
        CompletableFuture<Chunk> future = new CompletableFuture<>();
        ServerProvider.getServer().getScheduler()
                .runTaskAsynchronously(null, () -> {
                    GlowChunk chunk = chunkManager.getChunk(x, z);
                    chunk.load(gen);
                    future.complete(chunk);
                });
        return future;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return NamespacedKey.minecraft(name);
    }

    @Override
    public boolean lineOfSightExists(@NotNull Location from, @NotNull Location to) {
        return false;
    }

    @Override
    public boolean hasCollisionsIn(org.bukkit.util.@NotNull BoundingBox boundingBox) {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Chunk loading and unloading

    @Override
    public boolean isChunkLoaded(Chunk chunk) {
        return chunk.isLoaded();
    }

    @Override
    public boolean isChunkLoaded(int x, int z) {
        return chunkManager.isChunkLoaded(x, z);
    }

    @Override
    public boolean isChunkGenerated(int x, int z) {
        return chunkManager.getChunk(x, z).isPopulated();
    }

    @Override
    public boolean isChunkInUse(int x, int z) {
        return chunkManager.isChunkInUse(x, z);
    }

    @Override
    public Chunk[] getLoadedChunks() {
        return chunkManager.getLoadedChunks();
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

    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        return !isChunkLoaded(x, z) || getChunkAt(x, z).unload(save, safe);
    }

    @Override
    public boolean unloadChunkRequest(int x, int z) {
        return unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        if (safe && isChunkInUse(x, z)) {
            return false;
        }

        server.getScheduler().runTask(null, () -> unloadChunk(x, z, safe));

        return true;
    }

    @Override
    public boolean regenerateChunk(int x, int z) {
        if (!chunkManager.forceRegeneration(x, z)) {
            return false;
        }
        refreshChunk(x, z);
        return true;
    }

    @Override
    public boolean refreshChunk(int x, int z) {
        if (!isChunkLoaded(x, z)) {
            return false;
        }

        Key key = GlowChunk.Key.of(x, z);
        boolean result = false;

        for (GlowPlayer player : getRawPlayers()) {
            if (player.canSeeChunk(key)) {
                ChunkDataMessage message = getChunkAt(x, z).toMessage();
                player.getSession().sendAndRelease(message, message.getData());
                result = true;
            }
        }

        return result;
    }

    @Override
    public boolean isChunkForceLoaded(int x, int z) {
        return false;
    }

    @Override
    public void setChunkForceLoaded(int x, int z, boolean forced) {
        throw new UnsupportedOperationException("Force-loading chunks is not supported yet.");
    }

    @Override
    public @NotNull Collection<Chunk> getForceLoadedChunks() {
        return Collections.emptyList();
    }

    @Override
    public boolean addPluginChunkTicket(int x, int z, @NotNull Plugin plugin) {
        throw new UnsupportedOperationException("Chunk tickets are not implemented yet.");
    }

    @Override
    public boolean removePluginChunkTicket(int x, int z, @NotNull Plugin plugin) {
        throw new UnsupportedOperationException("Chunk tickets are not implemented yet.");
    }

    @Override
    public void removePluginChunkTickets(@NotNull Plugin plugin) {
        throw new UnsupportedOperationException("Chunk tickets are not implemented yet.");
    }

    @NotNull
    @Override
    public Collection<Plugin> getPluginChunkTickets(int x, int z) {
        throw new UnsupportedOperationException("Chunk tickets are not implemented yet.");
    }

    @NotNull
    @Override
    public Map<Plugin, Collection<Chunk>> getPluginChunkTickets() {
        throw new UnsupportedOperationException("Chunk tickets are not implemented yet.");
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome,
                                               boolean includeBiomeTempRain) {
        return new EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Biomes

    @Override
    public Biome getBiome(int x, int z) {
        if (environment == Environment.THE_END) {
            return Biome.THE_END;
        } else {
            return GlowBiome.getBiome(getChunkAt(x >> 4, z >> 4).getBiome(x & 0xF, z & 0xF))
                    .getType();
        }
    }

    @Override
    public @NotNull Biome getBiome(@NotNull Location location) {
        return null;
    }

    @NotNull
    @Override
    public Biome getBiome(int x, int y, int z) {
        return getBiome(x, z); // TODO: Biomes are now 3-dimensional
    }

    @Override
    public @NotNull Biome getComputedBiome(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBiome(@NotNull Location location, @NotNull Biome biome) {

    }

    @Override
    public void setBiome(int x, int z, @NotNull Biome bio) {
        getChunkAtAsync(
            x >> 4, z >> 4, chunk -> ((GlowChunk) chunk)
                .setBiome(x & 0xF, z & 0xF, GlowBiome.getId(bio)));
    }

    @Override
    public void setBiome(int x, int y, int z, @NotNull Biome biome) {
        // TODO: Biomes are now 3-dimensional
        setBiome(x, z, biome);
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        return null;
    }

    @Override
    public @NotNull BlockData getBlockData(@NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull BlockData getBlockData(int x, int y, int z) {
        return null;
    }

    @Override
    public @NotNull Material getType(@NotNull Location location) {
        return null;
    }

    @Override
    public @NotNull Material getType(int x, int y, int z) {
        return null;
    }

    @Override
    public void setBlockData(@NotNull Location location, @NotNull BlockData blockData) {

    }

    @Override
    public void setBlockData(int x, int y, int z, @NotNull BlockData blockData) {

    }

    @Override
    public void setType(@NotNull Location location, @NotNull Material material) {

    }

    @Override
    public void setType(int x, int y, int z, @NotNull Material material) {

    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type) {
        return false;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type, @Nullable Consumer<BlockState> stateConsumer) {
        return false;
    }

    @Override
    public boolean generateTree(@NotNull Location location, @NotNull Random random, @NotNull TreeType type, @Nullable Predicate<BlockState> statePredicate) {
        return false;
    }

    @Override
    public double getTemperature(int x, int z) {
        return GlowBiomeClimate.getBiomeTemperature(getBiome(x, z));
    }

    @Override
    public double getTemperature(int x, int y, int z) {
        // TODO: Biomes are now 3-dimensional
        return getTemperature(x, z);
    }

    @Override
    public double getHumidity(int x, int z) {
        return GlowBiomeClimate.getBiomeHumidity(getBiome(x, z));
    }

    @Override
    public double getHumidity(int x, int y, int z) {
        // TODO: Biomes are now 3-dimensional
        return getHumidity(x, z);
    }

    @Override
    public int getLogicalHeight() {
        return 0;
    }

    @Override
    public int getMinHeight() {
        return 0;
    }

    @Override
    public @NotNull BiomeProvider vanillaBiomeProvider() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity spawning

    @Override
    public <T extends Entity> T spawn(Location location,
                                      Class<T> clazz) throws IllegalArgumentException {
        return (T) spawnGlowEntity(location, EntityRegistry.getEntity(clazz), SpawnReason.CUSTOM, null);
    }

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz,
                                      Consumer<T> function) throws IllegalArgumentException {
        return (T) spawnGlowEntity(location, EntityRegistry.getEntity(clazz), SpawnReason.CUSTOM, function);
    }

    @NotNull
    @Override
    public <T extends Entity> T spawn(Location location,
                                      Class<T> clazz,
                                      Consumer<T> function,
                                      CreatureSpawnEvent.SpawnReason spawnReason) throws IllegalArgumentException {
        return (T) spawnGlowEntity(location, EntityRegistry.getEntity(clazz), spawnReason, function);
    }

    @Override
    public <T extends Entity> @NotNull T spawn(@NotNull Location location, @NotNull Class<T> clazz, boolean randomizeData, @Nullable Consumer<T> function) throws IllegalArgumentException {
        return null;
    }

    /**
     * Spawns an entity.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param clazz    the class of the {@link Entity} to spawn
     * @param reason   the reason for the spawning of the entity
     * @return an instance of the spawned {@link Entity}
     * @throws IllegalArgumentException TODO: document the reason this can happen
     */
    public <T extends GlowEntity, E extends Entity> GlowEntity spawnGlowEntity(Location location, Class<T> clazz,
                                                                               SpawnReason reason, @Nullable Consumer<E> function) throws IllegalArgumentException {
        checkNotNull(location);
        checkNotNull(clazz);

        GlowEntity entity = null;

        try {
            if (EntityRegistry.getEntity(clazz) != null) {
                entity = EntityStorage.create(clazz, location);
            }
            if (entity != null && function != null) {
                function.accept((E) entity);
            }
            EntitySpawnEvent spawnEvent = null;
            if (entity instanceof LivingEntity) {
                spawnEvent = EventFactory.getInstance()
                    .callEvent(new CreatureSpawnEvent((LivingEntity) entity, reason));
            } else if (!(entity instanceof Item)) { // ItemSpawnEvent is called elsewhere
                spawnEvent = EventFactory.getInstance().callEvent(new EntitySpawnEvent(entity));
            }
            if (spawnEvent != null && spawnEvent.isCancelled()) {
                // TODO: separate spawning and construction for better event cancellation
                entity.remove();
            } else {
                List<Message> spawnMessage = entity.createSpawnMessage();
                final GlowEntity finalEntity = entity;
                getRawPlayers().stream().filter(player -> player.canSeeEntity(finalEntity))
                    .forEach(player -> player.getSession().sendAll(spawnMessage
                        .toArray(new Message[spawnMessage.size()])));
            }
        } catch (NoSuchMethodError | IllegalAccessError e) {
            GlowServer.logger.log(Level.WARNING, "Invalid entity spawn: ", e);
        } catch (Throwable t) {
            GlowServer.logger.log(Level.SEVERE, "Unable to spawn entity: ", t);
        }

        if (entity != null) {
            return entity;
        }

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Spawn a custom entity at the given {@link Location}.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param id       the id of the custom entity
     * @param <T>      the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     */
    public <T extends Entity> T spawnCustomEntity(Location location,
                                                  String id) throws IllegalArgumentException {
        return spawnCustomEntity(location, id, SpawnReason.CUSTOM);
    }

    /**
     * Spawn a custom entity at the given {@link Location}, with the given {@link SpawnReason}.
     *
     * @param location the {@link Location} to spawn the entity at
     * @param id       the id of the custom entity
     * @param reason   the reason for the spawning of the entity
     * @param <T>      the class of the {@link Entity} to spawn
     * @return an instance of the spawned {@link Entity}
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> T spawnCustomEntity(Location location, String id,
                                                  SpawnReason reason) throws IllegalArgumentException {
        checkNotNull(location);
        checkNotNull(id);
        CustomEntityDescriptor descriptor = EntityRegistry.getCustomEntityDescriptor(id);
        if (descriptor == null) {
            throw new IllegalArgumentException(
                "Could not find a custom entity descriptor for the given id '" + id + "'");
        }
        return (T) spawn(location, descriptor.getEntityClass(), reason);
    }

    /**
     * Spawn an item at the given {@link Location} without shooting effect.
     *
     * @param location the {@link Location} to spawn the item at
     * @param item     the {@link ItemStack} the item should have
     */
    @Override
    public GlowItem dropItem(Location location, ItemStack item) {
        checkNotNull(location);
        GlowItem entity = new GlowItem(location, item);
        ItemSpawnEvent event = EventFactory.getInstance().callEvent(new ItemSpawnEvent(entity));
        if (event.isCancelled()) {
            entity.remove();
        }
        return entity;
    }

    @Override
    public @NotNull Item dropItem(@NotNull Location location, @NotNull ItemStack stack, @Nullable Consumer<Item> consumer) {
        final GlowItem item = dropItem(location, stack);
        if (!item.isRemoved() && consumer != null) {
            consumer.accept(item);
        }
        return item;
    }

    /**
     * Spawn an item at the given {@link Location} with shooting effect.
     *
     * @param location the {@link Location} to spawn the item at
     * @param item     the {@link ItemStack} the item should have
     */
    @Override
    public GlowItem dropItemNaturally(Location location, ItemStack item) {
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        // Calculate initial velocity using radius and offsetY as constant
        // offsetX and offsetZ are calculated using random and Pythagorean theorem
        double radius = 0.1;
        double offsetX = tlr.nextDouble(radius * 2) - radius;
        double offsetY = 0.15;
        double offsetZ = Math.sqrt(Math.pow(radius, 2) - Math.pow(offsetX, 2));

        // The previous calculation always gives a non-negative zOffset
        // This adds a 50% chance of offsetZ being negative
        if (tlr.nextBoolean()) {
            offsetZ *= -1;
        }

        // Move starting point to the center of the block
        location.add(0.5, 0.5, 0.5);
        GlowItem dropItem = dropItem(location, item);
        dropItem.setVelocity(new Vector(offsetX, offsetY, offsetZ));
        return dropItem;
    }

    @Override
    public @NotNull Item dropItemNaturally(@NotNull Location location, @NotNull ItemStack stack, @Nullable Consumer<Item> consumer) {
        final GlowItem item = dropItemNaturally(location, stack);
        if (!item.isRemoved() && consumer != null) {
            consumer.accept(item);
        }
        return item;
    }

    @Override
    public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
        // Transformative magic
        Vector randVec = new Vector(ThreadLocalRandom.current().nextGaussian(), ThreadLocalRandom
            .current().nextGaussian(), ThreadLocalRandom.current().nextGaussian());
        randVec.multiply(0.0075 * spread);

        velocity.normalize();
        velocity.add(randVec);
        velocity.multiply(speed);

        // yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
        // pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D

        Arrow arrow = spawn(location, Arrow.class);
        arrow.setVelocity(velocity);
        return arrow;
    }

    @NotNull
    @Override
    public <T extends AbstractArrow> T spawnArrow(@NotNull Location location, @NotNull Vector vector, float speed, float spread, @NotNull Class<T> clazz) {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location,
                                          MaterialData data) throws IllegalArgumentException {
        checkNotNull(data);
        return spawnFallingBlock(location, data.getItemType(), data.getData());
    }

    @Override
    @Deprecated
    public FallingBlock spawnFallingBlock(Location location, Material material,
            byte data) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Deprecated API.");
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, BlockData blockData)
            throws IllegalArgumentException {
        checkNotNull(location);
        checkNotNull(blockData, "BlockData cannot be null.");
        return new GlowFallingBlock(location, blockData);
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType type) {
        checkNotNull(loc);
        checkNotNull(type);
        return spawn(loc, type.getEntityClass());
    }

    @Override
    public @NotNull Entity spawnEntity(@NotNull Location loc, @NotNull EntityType type, boolean randomizeData) {
        return null;
    }

    private GlowLightningStrike strikeLightningFireEvent(Location loc, boolean effect,
                                                         boolean isSilent) {
        checkNotNull(loc);
        GlowLightningStrike strike = new GlowLightningStrike(loc, effect, isSilent);
        LightningStrikeEvent event = new LightningStrikeEvent(this, strike);
        if (EventFactory.getInstance().callEvent(event).isCancelled()) {
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

    @Override
    public @Nullable Location findLightningRod(@NotNull Location location) {
        return null;
    }

    @Override
    public @Nullable Location findLightningTarget(@NotNull Location location) {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Time

    @Override
    public void setTime(long time) {
        this.time = (time % TickUtil.TICKS_PER_DAY + TickUtil.TICKS_PER_DAY)
            % TickUtil.TICKS_PER_DAY;

        getRawPlayers().forEach(GlowPlayer::sendTime);
    }

    @Override
    public boolean isDayTime() {
        return false;
    }

    @Override
    public long getGameTime() {
        return 0;
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
        if (EventFactory.getInstance().callEvent(event).isCancelled()) {
            return;
        }

        // change weather
        boolean previouslyRaining = currentlyRaining;
        currentlyRaining = hasStorm;

        // Numbers borrowed from CraftBukkit.
        if (currentlyRaining) {
            setWeatherDuration(ThreadLocalRandom.current().nextInt(TickUtil.TICKS_PER_HALF_DAY)
                + TickUtil.TICKS_PER_HALF_DAY);
        } else {
            setWeatherDuration(ThreadLocalRandom.current().nextInt(TickUtil.TICKS_PER_WEEK)
                + TickUtil.TICKS_PER_HALF_DAY);
        }

        // update players
        if (previouslyRaining != currentlyRaining) {
            getRawPlayers().forEach(GlowPlayer::sendWeather);
        }
    }

    @Override
    public void setThundering(boolean thundering) {
        // call event
        ThunderChangeEvent event = new ThunderChangeEvent(this, thundering);
        if (EventFactory.getInstance().callEvent(event).isCancelled()) {
            return;
        }

        // change weather
        this.thundering = thundering;

        // Numbers borrowed from CraftBukkit.
        if (this.thundering) {
            setThunderDuration(ThreadLocalRandom.current().nextInt(TickUtil.TICKS_PER_HALF_DAY)
                + TickUtil.minutesToTicks(3));
        } else {
            setThunderDuration(ThreadLocalRandom.current().nextInt(TickUtil.TICKS_PER_WEEK)
                + TickUtil.TICKS_PER_WEEK);
        }
    }

    @Override
    public boolean isClearWeather() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public void setClearWeatherDuration(int i) {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public int getClearWeatherDuration() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    private void updateWeather() {
        float previousRainDensity = rainDensity;
        float previousSkyDarkness = skyDarkness;
        float rainDensityModifier = currentlyRaining ? .01F : -.01F;
        float skyDarknessModifier = thundering ? .01F : -.01F;
        rainDensity = Math.max(0, Math.min(1, previousRainDensity + rainDensityModifier));
        skyDarkness = Math.max(0, Math.min(1, previousSkyDarkness + skyDarknessModifier));

        if (previousRainDensity != rainDensity) {
            getRawPlayers().forEach(GlowPlayer::sendRainDensity);
        }

        if (previousSkyDarkness != skyDarkness) {
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
        checkNotNull(loc);
        return createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, true);
    }

    @Override
    public boolean createExplosion(Entity source, Location loc, float power, boolean setFire,
                                   boolean breakBlocks) {
        return createExplosion(source, loc.getX(), loc.getY(), loc.getZ(), power, setFire,
            breakBlocks);
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float power, boolean setFire, boolean breakBlocks) {
        return createExplosion(null, location, power, setFire, breakBlocks);
    }

    @Override
    public boolean createExplosion(@NotNull Location location, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source) {
        return createExplosion(source, location, power, setFire, breakBlocks);
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
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire,
                                   boolean breakBlocks) {
        return createExplosion(null, x, y, z, power, setFire, breakBlocks);
    }

    @Override
    public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks, @Nullable Entity source) {
        return createExplosion(source, x, y, z, power, setFire, breakBlocks);
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
    public boolean createExplosion(Entity source, double x, double y, double z, float power,
                                   boolean incendiary, boolean breakBlocks) {
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
        checkNotNull(location);
        checkNotNull(effect);
        int radiusSquared = radius * radius;
        getRawPlayers().stream()
            .filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared)
            .forEach(player -> player.playEffect(location, effect, data));
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data, int radius) {
        playEffect(location, effect, GlowEffect.getDataValue(effect, data), radius);
    }

    /**
     * Plays an effect to all but one player within a given radius around a location.
     *
     * @param location the {@link Location} around which players must be to
     *                 hear the effect
     * @param effect   the {@link Effect}
     * @param data     a data bit needed for some effects
     * @param radius   the radius around the location
     * @param exclude  the player who won't see the effect
     */
    public void playEffectExceptTo(Location location, Effect effect, int data, int radius,
                                   Player exclude) {
        checkNotNull(location);
        checkNotNull(effect);
        checkNotNull(exclude);
        int radiusSquared = radius * radius;
        getRawPlayers().stream().filter(player -> !player.equals(exclude)
            && player.getLocation().distanceSquared(location) <= radiusSquared)
            .forEach(player -> player.playEffect(location, effect, data));
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        checkNotNull(sound);
        playSound(location, sound, GlowSound
            .getSoundCategory(GlowSound.getVanillaId(sound)), volume, pitch);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        playSound(location, GlowSound.getVanillaSound(sound), volume, pitch);
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory category, float volume,
                          float pitch) {
        checkNotNull(location);
        checkNotNull(sound);

        double radiusSquared = Math.pow(volume * 16, 2);
        getRawPlayers().stream()
            .filter(player -> player.getLocation().distanceSquared(location) <= radiusSquared)
            .forEach(player -> player.playSound(location, sound, category, volume, pitch));
    }

    @Override
    public void playSound(Location location, String sound, SoundCategory category, float volume,
                          float pitch) {
        checkNotNull(sound);
        playSound(location, GlowSound.getVanillaSound(sound), category, volume, pitch);
    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, float volume, float pitch) {

    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String sound, float volume, float pitch) {

    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull Sound sound, @NotNull SoundCategory category, float volume, float pitch) {

    }

    @Override
    public void playSound(@NotNull Entity entity, @NotNull String sound, @NotNull SoundCategory category, float volume, float pitch) {

    }

    @Override
    public Spigot spigot() {
        return spigot;
    }

    @Nullable
    @Override
    public Raid locateNearestRaid(@NotNull Location location, int radius) {
        throw new UnsupportedOperationException("Raids are not supported yet.");
    }

    @NotNull
    @Override
    public List<Raid> getRaids() {
        throw new UnsupportedOperationException("Raids are not supported yet.");
    }

    @Nullable
    @Override
    public DragonBattle getEnderDragonBattle() {
        if (this.getEnvironment() == Environment.THE_END) {
            throw new UnsupportedOperationException("The DragonBattle API is not supported yet.");
        } else {
            return null;
        }
    }

    /**
     * Displays the given particle to all players.
     *
     * @param loc      the location
     * @param particle the particle type
     * @param offsetX  TODO: document this parameter
     * @param offsetY  TODO: document this parameter
     * @param offsetZ  TODO: document this parameter
     * @param speed    TODO: document this parameter
     * @param amount   the number of particles
     */
    //@Override
    public void showParticle(Location loc, Effect particle, float offsetX, float offsetY,
                             float offsetZ, float speed, int amount) {
        checkNotNull(loc);
        checkNotNull(particle);
        int radius;
        if (GlowParticle.isLongDistance(particle)) {
            radius = 48;
        } else {
            radius = 16;
        }

        showParticle(loc, particle, particle.getId(), 0, offsetX, offsetY, offsetZ, speed, amount, radius);
    }

    /**
     * Displays the given particle to all players.
     *
     * @param loc      the location
     * @param particle the particle type
     * @param id       the block or item type ID
     * @param data     the block or item data
     * @param offsetX  TODO: document this parameter
     * @param offsetY  TODO: document this parameter
     * @param offsetZ  TODO: document this parameter
     * @param speed    TODO: document this parameter
     * @param amount   the number of particles
     * @param radius   TODO: document this parameter
     */
    //@Override
    public void showParticle(Location loc, Effect particle, int id, int data, float offsetX,
                             float offsetY, float offsetZ, float speed, int amount, int radius) {
        checkNotNull(loc);
        checkNotNull(particle);

        double radiusSquared = radius * radius;

        getRawPlayers().stream()
                .filter(player -> player.getLocation().distanceSquared(loc) <= radiusSquared)
                .forEach(player -> player.showParticle(loc, particle,
                        new MaterialData(server.getBlockDataManager().convertToBlockData(id).getMaterial(), (byte) data),
                        offsetX, offsetY, offsetZ, speed, amount));
    }

    /**
     * Save the world data using the metadata service.
     *
     * @param async Whether to write asynchronously.
     */
    private void writeWorldData(boolean async) {
        maybeAsync(async, () -> {
            try {
                storage.getMetadataService().writeWorldData();
                storage.getScoreboardIoService().save();
            } catch (IOException e) {
                server.getLogger().severe("Could not save metadata for world: " + getName());
                e.printStackTrace();
            }

            storage.getStructureDataService().writeStructuresData(structures);
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
        checkNotNull(runnable);
        if (async) {
            server.getScheduler().runTaskAsynchronously(null, runnable);
        } else {
            runnable.run();
        }
    }

    /**
     * Unloads the world.
     *
     * @return true if successful
     */
    public boolean unload() {
        // terminate task service
        //aiTaskService.shutdown();
        if (EventFactory.getInstance().callEvent(new WorldUnloadEvent(this)).isCancelled()) {
            return false;
        }
        try {
            storage.getChunkIoService().unload();
            storage.getScoreboardIoService().unload();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the world folder.
     *
     * @return world folder
     */
    @Override
    public File getWorldFolder() {
        return storage.getFolder();
    }

    @Override
    public String[] getGameRules() {
        return gameRuleMap.getKeys();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Game rules

    @Override
    public String getGameRuleValue(@NonNls String rule) {
        return gameRuleMap.getString(rule);
    }

    @Override
    public <T> T getGameRuleValue(GameRule<T> gameRule) {
        return null; // TODO
    }

    @Override
    public boolean setGameRuleValue(@NonNls String rule, String value) {
        if (!gameRuleMap.setValue(rule, value)) {
            return false;
        }
        if (rule.equals(GameRules.DO_DAYLIGHT_CYCLE)) {
            // inform clients about the daylight cycle change
            getRawPlayers().forEach(GlowPlayer::sendTime);
        } else if (rule.equals(GameRules.REDUCED_DEBUG_INFO)) {
            // inform clients about the debug info change
            for (GlowPlayer player : getRawPlayers()) {
                EntityStatusMessage message = new EntityStatusMessage(player.getEntityId(),
                        gameRuleMap.getBoolean(GameRules.REDUCED_DEBUG_INFO)
                                ? EntityStatusMessage.ENABLE_REDUCED_DEBUG_INFO
                                : EntityStatusMessage.DISABLE_REDUCED_DEBUG_INFO);
                player.getSession().send(message);
            }
        }
        return true;
    }

    @Override
    public boolean isGameRule(String rule) {
        return gameRuleMap.isGameRule(rule);
    }

    @Override
    public <T> T getGameRuleDefault(GameRule<T> gameRule) {
        return null; // TODO
    }

    @Override
    public <T> boolean setGameRule(GameRule<T> gameRule, T t) {
        return false;
    }

    public Map<String, CommandFunction> getFunctions() {
        // TODO: replace this with a facade
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
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count,
                                  T data) {
        spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX,
                              double offsetY, double offsetZ) {
        spawnParticle(particle, location.getX(), location.getY(), location
            .getZ(), count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count,
                              double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count,
                                  double offsetX,
                                  double offsetY, double offsetZ, T data) {
        checkNotNull(location);
        spawnParticle(particle, location.getX(), location.getY(), location
            .getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count,
                                  double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX,
                              double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, location.getX(), location.getY(), location
            .getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count,
                              double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count,
                                  double offsetX,
                                  double offsetY, double offsetZ, double extra, T data) {
        checkNotNull(particle);
        checkNotNull(location);
        if (data != null && !particle.getDataType().isInstance(data)) {
            throw new IllegalArgumentException(
                "wrong data type " + data.getClass() + " should be " + particle.getDataType());
        }

        for (GlowPlayer player : getRawPlayers()) {
            if (!player.getWorld().equals(this)) {
                continue;
            }
            player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
        }
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count,
            double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(particle, new Location(this, x, y, z), count, offsetX, offsetY, offsetZ,
                extra, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, List<Player> receivers, Player source,
            double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ,
            double extra, T data) {
        if (receivers == null) {
            receivers = getPlayers();
        }

        for (Player player : receivers) {
            if (player.canSee(source) && ((GlowPlayer) player).canSeeEntity((GlowPlayer) source)) {
                player.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra,
                    data);
            }
        }
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @Nullable List<Player> list,
            @Nullable Player player, double v, double v1, double v2, int i, double v3, double v4,
            double v5, double v6, @Nullable T t, boolean b) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int i,
            double v, double v1, double v2, double v3, @Nullable T t, boolean b) {

    }

    @Override
    public <T> void spawnParticle(@NotNull Particle particle, double v, double v1, double v2, int i,
            double v3, double v4, double v5, double v6, @Nullable T t, boolean b) {

    }

    @Override
    public @Nullable Location locateNearestStructure(@NotNull Location location,
            @NotNull StructureType structureType, int i, boolean b) {
        return null;
    }

    @Override
    public @Nullable StructureSearchResult locateNearestStructure(@NotNull Location origin, org.bukkit.generator.structure.@NotNull StructureType structureType, int radius, boolean findUnexplored) {
        return null;
    }

    @Override
    public @Nullable StructureSearchResult locateNearestStructure(@NotNull Location origin, @NotNull Structure structure, int radius, boolean findUnexplored) {
        return null;
    }

    @Override
    public @Nullable Location locateNearestBiome(@NotNull Location location, @NotNull Biome biome, int i) {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public @Nullable Location locateNearestBiome(@NotNull Location location, @NotNull Biome biome, int i, int i1) {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isUltrawarm() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isNatural() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isBedWorks() {
        return false;
    }

    @Override
    public boolean hasSkyLight() {
        return false;
    }

    @Override
    public boolean hasCeiling() {
        return false;
    }

    @Override
    public double getCoordinateScale() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean hasSkylight() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean hasBedrockCeiling() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isPiglinSafe() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isRespawnAnchorWorks() {
        return false;
    }

    @Override
    public boolean doesBedWork() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean doesRespawnAnchorWork() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean hasRaids() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public boolean isUltraWarm() {
        return false;
    }

    @Override
    public boolean isFixedTime() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public @NotNull Collection<Material> getInfiniburn() {
        // TODO: 1.16
        throw new NotImplementedException("TODO");
    }

    @Override
    public void sendGameEvent(@Nullable Entity sourceEntity, @NotNull GameEvent gameEvent, @NotNull Vector position) {

    }

    @Override
    public int getSimulationDistance() {
        return 0;
    }

    @Override
    public void setSimulationDistance(int simulationDistance) {

    }

    @Override
    public int getNoTickViewDistance() {
        // TODO: Distinction between no-tick and tick view distance
        return this.getViewDistance();
    }

    @Override
    public void setNoTickViewDistance(int viewDistance) {
        // TODO: Distinction between no-tick and tick view distance
        this.setViewDistance(viewDistance);
    }

    @Override
    public int getSendViewDistance() {
        return 0;
    }

    @Override
    public void setSendViewDistance(int viewDistance) {

    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        checkNotNull(metadataKey);
        checkNotNull(newMetadataValue);
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
        for (Location location : tickMap) {
            GlowChunk chunk = (GlowChunk) location.getChunk();
            if (!chunk.isLoaded()) {
                continue;
            }
            int typeId = chunk.getType(
                location.getBlockX() & 0xF, location.getBlockZ() & 0xF, location.getBlockY());
            BlockType type = itemTable.getBlock(typeId);
            if (type == null) {
                cancelPulse(location);
                continue;
            }
            GlowBlock block = new GlowBlock(chunk, location.getBlockX(), location
                .getBlockY(), location.getBlockZ());
            int speed = type.getPulseTickSpeed(block);
            boolean once = type.isPulseOnce(block);
            if (speed == 0) {
                continue;
            }
            if (fullTime % speed == 0) {
                type.receivePulse(block);
                if (once) {
                    cancelPulse(location);
                }
            }
        }
    }

    public ConcurrentSet<Location> getTickMap() {
        // TODO: replace with a facade
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

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return null;
    }

    /**
     * The metadata store class for worlds.
     */
    private static final class WorldMetadataStore extends MetadataStoreBase<World>
        implements MetadataStore<World> {

        @Override
        protected String disambiguate(World subject, String metadataKey) {
            return subject.getName() + ":" + metadataKey;
        }
    }
}
