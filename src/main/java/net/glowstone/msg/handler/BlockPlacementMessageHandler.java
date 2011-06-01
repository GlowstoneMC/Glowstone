package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.GlowChunk;
import net.glowstone.msg.BlockChangeMessage;
import net.glowstone.net.Session;
import net.glowstone.GlowWorld;

import org.bukkit.Material;

/**
 * A {@link MessageHandler} which processes digging messages.
 * @author Zhuowei Zhang
 */
public final class BlockPlacementMessageHandler extends MessageHandler<BlockPlacementMessage> {

	@Override
	public void handle(Session session, GlowPlayer player, BlockPlacementMessage message) {
		if (player == null)
			return;

        GlowWorld world = player.getWorld();

        int x = message.getX();
        int z = message.getZ();
        int y = message.getY();
        switch (message.getDirection()) {
            case 0:
                --y; break;
            case 1:
                ++y; break;
            case 2:
                --z; break;
            case 3:
                ++z; break;
            case 4:
                --x; break;
            case 5:
                ++x; break;
        }
        
        world.getBlockAt(x, y, z).setType(Material.WOOD); 
	}

}
