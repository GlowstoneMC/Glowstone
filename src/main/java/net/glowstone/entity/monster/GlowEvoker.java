package net.glowstone.entity.monster;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityDamageEvent;

public class GlowEvoker extends GlowMonster implements Evoker {

    /**
     * Creates an evoker.
     *
     * @param loc the evoker's location
     */
    public GlowEvoker(Location loc) {
        super(loc, EntityType.EVOKER, 24);
        metadata.set(MetadataIndex.EVOKER_SPELL, (byte) Spellcaster.Spell.NONE.ordinal());
        setBoundingBox(0.6, 1.95);
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
        castSpell(Spellcaster.Spell.SUMMON_VEX); // todo: remove this, demo purposes
    }

    /**
     * Casts the given spell.
     *
     * @param spell the spell to cast
     */
    public void castSpell(Spellcaster.Spell spell) {
        setSpell(spell);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (spell) {
            case FANGS:
                // todo
                break;
            case SUMMON_VEX:
                world
                    .playSound(location, Sound.ENTITY_EVOCATION_ILLAGER_PREPARE_SUMMON, 1.0f, 1.0f);
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
            case WOLOLO:
                // todo
                break;
            default:
                // TODO: Should this raise a warning?
        }
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_EVOCATION_ILLAGER_AMBIENT;
    }

    @Override
    public Spellcaster.Spell getSpell() {
        return Spellcaster.Spell.values()[(int) metadata.getByte(MetadataIndex.EVOKER_SPELL)];
    }

    @Override
    public void setSpell(Spellcaster.Spell spell) {
        metadata.set(MetadataIndex.EVOKER_SPELL, (byte) spell.ordinal());
    }
}
