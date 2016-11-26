package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.concurrent.ThreadLocalRandom;

public class GlowEvoker extends GlowMonster implements Evoker {
    public GlowEvoker(Location loc) {
        super(loc, EntityType.EVOKER, 24);
        metadata.set(MetadataIndex.EVOKER_SPELL, (byte) Spell.NONE.ordinal());
    }

    @Override
    public Spell getCurrentSpell() {
        return Spell.values()[(int) metadata.getByte(MetadataIndex.EVOKER_SPELL)];
    }

    @Override
    public void setCurrentSpell(Spell spell) {
        metadata.set(MetadataIndex.EVOKER_SPELL, (byte) spell.ordinal());
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_EVOCATION_ILLAGER_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_EVOCATION_ILLAGER_HURT;
    }

    @Override
    public void damage(double amount, Entity source, EntityDamageEvent.DamageCause cause) {
        super.damage(amount, source, cause);
        castSpell(Spell.SUMMON); // todo: remove this, demo purposes
    }

    public void castSpell(Spell spell) {
        setCurrentSpell(spell);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (spell) {
            case FANGS: {
                // todo
                break;
            }
            case SUMMON: {
                world.playSound(location, Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.0f, 1.0f);
                int count = 3;
                for (int i = 0; i < count; i++) {
                    double y = random.nextDouble() + 0.5 + location.getY();
                    double radius = 0.5 + random.nextDouble();
                    double angle = random.nextDouble() * 2 * Math.PI;
                    double x = radius * Math.sin(angle) + location.getX();
                    double z = radius * Math.cos(angle) + location.getZ();
                    Location location = new Location(world, x, y, z);
                    world.spawnEntity(location, EntityType.VEX);
                }
                break;
            }
            case WOLOLO: {
                // todo
                break;
            }
        }
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_EVOCATION_ILLAGER_AMBIENT;
    }
}
