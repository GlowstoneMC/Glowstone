package net.glowstone.entity.monster.complex;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class GlowEnderDragonPart extends GlowEntity implements EnderDragonPart {

    private EnderDragon parent;

    public GlowEnderDragonPart(EnderDragon parent) {
        super(parent.getLocation());
        this.parent = parent;
    }

    @Override
    public EnderDragon getParent() {
        return parent;
    }

    @Override
    public List<Message> createSpawnMessage() {
        return null;
    }

    @Override
    public void damage(double amount) {
        parent.damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        parent.damage(amount, source);
    }

    @Override
    public void damage(double amount, EntityDamageEvent.DamageCause cause) {
        parent.damage(amount, cause);
    }

    @Override
    public void damage(double amount, Entity source, EntityDamageEvent.DamageCause cause) {
        parent.damage(amount, source, cause);
    }

    @Override
    public double getHealth() {
        return parent.getHealth();
    }

    @Override
    public void setHealth(double health) {
        parent.setHealth(health);
    }

    @Override
    public double getMaxHealth() {
        return parent.getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        parent.setMaxHealth(health);
    }

    @Override
    public void resetMaxHealth() {
        parent.resetMaxHealth();
    }

    @Override
    public boolean shouldSave() {
        return false;
    }
}
