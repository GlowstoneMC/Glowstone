package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class ItemChorusFruit extends ItemFood {

    public ItemChorusFruit() {
        super(4, 2.4f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) {
            return false;
        }

        for (int i = 0; i < 16; i++) { //16 attempts: +/- 8 blocks in every direction
            Location attempt = player.getLocation();
            double deltaX = ThreadLocalRandom.current().nextDouble() * 16 - 8;
            double deltaY = ThreadLocalRandom.current().nextDouble() * 16 - 8;
            double deltaZ = ThreadLocalRandom.current().nextDouble() * 16 - 8;
            attempt.setX(attempt.getX() + deltaX);
            attempt.setY(Math.min(Math.max(attempt.getY() + deltaY, 0),
                player.getWorld().getMaxHeight() - 1));
            attempt.setZ(attempt.getZ() + deltaZ);
            attempt = getSafeLocation(attempt);
            if (attempt != null) {
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_CHORUS_FRUIT_TELEPORT,
                    SoundCategory.PLAYERS, 1, 1);
                player.teleport(attempt);
                break;
            }
        }
        return true;
    }

    private Location getSafeLocation(Location loc) {
        int blockY = loc.getBlockY();
        World world = loc.getWorld();
        if (blockY > world.getHighestBlockYAt(loc)) {
            blockY = world.getHighestBlockYAt(loc) + 1;
        }
        boolean found = false;
        boolean hadSpace = false;
        while (blockY > 0) {
            Block current = world.getBlockAt(loc.getBlockX(), blockY, loc.getBlockZ());
            if (current.isEmpty() && current.getRelative(BlockFace.UP).isEmpty()) {
                hadSpace = true;
            } else if (hadSpace) {
                if (current.getType().isSolid()) {
                    found = true;
                    blockY++;
                    break;
                } else {
                    hadSpace = false;
                }
            }
            blockY--;
        }
        if (found) {
            loc.setY(blockY);
            loc.setX(loc.getBlockX() + 0.5);
            //TODO: Do a proper bounding box check instead of just centering the location
            loc.setZ(loc.getBlockZ() + 0.5);
            return loc;
        } else {
            return null;
        }
    }
}
