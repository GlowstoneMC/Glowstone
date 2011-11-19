package net.glowstone;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.glowstone.entity.*;
import net.glowstone.io.StorageOperation;
import net.glowstone.io.WorldMetadataService;
import net.glowstone.io.WorldMetadataService.WorldFinalValues;
import net.glowstone.io.WorldStorageProvider;
import net.glowstone.block.GlowBlock;
import net.glowstone.msg.LoadChunkMessage;
import net.glowstone.msg.StateChangeMessage;
import net.glowstone.msg.TimeMessage;

/**
 * A class which represents the in-game world.
 * @author Graham Edgecombe
 */
public final class GlowWorld implements World {
    
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
     * The entity manager.
     */
    private final EntityManager entities = new EntityManager();
    
    /**
     * This world's Random instance.
     */
    private final Random random = new Random();
    
    /**
     * A map between locations and cached Block objects.
     */
    private final Map<Location, GlowBlock> blockCache = new HashMap<Location, GlowBlock>();
    
    /**
     * The world populators for this world.
     */
    private final List<BlockPopulator> populators;
    
    /**
     * The environment.
     */
    private final Environment environment;
    
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
     * The current world time.
     */
    private long time = 0;
    
    /**
     * The time until the next full-save.
     */
    private int saveTimer = 0;

    /**
     * The check to autosave
     */
    private boolean autosave = true;

    /*
     * The world metadata service used
     */
    private final WorldMetadataService metadataService;

    /**
     * The world's UUID
     */
    private final UUID uid;

    /**
     * Creates a new world with the specified chunk I/O service, environment,
     * and world generator.
     * @param name The name of the world.
     * @param provider The world storage provider
     * @param environment The environment.
     * @param generator The world generator.
     */
    public GlowWorld(GlowServer server, String name, Environment environment, long seed, WorldStorageProvider provider, ChunkGenerator generator) {
        this.server = server;
        this.name = name;
        this.environment = environment;
        provider.setWorld(this);
        chunks = new ChunkManager(this, provider.getChunkIoService(), generator);
        metadataService = provider.getMetadataService();
        EventFactory.onWorldInit(this);
        WorldFinalValues values = null;
        try {
            values = metadataService.readWorldData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Extra checks for seed
        if (values != null) {
            if (values.getSeed() == 0L) {
                this.seed = seed;
            } else {
                this.seed = values.getSeed();
            }
            this.uid = values.getUuid();
        } else {
            this.seed = seed;
            this.uid = UUID.randomUUID();
        }
        populators = generator.getDefaultPopulators(this);
        if (spawnLocation == null) spawnLocation = generator.getFixedSpawnLocation(this, random);

        int centerX = (spawnLocation == null) ? 0 : spawnLocation.getBlockX() >> 4;
        int centerZ = (spawnLocation == null) ? 0 : spawnLocation.getBlockZ() >> 4;
        
        server.getLogger().log(Level.INFO, "Preparing spawn for {0}", name);
        long loadTime = System.currentTimeMillis();
        
        int radius = 4 * server.getViewDistance() / 3;
        
        int total = (radius * 2 + 1) * (radius * 2 + 1), current = 0;
        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                ++current;
                loadChunk(x, z);
            
                if (System.currentTimeMillis() >= loadTime + 1000) {
                    int progress = 100 * current / total;
                    GlowServer.logger.log(Level.INFO, "Preparing spawn for {0}: {1}%", new Object[]{name, progress});
                    loadTime = System.currentTimeMillis();
                }
            }
        }
        server.getLogger().log(Level.INFO, "Preparing spawn for {0}: done", name);
        if (spawnLocation == null) {
            spawnLocation = generator.getFixedSpawnLocation(this, random);
            if (spawnLocation == null) {
                spawnLocation = new Location(this, 0, getHighestBlockYAt(0, 0), 0);

                if (!generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ())) {
                    // 10 tries only to prevent a return false; bomb
                    for (int tries = 0; tries < 10 && !generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ()); ++tries) {
                        spawnLocation.setX(spawnLocation.getX() + random.nextDouble() * 128 - 64);
                        spawnLocation.setZ(spawnLocation.getZ() + random.nextDouble() * 128 - 64);
                    }
                }

                spawnLocation.setY(1 + getHighestBlockYAt(spawnLocation.getBlockX(), spawnLocation.getBlockZ()));
            }
        }
        EventFactory.onWorldLoad(this);
        save();

    }

    ////////////////////////////////////////
    // Various internal mechanisms
    
    /**
     * Get the world chunk manager.
     * @return The ChunkManager for the world.
     */
    protected ChunkManager getChunkManager() {
        return chunks;
    }

    /**
     * Updates all the entities within this world.
     */
    public void pulse() {
        ArrayList<GlowEntity> temp = new ArrayList<GlowEntity>(entities.getAll());
        
        for (GlowEntity entity : temp)
            entity.pulse();

        for (GlowEntity entity : temp)
            entity.reset();
        
        // We currently tick at 1/4 the speed of regular MC
        // Modulus by 12000 to force permanent day.
        time = (time + 1) % 12000;
        if (time % 12 == 0) {
            // Only send the time every so often; clients are smart.
            for (GlowPlayer player : getRawPlayers()) {
                player.getSession().send(new TimeMessage(player.getPlayerTime()));
            }
        }
        
        if (--rainingTicks <= 0) {
            setStorm(!currentlyRaining);
        }
        
        if (--thunderingTicks <= 0) {
            setThundering(!currentlyThundering);
        }
        
        if (currentlyRaining && currentlyThundering) {
            if (random.nextDouble() < .01) {
                GlowChunk[] chunkList = chunks.getLoadedChunks();
                GlowChunk chunk = chunkList[random.nextInt(chunkList.length)];
                
                int x = (chunk.getX() << 4) + (int)(random.nextDouble() * 16);
                int z = (chunk.getZ() << 4) + (int)(random.nextDouble() * 16);
                int y = getHighestBlockYAt(x, z);
                
                strikeLightning(new Location(this, x, y, z));
            }
        }
        
        if (autosave && --saveTimer <= 0) {
            saveTimer = 60 * 20;
            save();
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

    // GlowEntity lists
    
    public List<Player> getPlayers() {
        Collection<GlowPlayer> players = entities.getAll(GlowPlayer.class);
        ArrayList<Player> result = new ArrayList<Player>();
        for (Player p : players) {
            result.add(p);
        }
        return result;
    }

    public List<Entity> getEntities() {
        Collection<GlowEntity> list = entities.getAll();
        ArrayList<Entity> result = new ArrayList<Entity>();
        for (Entity e : list) {
            result.add(e);
        }
        return result;
    }

    public List<LivingEntity> getLivingEntities() {
        Collection<GlowEntity> list = entities.getAll();
        ArrayList<LivingEntity> result = new ArrayList<LivingEntity>();
        for (Entity e : list) {
            if (e instanceof GlowLivingEntity) result.add((GlowLivingEntity) e);
        }
        return result;
    }

    // Various malleable world properties

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public boolean setSpawnLocation(int x, int y, int z) {
        return setSpawnLocation(new Location(this, x, y, z));
    }

    public boolean setSpawnLocation(Location loc) {
        Location oldSpawn = spawnLocation;
        loc.setWorld(this);
        spawnLocation = loc;
        EventFactory.onSpawnChange(this, oldSpawn);
        return !loc.equals(oldSpawn);
    }

    public boolean getPVP() {
        return pvpAllowed;
    }

    public void setPVP(boolean pvp) {
        pvpAllowed = pvp;
    }

    public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals) {
        spawnMonsters = allowMonsters;
        spawnAnimals = allowAnimals;
    }

    public boolean getAllowAnimals() {
        return spawnAnimals;
    }

    public boolean getAllowMonsters() {
        return spawnMonsters;
    }

    // various fixed world properties

    public Environment getEnvironment() {
        return environment;
    }

    public long getSeed() {
        return seed;
    }

    public UUID getUID() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return (getSeed() + "_" + getName()).hashCode();
    }

    public int getMaxHeight() {
        return GlowChunk.DEPTH;
    }

    public int getSeaLevel() {
        return getMaxHeight() / 2;
    }

    // force-save

    public void save() {
        EventFactory.onWorldSave(this);
        server.getStorageQueue().queue(new StorageOperation() {
            @Override
            public boolean isParallel() {
                return true;
            }

            @Override
            public String getGroup() {
                return getName();
            }

            @Override
            public String getOperation() {
                return "world-save";
            }

            @Override
            public boolean queueMultiple() {
                return false;
            }

            public void run() {
                for (GlowChunk chunk : chunks.getLoadedChunks()) {
                    chunks.forceSave(chunk.getX(), chunk.getZ());
                }
            }
        });
        
        for (GlowPlayer player : getRawPlayers()) {
            player.saveData();
        }

        writeWorldData();
    }
    
    // map generation

    public ChunkGenerator getGenerator() {
        return chunks.getGenerator();
    }

    public List<BlockPopulator> getPopulators() {
        return populators;
    }

    public boolean generateTree(Location location, TreeType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // get block, chunk, id, highest methods with coords

    public synchronized GlowBlock getBlockAt(int x, int y, int z) {
        Location blockLoc = new Location(this, x, y, z);
        if (blockCache.containsKey(blockLoc)) {
            return blockCache.get(blockLoc);
        } else {
            GlowBlock block = new GlowBlock(getChunkAt(x >> 4, z >> 4), x, y, z);
            blockCache.put(blockLoc, block);
            return block;
        }
    }

    public int getBlockTypeIdAt(int x, int y, int z) {
        return ((GlowChunk) getChunkAt(x >> 4, z >> 4)).getType(x & 0xF, z & 0xF, y & 0x7F);
    }

    public int getHighestBlockYAt(int x, int z) {
        for (int y = GlowChunk.DEPTH - 1; y >= 0; --y) {
            if (getBlockTypeIdAt(x, y, z) != 0) {
                return y + 1;
            }
        }
        return 0;
    }

    public synchronized GlowChunk getChunkAt(int x, int z) {
        return chunks.getChunk(x, z);
    }

    // get block, chunk, id, highest with locations

    public GlowBlock getBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getBlockTypeIdAt(Location location) {
        return getBlockTypeIdAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getHighestBlockYAt(Location location) {
        return getHighestBlockYAt(location.getBlockX(), location.getBlockZ());
    }

    public Block getHighestBlockAt(int x, int z) {
        return getBlockAt(x, getHighestBlockYAt(x, z), z);
    }

    public Block getHighestBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), getHighestBlockYAt(location), location.getBlockZ());
    }

    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX(), location.getBlockZ());
    }

    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX(), block.getZ());
    }

    // Chunk loading and unloading

    public boolean isChunkLoaded(Chunk chunk) {
        return chunk.isLoaded();
    }

    public boolean isChunkLoaded(int x, int z) {
        return getChunkAt(x, z).isLoaded();
    }

    public Chunk[] getLoadedChunks() {
        return chunks.getLoadedChunks();
    }

    public void loadChunk(Chunk chunk) {
        chunk.load();
    }

    public void loadChunk(int x, int z) {
        getChunkAt(x, z).load();
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        if (generate) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            loadChunk(x, z);
            return true;
        }
    }

    public boolean unloadChunk(Chunk chunk) {
        return unloadChunk(chunk.getX(), chunk.getZ(), true);
    }

    public boolean unloadChunk(int x, int z) {
        return unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean save) {
        return unloadChunk(x, z, save, true);
    }

    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        if (!safe) {
            throw new UnsupportedOperationException("unloadChunk does not yet support unsafe unloading.");
        }
        if (save) {
            getChunkManager().forceSave(x, z);
        }
        return unloadChunkRequest(x, z, safe);
    }

    public boolean unloadChunkRequest(int x, int z) {
        return unloadChunkRequest(x, z, true);
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean regenerateChunk(int x, int z) {
        if (!chunks.forceRegeneration(x, z)) return false;
        refreshChunk(x, z);
        return true;
    }

    public boolean refreshChunk(int x, int z) {
        if (!isChunkLoaded(x, z)) {
            return false;
        }
        
        GlowChunk.Key key = new GlowChunk.Key(x, z);
        boolean result = false;
        
        for (Player p : getPlayers()) {
            GlowPlayer player = (GlowPlayer) p;
            if (player.canSee(key)) {
                player.getSession().send(new LoadChunkMessage(x, z, false));
                player.getSession().send(new LoadChunkMessage(x, z, true));
                player.getSession().send(getChunkAt(x, z).toMessage());
                result = true;
            }
        }
        
        return result;
    }
    
    // biomes

    public Biome getBiome(int x, int z) {
        if (environment == Environment.SKYLANDS) {
            return Biome.SKY;
        } else if (environment == Environment.NETHER) {
            return Biome.HELL;
        }
        
        return Biome.FOREST;
    }

    public double getTemperature(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getHumidity(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // entity spawning

    public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Item dropItem(Location location, ItemStack item) {
        // TODO: maybe spawn special due to item-ness?
        Item itemEntity = spawn(location, Item.class);
        itemEntity.setItemStack(item);
        return itemEntity;
    }

    public Item dropItemNaturally(Location location, ItemStack item) {
        double xs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double ys = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        double zs = random.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D;
        location = location.clone().add(new Location(this, xs, ys, zs));
        return dropItem(location, item);
    }

    public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
        Arrow arrow = spawn(location, Arrow.class);
        
        // Transformative magic
        Vector randVec = new Vector(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
        randVec.multiply(0.007499999832361937D * (double) spread);
        
        velocity.normalize();
        velocity.add(randVec);
        velocity.multiply(speed);
        
        // yaw = Math.atan2(x, z) * 180.0D / 3.1415927410125732D;
        // pitch = Math.atan2(y, Math.sqrt(x * x + z * z)) * 180.0D / 3.1415927410125732D
        
        arrow.setVelocity(velocity);
        return arrow;
    }

    public LivingEntity spawnCreature(Location loc, CreatureType type) {
        switch (type) {
            case CHICKEN:
                return spawn(loc, org.bukkit.entity.Chicken.class);
            case COW:
                return spawn(loc, org.bukkit.entity.Cow.class);
            case CREEPER:
                return spawn(loc, org.bukkit.entity.Creeper.class);
            case GHAST:
                return spawn(loc, org.bukkit.entity.Ghast.class);
            case GIANT:
                return spawn(loc, org.bukkit.entity.Giant.class);
            case MONSTER:
                return spawn(loc, org.bukkit.entity.Monster.class);
            case PIG:
                return spawn(loc, org.bukkit.entity.Pig.class);
            case PIG_ZOMBIE:
                return spawn(loc, org.bukkit.entity.PigZombie.class);
            case SHEEP:
                return spawn(loc, org.bukkit.entity.Sheep.class);
            case SKELETON:
                return spawn(loc, org.bukkit.entity.Skeleton.class);
            case SLIME:
                return spawn(loc, org.bukkit.entity.Slime.class);
            case SPIDER:
                return spawn(loc, org.bukkit.entity.Spider.class);
            case SQUID:
                return spawn(loc, org.bukkit.entity.Squid.class);
            case ZOMBIE:
                return spawn(loc, org.bukkit.entity.Zombie.class);
            case WOLF:
                return spawn(loc, org.bukkit.entity.Wolf.class);
            case CAVE_SPIDER:
                return spawn(loc, org.bukkit.entity.CaveSpider.class);
            case SILVERFISH:
                return spawn(loc, org.bukkit.entity.Silverfish.class);
            case ENDERMAN:
                return spawn(loc, org.bukkit.entity.Enderman.class);
            default:
                throw new IllegalArgumentException();
        }
    }

    public GlowLightningStrike strikeLightning(Location loc) {
        GlowLightningStrike strike = new GlowLightningStrike(server, this, false);
        strike.teleport(loc);
        return strike;
    }

    public GlowLightningStrike strikeLightningEffect(Location loc) {
        GlowLightningStrike strike = new GlowLightningStrike(server, this, true);
        strike.teleport(loc);
        return strike;
    }

    // Time related methods

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        if (time < 0) time = (time % 24000) + 24000;
        if (time > 24000) time %= 24000;
        this.time = time;
    }

    public long getFullTime() {
        return getTime();
    }

    public void setFullTime(long time) {
        setTime(time);
    }

    // Weather related methods

    public boolean hasStorm() {
        return currentlyRaining;
    }

    public void setStorm(boolean hasStorm) {
        currentlyRaining = hasStorm;
        
        // Numbers borrowed from CraftBukkit.
        if (currentlyRaining) {
            setWeatherDuration(random.nextInt(12000) + 12000);
        } else {
            setWeatherDuration(random.nextInt(168000) + 12000);
        }
        
        for (GlowPlayer player : getRawPlayers()) {
            player.getSession().send(new StateChangeMessage((byte)(currentlyRaining ? 1 : 2), (byte)0));
        }
    }

    public int getWeatherDuration() {
        return rainingTicks;
    }

    public void setWeatherDuration(int duration) {
        rainingTicks = duration;
    }

    public boolean isThundering() {
        return currentlyThundering;
    }

    public void setThundering(boolean thundering) {
        currentlyThundering = thundering;
        
        // Numbers borrowed from CraftBukkit.
        if (currentlyThundering) {
            setThunderDuration(random.nextInt(12000) + 3600);
        } else {
            setThunderDuration(random.nextInt(168000) + 12000);
        }
    }

    public int getThunderDuration() {
        return thunderingTicks;
    }

    public void setThunderDuration(int duration) {
        thunderingTicks = duration;
    }
    
    // explosions

    public boolean createExplosion(Location loc, float power, boolean setFire) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean createExplosion(Location loc, float power) {
        return createExplosion(loc, power, false);
    }

    public boolean createExplosion(double x, double y, double z, float power, boolean setFire) {
        return createExplosion(new Location(this, x, y, z), power, setFire);
    }

    public boolean createExplosion(double x, double y, double z, float power) {
        return createExplosion(new Location(this, x, y, z), power, false);
    }
    
    // effects

    public void playEffect(Location location, Effect effect, int data) {
        playEffect(location, effect, data, 64);
    }

    public void playEffect(Location location, Effect effect, int data, int radius) {
        for (Player player : getPlayers()) {
            if (player.getLocation().distance(location) <= radius) {
                player.playEffect(location, effect, data);
            }
        }
    }

    public void playEffectExceptTo(Location location, Effect effect, int data, int radius, Player exclude) {
        for (Player player : getPlayers()) {
            if (!player.equals(exclude) && player.getLocation().distance(location) <= radius) {
                player.playEffect(location, effect, data);
            }
        }
    }
    
    // misc

    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        return new GlowChunkSnapshot.EmptySnapshot(x, z, this, includeBiome, includeBiomeTempRain);
    }

    public boolean getKeepSpawnInMemory() {
        return keepSpawnLoaded;
    }

    public void setKeepSpawnInMemory(boolean keepLoaded) {
        keepSpawnLoaded = keepLoaded;
    }

    public boolean isAutoSave() {
        return autosave;
    }

    public void setAutoSave(boolean value) {
        autosave = value;
    }

    public void setDifficulty(Difficulty difficulty) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Difficulty getDifficulty() {
        return Difficulty.PEACEFUL;
    }

    // level data write

    void writeWorldData() {
        server.getStorageQueue().queue(new StorageOperation() {
            @Override
            public boolean isParallel() {
                return true;
            }

            @Override
            public String getGroup() {
                return getName();
            }

            @Override
            public boolean queueMultiple() {
                return false;
            }

            @Override
            public String getOperation() {
                return "world-metadata-save";
            }

            public void run() {
                try {
                    metadataService.writeWorldData();
                } catch (IOException e) {
                    server.getLogger().severe("Could not save world metadata file for world" + getName());
                    e.printStackTrace();
                }
            }
        });
    }

    public WorldMetadataService getMetadataService() {
        return metadataService;
    }
}
