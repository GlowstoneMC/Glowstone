package net.glowstone;

import net.glowstone.block.GlowBlock;
import net.glowstone.constants.GlowBiome;
import net.glowstone.entity.*;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.io.WorldMetadataService.WorldFinalValues;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.io.anvil.AnvilWorldStorageProvider;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * A class which represents the in-game world.
 * @author Graham Edgecombe
 */
public final class GlowWorld implements World {

    /**
     * The metadata store class for worlds.
     */
    private final static class WorldMetadataStore extends MetadataStoreBase<World> implements MetadataStore<World> {
        @Override
        protected String disambiguate(World subject, String metadataKey) {
            return subject.getName() + ":" + metadataKey;
        }
    }

    /**
     * The metadata store for world objects.
     */
    private final static MetadataStore<World> metadata = new WorldMetadataStore();

    /**
     * The length in ticks of one Minecraft day.
     */
    public static final long DAY_LENGTH = 24000;

    /**
     * The length in ticks between autosaves (5 minutes).
     */
    private static final int AUTOSAVE_TIME = 20 * 60 * 5;

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
    private final ChunkManager.ChunkLock spawnChunkLock;

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
     * The world populators for this world.
     */
    private final List<BlockPopulator> populators;

    /**
     * The game rules used in this world.
     */
    private final Map<String, String> gameRules = new HashMap<>();

    /**
     * The environment.
     */
    private final Environment environment;

    /**
     * The world type.
     */
    private final WorldType worldType;

    /**
     * Whether structure generation is enabled.
     */
    private final boolean generateStructures;

    /**
     * The world seed.
     */
    private final long seed;

    /**
     * The spawn position.
     */
    private Location spawnLocation;

    /**
     * Whether to keep the spawn chunks in memory (prevent them from being unloaded)
     */
    private boolean keepSpawnLoaded = true;

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
    private boolean currentlyRaining = false;

    /**
     * How many ticks until the rain/snow status is expected to change.
     */
    private int rainingTicks = 0;

    /**
     * Whether it is currently thundering on this world.
     */
    private boolean currentlyThundering = false;

    /**
     * How many ticks until the thundering status is expected to change.
     */
    private int thunderingTicks = 0;

    /**
     * The age of the world, in ticks.
     */
    private long worldAge = 0;

    /**
     * The current world time.
     */
    private long time = 0;

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

    /**
     * Creates a new world from the options in the given WorldCreator.
     * @param server The server for the world.
     * @param creator The WorldCreator to use.
     */
    public GlowWorld(GlowServer server, WorldCreator creator) {
        this.server = server;

        // set up values from WorldCreator
        name = creator.name();
        environment = creator.environment();
        worldType = creator.type();
        generateStructures = creator.generateStructures();

        final ChunkGenerator generator = creator.generator();
        storageProvider = new AnvilWorldStorageProvider(new File(server.getWorldContainer(), name));
        storageProvider.setWorld(this);
        chunks = new ChunkManager(this, storageProvider.getChunkIoService(), generator);
        populators = generator.getDefaultPopulators(this);

        // set up values from server defaults
        ticksPerAnimal = server.getTicksPerAnimalSpawns();
        ticksPerMonster = server.getTicksPerMonsterSpawns();
        monsterLimit = server.getMonsterSpawnLimit();
        animalLimit = server.getAnimalSpawnLimit();
        waterAnimalLimit = server.getWaterAnimalSpawnLimit();
        ambientLimit = server.getAmbientSpawnLimit();

        // read in world data
        WorldFinalValues values = null;
        try {
            values = storageProvider.getMetadataService().readWorldData();
        } catch (IOException e) {
            server.getLogger().log(Level.SEVERE, "Error reading world for creation", e);
        }
        if (values != null) {
            if (values.getSeed() == 0L) {
                this.seed = creator.seed();
            } else {
                this.seed = values.getSeed();
            }
            this.uid = values.getUuid();
        } else {
            this.seed = creator.seed();
            this.uid = UUID.randomUUID();
        }

        // begin loading spawn area
        spawnChunkLock = newChunkLock("spawn");
        EventFactory.onWorldInit(this);
        server.getLogger().info("Preparing spawn for " + name + "...");

        // determine the spawn location if we need to
        if (spawnLocation == null) {
            // no location loaded, look for fixed spawn
            spawnLocation = generator.getFixedSpawnLocation(this, random);

            if (spawnLocation == null) {
                // determine a location randomly
                int spawnX = random.nextInt(128) - 64, spawnZ = random.nextInt(128) - 64;
                GlowChunk chunk = getChunkAt(spawnX >> 4, spawnZ >> 4);
                //GlowServer.logger.info("determining spawn: " + chunk.getX() + " " + chunk.getZ());
                chunk.load(true);  // I'm not sure there's a sane way around this
                for (int tries = 0; tries < 10 && !generator.canSpawn(this, spawnX, spawnZ); ++tries) {
                    spawnX += random.nextInt(128) - 64;
                    spawnZ += random.nextInt(128) - 64;
                }
                setSpawnLocation(spawnX, getHighestBlockYAt(spawnX, spawnZ), spawnZ);
            }
        }

        // load up chunks around the spawn location
        spawnChunkLock.clear();
        if (keepSpawnLoaded) {
            int centerX = spawnLocation.getBlockX() >> 4;
            int centerZ = spawnLocation.getBlockZ() >> 4;
            int radius = 4 * server.getViewDistance() / 3;

            long loadTime = System.currentTimeMillis();

            int total = (radius * 2 + 1) * (radius * 2 + 1), current = 0;
            for (int x = centerX - radius; x <= centerX + radius; ++x) {
                for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                    ++current;
                    loadChunk(x, z);
                    spawnChunkLock.acquire(new GlowChunk.Key(x, z));

                    if (System.currentTimeMillis() >= loadTime + 1000) {
                        int progress = 100 * current / total;
                        GlowServer.logger.info("Preparing spawn for " + name + ": " + progress + "%");
                        loadTime = System.currentTimeMillis();
                    }
                }
            }
        }
        server.getLogger().info("Preparing spawn for " + name + ": done");
        EventFactory.onWorldLoad(this);
    }

    @Override
    public String toString() {
        return "GlowWorld{" +
                "name='" + name + '\'' +
                '}';
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various internal mechanisms

    /**
     * Get the world chunk manager.
     * @return The ChunkManager for the world.
     */
    public ChunkManager getChunkManager() {
        return chunks;
    }

    /**
     * Get the world's parent server.
     * @return The GlowServer for the world.
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Get a new chunk lock object a player or other party can use to keep chunks loaded.
     * @return The ChunkLock.
     */
    public ChunkManager.ChunkLock newChunkLock(String desc) {
        return new ChunkManager.ChunkLock(chunks, name + ": " + desc);
    }

    /**
     * Updates all the entities within this world.
     */
    public void pulse() {
        List<GlowEntity> temp = new ArrayList<>(entities.getAll());
        List<GlowEntity> players = new LinkedList<>();

        // pulse players last so they actually see that other entities have
        // moved. unfortunately pretty hacky. not a problem for players b/c
        // their position is modified by session ticking.
        for (GlowEntity entity : temp) {
            if (entity instanceof GlowPlayer) {
                players.add(entity);
            } else {
                entity.pulse();
            }
        }
        for (GlowEntity entity : players) {
            entity.pulse();
        }

        for (GlowEntity entity : temp) {
            entity.reset();
        }

        // Tick the world age and time of day
        // Modulus by 24000, the tick length of a day
        worldAge++;
        time = (time + 1) % DAY_LENGTH;
        if (worldAge % (30 * 20) == 0) {
            // Only send the time every so often; clients are smart.
            for (GlowPlayer player : getRawPlayers()) {
                player.sendTime();
            }
        }

        // only tick weather in a NORMAL world
        if (environment == Environment.NORMAL) {
            if (--rainingTicks <= 0) {
                setStorm(!currentlyRaining);
            }

            if (--thunderingTicks <= 0) {
                setThundering(!currentlyThundering);
            }

            if (currentlyRaining && currentlyThundering) {
                if (random.nextDouble() < .01) {
                    GlowChunk[] chunkList = chunks.getLoadedChunks();
                    if (chunkList.length > 0) {
                        GlowChunk chunk = chunkList[random.nextInt(chunkList.length)];

                        int x = (chunk.getX() << 4) + random.nextInt(16);
                        int z = (chunk.getZ() << 4) + random.nextInt(16);
                        int y = getHighestBlockYAt(x, z);

                        strikeLightning(new Location(this, x, y, z));
                    }
                }
            }
        }

        if (--saveTimer <= 0) {
            saveTimer = AUTOSAVE_TIME;
            chunks.unloadOldChunks();
            if (autosave) {
                save(true);
            }
        }
    }

    /**
     * Gets the entity manager.
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
        return new ArrayList<Player>(entities.getAll(GlowPlayer.class));
    }

    @Override
    public List<Entity> getEntities() {
        return new ArrayList<Entity>(entities.getAll());
    }

    @Override
    public List<LivingEntity> getLivingEntities() {
        List<LivingEntity> result = new LinkedList<>();
        for (Entity e : entities.getAll()) {
            if (e instanceof GlowLivingEntity) result.add((GlowLivingEntity) e);
        }
        return result;
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
        ArrayList<T> result = new ArrayList<>();
        for (Entity e : entities.getAll()) {
            if (cls.isAssignableFrom(e.getClass())) {
                result.add((T) e);
            }
        }
        return result;
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
        Location oldSpawn = spawnLocation;
        spawnLocation = new Location(this, x, y, z);
        EventFactory.onSpawnChange(this, oldSpawn);
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

        // update the chunk lock as needed
        spawnChunkLock.clear();
        if (keepLoaded) {
            int centerX = spawnLocation.getBlockX() >> 4;
            int centerZ = spawnLocation.getBlockZ() >> 4;
            int radius = 4 * server.getViewDistance() / 3;

            for (int x = centerX - radius; x <= centerX + radius; ++x) {
                for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                    loadChunk(x, z);
                    spawnChunkLock.acquire(new GlowChunk.Key(x, z));
                }
            }
        } else {
            // attempt to immediately unload the spawn
            chunks.unloadOldChunks();
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
        return GlowChunk.DEPTH;
    }

    @Override
    public int getSeaLevel() {
        return getMaxHeight() / 2;
    }

    @Override
    public WorldType getWorldType() {
        return worldType;
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
        EventFactory.onWorldSave(this);

        // save metadata
        writeWorldData(async);

        // save chunks
        maybeAsync(async, new Runnable() {
            @Override
            public void run() {
                for (GlowChunk chunk : chunks.getLoadedChunks()) {
                    chunks.performSave(chunk);
                }
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // get block, chunk, id, highest methods with coords

    @Override
    public GlowBlock getBlockAt(int x, int y, int z) {
        return new GlowBlock(getChunkAt(x >> 4, z >> 4), x, y & 0xff, z);
    }

    @Override
    public int getBlockTypeIdAt(int x, int y, int z) {
        return getChunkAt(x >> 4, z >> 4).getType(x & 0xF, z & 0xF, y);
    }

    @Override
    public int getHighestBlockYAt(int x, int z) {
        for (int y = getMaxHeight() - 1; y >= 0; --y) {
            if (getBlockTypeIdAt(x, y, z) != 0) {
                return y + 1;
            }
        }
        return 0;
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

    @Override
    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX() >> 4, location.getBlockZ() >> 4);
    }

    @Override
    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX() >> 4, block.getZ() >> 4);
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
        getChunkAt(x, z).load();
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
    public boolean unloadChunkRequest(final int x, final int z, final boolean safe) {
        if (safe && isChunkInUse(x, z)) return false;

        server.getScheduler().runTask(null, new Runnable() {
            @Override
            public void run() {
                unloadChunk(x, z, safe);
            }
        });

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

        GlowChunk.Key key = new GlowChunk.Key(x, z);
        boolean result = false;

        for (GlowPlayer player : getRawPlayers()) {
            if (player.canSee(key)) {
                player.getSession().send(getChunkAt(x, z).toMessage());
                result = true;
            }
        }

        return result;
    }

    @Override
    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return new GlowChunkSnapshot.EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
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
        getChunkAt(x >> 4, z >> 4).setBiome(x & 0xF, z & 0xF, GlowBiome.getId(bio));
    }

    @Override
    public double getTemperature(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getHumidity(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity spawning

    @Override
    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Item dropItem(Location location, ItemStack item) {
        return new GlowItem(location, item);
    }

    @Override
    public Item dropItemNaturally(Location location, ItemStack item) {
        double xs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        location = location.clone().add(xs, ys, zs);
        return dropItem(location, item);
    }

    @Override
    public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
        Arrow arrow = spawn(location, Arrow.class);

        // Transformative magic
        Vector randVec = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
        randVec.multiply(0.0075 * (double) spread);

        velocity.normalize();
        velocity.add(randVec);
        velocity.multiply(speed);

        // yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
        // pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D

        arrow.setVelocity(velocity);
        return arrow;
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException {
        return null;
    }

    @Override
    public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Entity spawnEntity(Location loc, EntityType type) {
        return spawn(loc, type.getEntityClass());
    }

    @Override
    @Deprecated
    public LivingEntity spawnCreature(Location loc, EntityType type) {
        return (LivingEntity) spawn(loc, type.getEntityClass());
    }

    @Override
    @Deprecated
    public LivingEntity spawnCreature(Location loc, CreatureType type) {
        return (LivingEntity) spawn(loc, type.getEntityClass());
    }

    @Override
    public GlowLightningStrike strikeLightning(Location loc) {
        return new GlowLightningStrike(loc, false);
    }

    @Override
    public GlowLightningStrike strikeLightningEffect(Location loc) {
        return new GlowLightningStrike(loc, true);
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

        for (GlowPlayer player : getRawPlayers()) {
            player.sendTime();
        }
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
        currentlyRaining = hasStorm;

        // Numbers borrowed from CraftBukkit.
        if (currentlyRaining) {
            setWeatherDuration(random.nextInt(12000) + 12000);
        } else {
            setWeatherDuration(random.nextInt(168000) + 12000);
        }

        // update players
        for (GlowPlayer player : getRawPlayers()) {
            player.sendWeather();
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
            setThunderDuration(random.nextInt(12000) + 3600);
        } else {
            setThunderDuration(random.nextInt(168000) + 12000);
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
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Effects

    @Override
    public void playEffect(Location location, Effect effect, int data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public void playEffect(Location location, Effect effect, int data, int radius) {
        radius *= radius;
        for (Player player : getRawPlayers()) {
            if (player.getLocation().distanceSquared(location) <= radius) {
                player.playEffect(location, effect, data);
            }
        }
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data) {
        playEffect(location, effect, data, 64);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T data, int radius) {
        int rawData = 0;
        playEffect(location, effect, rawData, radius);
    }

    public void playEffectExceptTo(Location location, Effect effect, int data, int radius, Player exclude) {
        radius *= radius;
        for (Player player : getRawPlayers()) {
            if (!player.equals(exclude) && player.getLocation().distanceSquared(location) <= radius) {
                player.playEffect(location, effect, data);
            }
        }
    }

    @Override
    public void playSound(Location location, Sound sound, float volume, float pitch) {
        if (location == null || sound == null) return;

        final double radiusSquared = Math.pow(Math.min(volume * 16, 16), 2);
        for (Player player : getRawPlayers()) {
            if (player.getLocation().distanceSquared(location) <= radiusSquared) {
                player.playSound(location, sound, volume, pitch);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Level data write

    /**
     * Save the world data using the metadata service.
     * @param async Whether to write asynchronously.
     */
    private void writeWorldData(boolean async) {
        maybeAsync(async, new Runnable() {
            @Override
            public void run() {
                try {
                    storageProvider.getMetadataService().writeWorldData();
                } catch (IOException e) {
                    server.getLogger().severe("Could not save metadata for world: " + getName());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Execute a runnable, optionally asynchronously.
     * @param async Whether to run the runnable in an asynchronous task.
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
     * @return true if successful
     */
    public boolean unload() {
        try {
            storageProvider.getChunkIoService().unload();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Get the storage provider for the world.
     * @return The {@link WorldStorageProvider}.
     */
    public WorldStorageProvider getStorage() {
        return storageProvider;
    }

    /**
     * Get the world folder.
     * @return world folder
     */
    @Override
    public File getWorldFolder() {
        return storageProvider.getFolder();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Game rules

    @Override
    public String[] getGameRules() {
        return gameRules.keySet().toArray(new String[gameRules.size()]);
    }

    @Override
    public String getGameRuleValue(String rule) {
        return gameRules.get(rule);
    }

    @Override
    public boolean setGameRuleValue(String rule, String value) {
        if (value == null || !gameRules.containsKey(rule)) {
            return false;
        } else {
            gameRules.put(rule, value);
            return true;
        }
    }

    @Override
    public boolean isGameRule(String rule) {
        return gameRules.containsKey(rule);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        metadata.setMetadata(this, metadataKey, newMetadataValue);
    }

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

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(server.getMessenger(), source, channel, message);
        for (Player player : getRawPlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        HashSet<String> result = new HashSet<>();
        for (Player player : getRawPlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }
}
