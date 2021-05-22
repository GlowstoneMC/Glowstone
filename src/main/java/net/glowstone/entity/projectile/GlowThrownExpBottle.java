package net.glowstone.entity.projectile;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.EventFactory;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.objects.GlowExperienceOrb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GlowThrownExpBottle extends GlowProjectile implements ThrownExpBottle {

    public GlowThrownExpBottle(Location location) {
        super(location);
    }

    @Override
    public void collide(Block block) {
        spawnOrb();
    }

    @Override
    public void collide(LivingEntity entity) {
        spawnOrb();
    }

    private void spawnOrb() {
        int xp = ThreadLocalRandom.current().nextInt(9) + 3;
        ExpBottleEvent event = EventFactory.getInstance()
                .callEvent(new ExpBottleEvent(this, xp));
        xp = event.getExperience();
        ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(location, EntityType.EXPERIENCE_ORB);
        orb.setExperience(xp);
        if (orb instanceof GlowExperienceOrb) {
            ((GlowExperienceOrb) orb).setFromBottle(true);
        }
        remove();
    }

    @Override
    protected int getObjectId() {
        return EntityNetworkUtil.getObjectId(EntityType.THROWN_EXP_BOTTLE);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Material.EXPERIENCE_BOTTLE);
    }

    @Override
    public void setItem(@NotNull ItemStack itemStack) {
        // TODO: 1.16
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
