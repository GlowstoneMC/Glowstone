package net.glowstone.block.blocktype;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class BlockMonsterEgg extends BlockType {
    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        //only spawn silverfish if block broke by player
        block.getWorld().spawn(block.getLocation().add(0.5, 0, 0.5), Silverfish.class, CreatureSpawnEvent.SpawnReason.SILVERFISH_BLOCK);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        ItemStack stack;

        switch (block.getData()) {
            case 0: //stone
            case 1: //cobblestone
                stack = new ItemStack(Material.COBBLESTONE);
                break;
            case 2: //stone bricks
                stack = new ItemStack(Material.SMOOTH_BRICK);
                break;
            case 3: //mossy stone bricks
                stack = new ItemStack(Material.SMOOTH_BRICK, 2);
                break;
            case 4: //cracked stone bricks
                stack = new ItemStack(Material.SMOOTH_BRICK, 1);
                break;
            case 5: //chiseled stone bricks
                stack = new ItemStack(Material.SMOOTH_BRICK, 3);
                break;
            default:
                GlowServer.logger.warning("Wrong data for block monster_egg: " + block.getData());
                stack = new ItemStack(Material.STONE);
        }

        return Arrays.asList(stack);
    }
}
