package net.glowstone.entity.projectile;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GlowSpectralArrow extends GlowArrow implements SpectralArrow {

    private int glowingTicks;

    public GlowSpectralArrow(Location location) {
        super(location);
        glowingTicks = 200;
    }

    @Override
    public void collide(LivingEntity entity) {
        super.collide(entity);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, glowingTicks, 1));
    }

    @Override
    public int getGlowingTicks() {
        return glowingTicks;
    }

    @Override
    protected int getObjectId() {
        return 91;
    }

    @Override
    public void setGlowingTicks(int i) {
        glowingTicks = i;
    }
}
