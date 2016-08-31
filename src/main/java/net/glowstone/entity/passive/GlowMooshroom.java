package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GlowMooshroom extends GlowAnimal implements MushroomCow {

    public GlowMooshroom(Location location) {
        super(location, EntityType.MUSHROOM_COW, 10);
        setSize(0.9F, 1.3F);
    }

    @Override
    public void kill()
    {
        super.kill();

        Random r = new Random();

        int leatherDrop = r.nextInt(2);
        int beefDrop = r.nextInt(3);
        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.LEATHER, leatherDrop));
        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.RAW_BEEF, beefDrop));
    }
}
