package net.glowstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
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
import net.glowstone.world.FlatGrassWorldGenerator;
import net.glowstone.world.FlatNetherWorldGenerator;
import net.glowstone.world.ForestWorldGenerator;
import net.glowstone.world.WorldGenerator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

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
     * A map between locations and cached Block objects.
     */
    private final HashMap<Location, GlowBlock> blockCache = new HashMap<Location, GlowBlock>();

	/**
	 * The spawn position.
	 */
	private Location spawnLocation = new Location(this, 0, 128, 0);
    
    /**
     * Whether PvP is allowed in this world.
     */
    private boolean pvpAllowed = true;
    
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
	 * Creates a new world with the specified chunk I/O service, and environment.
     * @param name The name of the world.
	 * @param service The chunk I/O service.
	 * @param environment The environment.
	 */
	public GlowWorld(String name, ChunkIoService service, Environment environment) {
        this(name, service, environment, new Random().nextLong());
    }

	/**
	 * Creates a new world with the specified chunk I/O service, environment, and seed.
     * @param name The name of the world.
	 * @param service The chunk I/O service.
	 * @param environment The environment.
     * @param seed The seed.
	 */
	public GlowWorld(String name, ChunkIoService service, Environment environment, long seed) {
        this(name, service, environment, environment == Environment.NETHER ? new FlatNetherWorldGenerator() : new FlatGrassWorldGenerator());
	}

	/**
	 * Creates a new world with the specified chunk I/O service, environment, 
	 * and world generator.
     * @param name The name of the world.
	 * @param service The chunk I/O service.
     * @param environment The environment.
	 * @param generator The world generator.
	 */
	public GlowWorld(String name, ChunkIoService service, Environment environment, WorldGenerator generator) {
        this.name = name;
		chunks = new ChunkManager(this, service, generator);
        
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
        
        final int TIME_SCALE = 4;
        
        // We currently tick at 1/4 the speed of regular MC
        // Modulus by 12000 to force permanent day.
        time = (time + TIME_SCALE) % 12000;
        for (GlowPlayer player : getRawPlayers()) {
            player.getSession().send(new TimeMessage(time));
        }
        
        rainingTicks -= TIME_SCALE;
        if (rainingTicks <= 0) {
            setStorm(!currentlyRaining);
        }
        
        thunderingTicks -= TIME_SCALE;
        if (thunderingTicks <= 0) {
            setThundering(!currentlyThundering);
        }
        
        if (currentlyRaining && currentlyThundering) {
            if (Math.random() < .001) {
                GlowChunk[] chunkList = chunks.getLoadedChunks();
                GlowChunk chunk = chunkList[new Random().nextInt(chunkList.length)];
                
                int x = (chunk.getX() << 4) + (int)(Math.random() * 16);
                int z = (chunk.getZ() << 4) + (int)(Math.random() * 16);
                int y = getHighestBlockYAt(x, z);
                
                // strikeLightning(new Location(this, x, z, y));
                broadcastMessage(ChatColor.GREEN + "Pretend lightning struck at " + x + "," + y + "," + z);
            }
        }
	}

	/**
	 * Gets the chunk manager.
	 * @return The chunk manager.
	 */
	public ChunkManager getChunkManager() {
		return chunks;
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

	/**
	 * Broadcasts a message to every player.
	 * @param text The message text.
	 */
	public void broadcastMessage(String text) {
		for (Player player : getPlayers())
			player.sendMessage(text);
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

	// Spawn location

	public Location getSpawnLocation() {
		return spawnLocation;
	}

    public boolean setSpawnLocation(int x, int y, int z) {
        spawnLocation = new Location(this, x, y, z);
        return true;
    }
    
    // Pvp on/off

    public boolean getPVP() {
        return pvpAllowed;
    }

    public void setPVP(boolean pvp) {
        pvpAllowed = pvp;
    }

    // force-save

    public void save() {
        for (GlowChunk chunk : chunks.getLoadedChunks()) {
            chunks.forceSave(chunk.getX(), chunk.getZ());
        }
    }

    // various fixed world properties

    public Environment getEnvironment() {
        return Environment.NORMAL;
    }

    public long getSeed() {
        return chunks.getSeed();
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return (getSeed() + "_" + getName()).hashCode();
    }

    // get block, chunk, id, highest methods with coords

    public GlowBlock getBlockAt(int x, int y, int z) {
        if (blockCache.containsKey(new Location(this, x, y, z))) {
            return blockCache.get(new Location(this, x, y, z));
        } else {
            GlowBlock block = new GlowBlock(getChunkAt(x >> 4, z >> 4), x, y, z);
            blockCache.put(new Location(this, x, y, z), block);
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
        return chunks.forceRegeneration(x, z);
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
    
    // Map gen related things

    public boolean generateTree(Location location, TreeType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // GlowEntity spawning

    public Item dropItem(Location location, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Item dropItemNaturally(Location location, ItemStack item) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Arrow spawnArrow(Location location, Vector velocity, float speed, float spread) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Minecart spawnMinecart(Location location) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public StorageMinecart spawnStorageMinecart(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PoweredMinecart spawnPoweredMinecart(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boat spawnBoat(Location loc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LivingEntity spawnCreature(Location loc, CreatureType type) {
        throw new UnsupportedOperationException("Not supported yet.");
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
            setWeatherDuration(new Random().nextInt(12000) + 12000);
        } else {
            setWeatherDuration(new Random().nextInt(168000) + 12000);
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
            setThunderDuration(new Random().nextInt(12000) + 3600);
        } else {
            setThunderDuration(new Random().nextInt(168000) + 12000);
        }
    }

    public int getThunderDuration() {
        return thunderingTicks;
    }

    public void setThunderDuration(int duration) {
        thunderingTicks = duration;
    }

}
