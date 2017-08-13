package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemFishingRode extends ItemType {

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        Location location = calculateSpawnLocation(player);
        player.getWorld().spawn(location, FishHook.class);
    }

    private Location calculateSpawnLocation(Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();

        double dx = direction.getX() * player.getWidth() / 2;
        double dz = direction.getZ() * player.getWidth() / 2;
        loc.add(dx, 0, dz);

        return loc;
    }

    @Override
    public boolean canOnlyUseSelf() {
        return true;
    }
}
