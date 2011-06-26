package net.glowstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.glowstone.block.GlowBlock;
import net.glowstone.io.ChunkIoService;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.EntityManager;
import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.LoadChunkMessage;
import net.glowstone.msg.StateChangeMessage;
import net.glowstone.msg.TimeMessage;

/**
 * A class which represents the in-game world.
 * @author Graham Edgecombe
 */
public final class GlowWorld implements World {
    
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
    private final HashMap<Location, GlowBlock> blockCache = new HashMap<Location, GlowBlock>();
    
    /**
     * The world populators for this world.
     */
    private final List<BlockPopulator> populators;

    /**
     * The spawn position.
     */
    private Location spawnLocation;
    
    /**
     * The environment.
     */
    private final Environment environment;
    
    /**
     * The world seed.
     */
    private final long seed;
    
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
     * Creates a new world with the specified chunk I/O service, environment,
     * and world generator.
     * @param name The name of the world.
     * @param service The chunk I/O service.
     * @param environment The environment.
     * @param generator The world generator.
     */
    public GlowWorld(String name, Environment environment, long seed, ChunkIoService service, ChunkGenerator generator) {
        this.name = name;
        this.environment = environment;
        this.seed = seed;
        chunks = new ChunkManager(this, service, generator);
        
        populators = generator.getDefaultPopulators(this);
        spawnLocation = generator.getFixedSpawnLocation(this, random);
        
        int centerX = (spawnLocation == null) ? 0 : spawnLocation.getBlockX() >> 4;
        int centerZ = (spawnLocation == null) ? 0 : spawnLocation.getBlockZ() >> 4;
        
        GlowServer.logger.log(Level.INFO, "Preparing spawn for {0}", name);
        long loadTime = new Date().getTime();
        
        int radius = 4 * GlowChunk.VISIBLE_RADIUS / 3;
        
        for (int x = centerX - radius; x <= centerX + radius; ++x) {
            for (int z = centerZ - radius; z <= centerZ + radius; ++z) {
                chunks.getChunk(x, z);
            
                if (new Date().getTime() >= loadTime + 1000) {
                    int progress = 100 * (x - centerX + radius) / (2 * radius);
                    GlowServer.logger.log(Level.INFO, "Preparing spawn for {0}: {1}%", new Object[]{name, progress});
                }
            }
        }
        GlowServer.logger.log(Level.INFO, "Preparing spawn for {0}: done", name);
        
        if (spawnLocation == null) {
            spawnLocation = new Location(this, 0, 128, 0);
            
            if (!generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ())) {
                // 10 tries only to prevent a return false; bomb
                for (int tries = 0; tries < 10 && !generator.canSpawn(this, spawnLocation.getBlockX(), spawnLocation.getBlockZ()); ++tries) {
                    spawnLocation.setX(spawnLocation.getX() + random.nextDouble() * 128 - 64);
                    spawnLocation.setZ(spawnLocation.getZ() + random.nextDouble() * 128 - 64);
                }
            }
            
            spawnLocation.setY(1 + getHighestBlockYAt(spawnLocation.getBlockX(), spawnLocation.getBlockZ()));
        }
        
        setStorm(false);
        setThundering(false);
    }

    ////////////////////////////////////////
    // Various internal mechanisms

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
                
                // TODO: lightning.
                // strikeLightning(new Location(this, x, z, y));
            }
        }
        
        if (--saveTimer <= 0) {
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
        spawnLocation = new Location(this, x, y, z);
        return true;
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

    public String getName() {
        return name;
    }

    public long getId() {
        return (getSeed() + "_" + getName()).hashCode();
    }

    // force-save

    public void save() {
        for (GlowChunk chunk : chunks.getLoadedChunks()) {
            chunks.forceSave(chunk.getX(), chunk.getZ());
        }
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

    public GlowBlock getBlockAt(int x, int y, int z) {
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
                return y;
            }
        }
        return 0;
    }

    public GlowChunk getChunkAt(int x, int z) {
        GlowChunk ret = chunks.getChunk(x, z);
        return ret;
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

    public Chunk getChunkAt(Location location) {
        return getChunkAt(location.getBlockX(), location.getBlockZ());
    }

    public Chunk getChunkAt(Block block) {
        return getChunkAt(block.getX(), block.getZ());
    }

    // Chunk loading and unloading

    public boolean isChunkLoaded(Chunk chunk) {
        return isChunkLoaded(chunk.getX(), chunk.getZ());
    }

    public boolean isChunkLoaded(int x, int z) {
        return chunks.isLoaded(x, z);
    }

    public Chunk[] getLoadedChunks() {
        return chunks.getLoadedChunks();
    }

    public void loadChunk(Chunk chunk) {
        loadChunk(chunk.getX(), chunk.getZ());
    }

    public void loadChunk(int x, int z) {
        // Force load by getting chunk.
        chunks.getChunk(x, z);
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        if (generate) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            loadChunk(x, z);
            return true;
        }
    }

    public boolean unloadChunk(int x, int z) {
        return unloadChunk(x, z, true);
    }

    public boolean unloadChunk(int x, int z, boolean save) {
        return chunks.unloadChunk(x, z, save);
    }

    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        if (!safe) {
            throw new UnsupportedOperationException("unloadChunk does not yet support unsafe unloading.");
        }
        return unloadChunk(x, z, save);
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

    public Minecart spawnMinecart(Location location) {
        return spawn(location, Minecart.class);
    }

    public StorageMinecart spawnStorageMinecart(Location location) {
        return spawn(location, StorageMinecart.class);
    }

    public PoweredMinecart spawnPoweredMinecart(Location location) {
        return spawn(location, PoweredMinecart.class);
    }

    public Boat spawnBoat(Location location) {
        return spawn(location, Boat.class);
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
            default:
                throw new IllegalArgumentException();
        }
    }

    public LightningStrike strikeLightning(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LightningStrike strikeLightningEffect(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
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
            player.getSession().send(new StateChangeMessage((byte)(currentlyRaining ? 1 : 2)));
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
    
    // misc

    public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
