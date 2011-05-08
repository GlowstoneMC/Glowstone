package net.glowstone.msg.handler;

import net.glowstone.entity.Player;
import net.glowstone.msg.DiggingMessage;
import net.glowstone.GlowChunk;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.net.Session;
import net.glowstone.World;

import org.bukkit.Material;

/**
 * A {@link MessageHandler} which processes digging messages.
 * @author Zhuowei Zhang
 */
public final class DiggingMessageHandler extends MessageHandler<DiggingMessage> {

	@Override
	public void handle(Session session, Player player, DiggingMessage message) {
		if (player == null)
			return;

		if (message.getState() == DiggingMessage.STATE_DONE_DIGGING) {
			World world = player.getWorld();

			int x = message.getX();
			int z = message.getZ();
			int y = message.getY();

			// TODO it might be nice to move these calculations somewhere else since they will need to be reused
			int chunkX = x / GlowChunk.WIDTH + ((x < 0 && x % GlowChunk.WIDTH != 0) ? -1 : 0);
			int chunkZ = z / GlowChunk.HEIGHT + ((z < 0 && z % GlowChunk.HEIGHT != 0) ? -1 : 0);

			int localX = (x - chunkX * GlowChunk.WIDTH) % GlowChunk.WIDTH;
			int localZ = (z - chunkZ * GlowChunk.HEIGHT) % GlowChunk.HEIGHT;

			GlowChunk chunk = world.getChunks().getChunk(chunkX, chunkZ);
			chunk.setType(localX, localZ, y, Material.AIR.getId());

			// TODO this should also be somewhere else as well... perhaps in the chunk.setType() method itself?
			BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, 0, 0);
			for (Player p: world.getPlayers()) {
				p.getSession().send(bcmsg);
			}
		}
	}

}
