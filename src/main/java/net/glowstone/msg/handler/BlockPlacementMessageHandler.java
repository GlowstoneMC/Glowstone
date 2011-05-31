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

        // TODO it might be nice to move these calculations somewhere else since they will need to be reused
        int chunkX = x / GlowChunk.WIDTH + ((x < 0 && x % GlowChunk.WIDTH != 0) ? -1 : 0);
        int chunkZ = z / GlowChunk.HEIGHT + ((z < 0 && z % GlowChunk.HEIGHT != 0) ? -1 : 0);

        int localX = (x - chunkX * GlowChunk.WIDTH) % GlowChunk.WIDTH;
        int localZ = (z - chunkZ * GlowChunk.HEIGHT) % GlowChunk.HEIGHT;

        GlowChunk chunk = world.getChunkManager().getChunk(chunkX, chunkZ);
        chunk.setType(localX, localZ, y, Material.WOOD.getId());

        // TODO this should also be somewhere else as well... perhaps in the chunk.setType() method itself?
        BlockChangeMessage bcmsg = new BlockChangeMessage(x, y, z, Material.WOOD.getId(), 0);
        for (GlowPlayer p: world.getRawPlayers()) {
            p.getSession().send(bcmsg);
        }
	}

}
