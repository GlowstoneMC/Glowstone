package net.glowstone.msg.handler;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.DiggingMessage;
import net.glowstone.net.Session;
import net.glowstone.GlowWorld;

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
            if (block.getType() != Material.AIR) {
                player.getInventory().addItem(new ItemStack(block.getType(), 1));
            }
            block.setType(Material.AIR);
        }
    }

}
