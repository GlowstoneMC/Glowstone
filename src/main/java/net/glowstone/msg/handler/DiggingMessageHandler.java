package net.glowstone.msg.handler;

import java.util.logging.Level;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.DiggingMessage;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.net.Session;
import net.glowstone.GlowWorld;

import org.bukkit.Material;
import org.bukkit.block.Block;

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
			int y = message.getY();
			int z = message.getZ();
            
            Block block = world.getBlockAt(x, y, z);
            block.setType(Material.AIR);
		}
	}

}
