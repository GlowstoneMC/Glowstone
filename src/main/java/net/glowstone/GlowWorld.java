package net.glowstone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.World;

import net.glowstone.io.ChunkIoService;
import net.glowstone.ChunkManager;
import net.glowstone.entity.Entity;
import net.glowstone.entity.EntityManager;
import net.glowstone.entity.Player;
import net.glowstone.msg.ChatMessage;
import net.glowstone.world.WorldGenerator;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * A class which represents the in-game world.
 * @author Graham Edgecombe
 */
public class GlowWorld implements World {

	/**
	 * The spawn position.
	 */
	private final Location spawnLocation = new Location(null, 0, 128, 0);

	/**
	 * The chunk manager.
	 */
	private final ChunkManager chunks;

	/**
	 * The entity manager.
	 */
	private final EntityManager entities = new EntityManager();

	/**
	 * Creates a new world with the specified chunk I/O service and world
	 * generator.
	 * @param service The chunk I/O service.
	 * @param generator The world generator.
	 */
	public GlowWorld(ChunkIoService service, WorldGenerator generator) {
		chunks = new ChunkManager(service, generator);
	}

    ////////////////////////////////////////
    // Various internal mechanisms

	/**
	 * Updates all the entities within this world.
	 */
	public void pulse() {
		for (Entity entity : entities)
			entity.pulse();

		for (Entity entity : entities)
			entity.reset();
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

	public Collection<Player> getRawPlayers() {
        return entities.getAll(Player.class);
	}

	/**
	 * Broadcasts a message to every player.
	 * @param text The message text.
	 */
	public void broadcastMessage(String text) {
		ChatMessage message = new ChatMessage(text);
		for (Player player : getRawPlayers())
			player.getSession().send(message);
	}

    // Entity lists
	
	public List<org.bukkit.entity.Player> getPlayers() {
		/*
        Collection<Player> players = entities.getAll(Player.class);
        List<Player> result = new ArrayList<Player>();
        for (Player p : players) {
            result.add(p);
        }
        return result;
         */
        throw new UnsupportedOperationException("Not supported yet.");
	}

    public List<org.bukkit.entity.Entity> getEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<LivingEntity> getLivingEntities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	// Spawn location

	public Location getSpawnLocation() {
		return spawnLocation;
	}

    public boolean setSpawnLocation(int x, int y, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Pvp on/off

    public boolean getPVP() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPVP(boolean pvp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // force-save

    public void save() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // various fixed world properties

    public Environment getEnvironment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getSeed() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // get block, chunk, id, highest methods with coords

    public Block getBlockAt(int x, int y, int z) {
        return getChunkAt(x >> 4, z >> 4).getBlock(x & 0xF, y & 0x7F, z & 0xF);
    }

    public int getBlockTypeIdAt(int x, int y, int z) {
        return ((GlowChunk)getChunkAt(x >> 4, z >> 4)).getType(x & 0xF, z & 0xF, y & 0x7F);
    }

    public int getHighestBlockYAt(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Chunk getChunkAt(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // get block, chunk, id, highest with locations

    public Block getBlockAt(Location location) {
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isChunkLoaded(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Chunk[] getLoadedChunks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadChunk(Chunk chunk) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean loadChunk(int x, int z, boolean generate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean unloadChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean unloadChunk(int x, int z, boolean save) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean unloadChunk(int x, int z, boolean save, boolean safe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean unloadChunkRequest(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean unloadChunkRequest(int x, int z, boolean safe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean regenerateChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean refreshChunk(int x, int z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    // Map gen related things

    public boolean generateTree(Location location, TreeType type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Entity spawning

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setTime(long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getFullTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFullTime(long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Weather related methods

    public boolean hasStorm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setStorm(boolean hasStorm) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getWeatherDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setWeatherDuration(int duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isThundering() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setThundering(boolean thundering) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getThunderDuration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setThunderDuration(int duration) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
