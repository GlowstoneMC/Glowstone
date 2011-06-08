package net.glowstone.msg.handler;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.BlockPlacementMessage;
import net.glowstone.net.Session;
import net.glowstone.GlowWorld;

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
        
        if (player.getItemInHand() != null && player.getItemInHand().getTypeId() < 256) {
            if (world.getBlockAt(x, y, z).getType() == Material.AIR) {
                world.getBlockAt(x, y, z).setType(player.getItemInHand().getType());
                ItemStack stack = player.getItemInHand();
                stack.setAmount(stack.getAmount() - 1);
                if (stack.getAmount() == 0) {
                    player.setItemInHand(null);
                } else {
                    player.setItemInHand(stack);
                }
            }
        }
	}

}
