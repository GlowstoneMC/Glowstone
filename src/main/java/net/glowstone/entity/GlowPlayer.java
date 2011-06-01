package net.glowstone.entity;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
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
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.net.Session;

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
     * The entities that the client knows about.
     */
	private Set<GlowEntity> knownEntities = new HashSet<GlowEntity>();

    /**
     * The chunks that the client knows about.
     */
	private Set<GlowChunk.Key> knownChunks = new HashSet<GlowChunk.Key>();

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
	public GlowPlayer(Session session, String name) {
		super((GlowWorld) session.getServer().getWorlds().get(0), name);
		this.session = session;

		// stream the initial set of blocks and teleport us
		this.streamBlocks();
		this.location = world.getSpawnLocation();
		this.session.send(new PositionRotationMessage(location.getX(), location.getY(), location.getZ(), location.getY() + EYE_HEIGHT, (float) location.getYaw(), (float) location.getPitch(), true));
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

		int centralX = ((int) location.getX()) / GlowChunk.WIDTH;
		int centralZ = ((int) location.getZ()) / GlowChunk.HEIGHT;

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Location getCompassTarget() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InetSocketAddress getAddress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendRawMessage(String message) {
        session.send(new ChatMessage(message));
    }

    public void kickPlayer(String message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Says a message (or runs a command).
     *
     * @param msg message to print
     */
    public void chat(String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean performCommand(String command) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
