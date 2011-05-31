package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.DiggingMessage;
import net.glowstone.GlowChunk;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.net.Session;
import net.glowstone.GlowWorld;

import org.bukkit.Material;

/**
 * A {@link MessageHandler} which processes digging messages.
 * @author Zhuowei Zhang
 */
public final class DiggingMessageHandler extends MessageHandler<DiggingMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, DiggingMessage message) {
		if (player == null)
			return;

		if (message.getState() == DiggingMessage.STATE_START_DIGGING) {
			GlowWorld world = player.getWorld();

			int x = message.getX();
			int z = message.getZ();
			int y = message.getY();
            
            world.getBlockAt(x, y, z).setType(Material.AIR);

			// TODO this should also be somewhere else as well... perhaps in the chunk.setType() method itself?
			BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, 0, 0);
			for (GlowPlayer p: world.getRawPlayers()) {
				p.getSession().send(bcmsg);
			}
		}
	}

}
