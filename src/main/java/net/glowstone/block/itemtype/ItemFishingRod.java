package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.passive.GlowFishingHook;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemFishingRod extends ItemType {

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        if (player.getCurrentFishingHook() == null) {
            Location location = calculateSpawnLocation(player);
            FishHook fishHook = new GlowFishingHook(location, holding);
            fishHook.setShooter(player);
            player.setCurrentFishingHook((GlowFishingHook) fishHook);
        } else {
            player.getCurrentFishingHook().reelIn();
            player.setCurrentFishingHook(null);
        }

    }

    private Location calculateSpawnLocation(Player player) {
        Location loc = player.getEyeLocation();
        Vector direction = loc.getDirection();

        double dx = direction.getX() * player.getWidth() / 2;
        double dz = direction.getZ() * player.getWidth() / 2;
        loc.add(dx, 0, dz);

        return loc;
    }
}
