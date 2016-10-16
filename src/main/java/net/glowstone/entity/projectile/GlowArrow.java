package net.glowstone.entity.projectile;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class GlowArrow extends GlowProjectile implements Arrow {

    private boolean inGround = false;

    public GlowArrow(Location location) {
        super(location);
        setBoundingBox(0.5, 0.5);
    }

    @Override
    public void collide(Block block) {
        if (!inGround) {
            setVelocity(new Vector(0, 0, 0));
            inGround = true;
        }
    }

    @Override
    public void collide(LivingEntity entity) {
        entity.damage(6);
        remove();
    }

    @Override
    protected int getObjectId() {
        return 60;
    }

    @Override
    public int getKnockbackStrength() {
        return 0;
    }

    @Override
    public void setKnockbackStrength(int strength) {

    }

    @Override
    public boolean isCritical() {
        return false;
    }

    @Override
    public void setCritical(boolean critical) {

    }

    @Override
    public PickupRule getPickupRule() {
        return null;
    }

    @Override
    public void setPickupRule(PickupRule pickupRule) {

    }

    @Override
    public void sendMessage(BaseComponent... components) {

    }

    @Override
    public Arrow.Spigot spigot() {
        return null;
    }
}
