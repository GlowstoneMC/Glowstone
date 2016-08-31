package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class GlowPolarBear extends GlowAnimal implements PolarBear {

    public GlowPolarBear(Location location) {
        super(location, EntityType.POLAR_BEAR, 30);
    }

    @Override
    public boolean isStanding() {
        return metadata.getBoolean(MetadataIndex.POLARBEAR_STANDING);
    }

    @Override
    public void setStanding(boolean standing) {
        metadata.set(MetadataIndex.POLARBEAR_STANDING, standing);
    }

    @Override
    public void kill()
    {
        super.kill();

        Random r = new Random();

        int porkDrop = r.nextInt(3);
        getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.RAW_FISH, porkDrop));
    }


}
