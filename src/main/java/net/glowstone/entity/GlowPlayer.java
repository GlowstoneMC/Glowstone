package net.glowstone.entity;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Achievement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;

import org.bukkit.entity.Player;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.msg.ChatMessage;
import net.glowstone.msg.DestroyEntityMessage;
import net.glowstone.msg.LoadChunkMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.PingMessage;
import net.glowstone.msg.PlayNoteMessage;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.msg.RespawnMessage;
import net.glowstone.msg.SpawnPositionMessage;
import net.glowstone.msg.StateChangeMessage;
import net.glowstone.net.Session;
import org.bukkit.ChatColor;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
public final class GlowPlayer extends GlowHumanEntity implements Player {

    /**
     * The normal height of a player's eyes above their feet.
     */
	public static final double EYE_HEIGHT = 1.62D;

    /**
     * This player's session.
     */
	private final Session session;
    
    /**
     * The display name of this player, for chat purposes.
     */
    private String displayName;
    
    /**
     * The player's compass target.
     */
    private Location compassTarget;

    /**
     * The entities that the client knows about.
     */
	private Set<GlowEntity> knownEntities = new HashSet<GlowEntity>();

    /**
     * The chunks that the client knows about.
     */
	private final Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
	public GlowPlayer(Session session, String name) {
		super(session.getServer(), (GlowWorld) session.getServer().getWorlds().get(0), name);
		this.session = session;

		streamBlocks(); // stream the initial set of blocks
        setCompassTarget(world.getSpawnLocation()); // set our compass target
		teleport(world.getSpawnLocation()); // take us to spawn position
        session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2))); // send the world's weather
    }

	@Override
	public void pulse() {
		super.pulse();
        session.send(new PingMessage());

		streamBlocks();

		for (Iterator<GlowEntity> it = knownEntities.iterator(); it.hasNext(); ) {
			GlowEntity entity = it.next();
			boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

			if (withinDistance) {
				Message msg = entity.createUpdateMessage();
				if (msg != null)
					session.send(msg);
			} else {
				session.send(new DestroyEntityMessage(entity.getEntityId()));
				it.remove();
			}
		}

		for (GlowEntity entity : world.getEntityManager()) {
			if (entity == this)
				continue;
			boolean withinDistance = !entity.isDead() && isWithinDistance(entity);

			if (withinDistance && !knownEntities.contains(entity)) {
				knownEntities.add(entity);
				session.send(entity.createSpawnMessage());
			}
		}
	}

    /**
     * Streams chunks to the player's client.
     */
	private void streamBlocks() {
        Set<GlowChunk.Key> previousChunks = new HashSet<GlowChunk.Key>(knownChunks);

		int centralX = ((int) location.getX()) >> 4;
		int centralZ = ((int) location.getZ()) >> 4;
        
        for (int x = (centralX - GlowChunk.VISIBLE_RADIUS); x <= (centralX + GlowChunk.VISIBLE_RADIUS); x++) {
            for (int z = (centralZ - GlowChunk.VISIBLE_RADIUS); z <= (centralZ + GlowChunk.VISIBLE_RADIUS); z++) {
                GlowChunk.Key key = new GlowChunk.Key(x, z);
                if (!knownChunks.contains(key)) {
                    knownChunks.add(key);
                    session.send(new LoadChunkMessage(x, z, true));
                    session.send(world.getChunkManager().getChunk(x, z).toMessage());
                }
                previousChunks.remove(key);
            }
        }

        for (GlowChunk.Key key : previousChunks) {
            session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            knownChunks.remove(key);
        }

        previousChunks.clear();
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
     * Teleport the player.
     * @param location The destination to teleport to.
     * @return Whether the teleport was a success.
     */
    @Override
    public boolean teleport(Location location) {
        if (location.getWorld() != world) {
            world.getEntityManager().deallocate(this);
            
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);
            
            for (GlowChunk.Key key : knownChunks) {
                session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
            }
            knownChunks.clear();
            
            session.send(new RespawnMessage((byte) world.getEnvironment().getId()));
            
            streamBlocks(); // stream blocks
            
            setCompassTarget(world.getSpawnLocation()); // set our compass target
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location; // take us to spawn position
            session.send(new StateChangeMessage((byte)(getWorld().hasStorm() ? 1 : 2))); // send the world's weather
            reset();
        } else {
            this.session.send(new PositionRotationMessage(location.getX(), location.getY() + EYE_HEIGHT + 0.01, location.getZ(), location.getY(), (float) location.getYaw(), (float) location.getPitch(), true));
            this.location = location;
            reset();
        }
        
        return true;
    }

    /**
     * Gets the session.
     * @return The session.
     */
	public Session getSession() {
		return session;
	}

    public boolean isOnline() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public void setCompassTarget(Location loc) {
        compassTarget = loc;
        session.send(new SpawnPositionMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    public Location getCompassTarget() {
        return compassTarget;
    }

    public InetSocketAddress getAddress() {
        return session.getAddress();
    }

    public void sendRawMessage(String message) {
        session.send(new ChatMessage(message));
    }

    public void kickPlayer(String message) {
        session.disconnect(message);
    }

    /**
     * Says a message (or runs a command).
     *
     * @param text message to print
     */
    public void chat(String text) {
        if (text.startsWith("/")) {
			try {
                if (!performCommand(text.substring(1))) {
                    sendMessage(ChatColor.RED + "Your command could not be executed.");
                }
            }
            catch (Exception ex) {
                sendMessage(ChatColor.RED + "An exception occured while executing your command.");
                getServer().getLogger().log(Level.SEVERE, "Error while executing command: {0}", ex.getMessage());
                ex.printStackTrace();
            }
		} else {
            getServer().broadcastMessage("<" + getName() + "> " + text);
            getServer().getLogger().log(Level.INFO, "<{0}> {1}", new Object[]{getName(), text});
		}
    }

    public boolean performCommand(String command) {
        return getServer().dispatchCommand(this, command);
    }

    public boolean isSneaking() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSneaking(boolean sneak) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void saveData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSleepingIgnored(boolean isSleeping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSleepingIgnored() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void playNote(Location loc, byte instrument, byte note) {
        session.send(new PlayNoteMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), instrument, note));
    }

    public void sendBlockChange(Location loc, Material material, byte data) {
        sendBlockChange(loc, material.getId(), data);
    }

    public void sendBlockChange(Location loc, int material, byte data) {
        session.send(new BlockChangeMessage(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), material, data));
    }

    public void updateInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void awardAchievement(Achievement achievement) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void incrementStatistic(Statistic statistic) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void incrementStatistic(Statistic statistic, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void incrementStatistic(Statistic statistic, Material material) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendMessage(String message) {
        sendRawMessage(message);
    }

    public boolean isOp() {
        return getServer().getOpsList().contains(getName());
    }

}
