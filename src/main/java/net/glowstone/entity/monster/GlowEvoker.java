package net.glowstone.entity.monster;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class GlowEvoker extends GlowSpellcaster implements Evoker {

    @Getter
    @Setter
    private Sheep wololoTarget;

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
    @Deprecated
    public Evoker.Spell getCurrentSpell() {
        switch (this.getSpell()) {
            case FANGS:
                return Evoker.Spell.FANGS;
            case BLINDNESS:
                return Evoker.Spell.BLINDNESS;
            case DISAPPEAR:
                return Evoker.Spell.DISAPPEAR;
            case SUMMON_VEX:
                return Evoker.Spell.SUMMON;
            case WOLOLO:
                return Evoker.Spell.WOLOLO;
            default:
                return Evoker.Spell.NONE;
        }
    }

    @Override
    @Deprecated
    public void setCurrentSpell(Evoker.Spell spell) {
        if (spell == null) {
            setSpell(Spellcaster.Spell.NONE);
            return;
        }
        switch (spell) {
            case FANGS:
                setSpell(Spellcaster.Spell.FANGS);
                break;
            case BLINDNESS:
                setSpell(Spellcaster.Spell.BLINDNESS);
                break;
            case DISAPPEAR:
                setSpell(Spellcaster.Spell.DISAPPEAR);
                break;
            case SUMMON:
                setSpell(Spellcaster.Spell.SUMMON_VEX);
                break;
            case WOLOLO:
                setSpell(Spellcaster.Spell.WOLOLO);
                break;
            default:
                setSpell(Spellcaster.Spell.NONE);
        }
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_EVOKER_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_EVOKER_HURT;
    }

    @Override
    public void damage(double amount, Entity source, @NotNull EntityDamageEvent.DamageCause cause) {
        super.damage(amount, source, cause);
        castSpell(Spellcaster.Spell.SUMMON_VEX); // todo: remove this, demo purposes
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_EVOKER_AMBIENT;
    }

    /**
     * Casts the given spell.
     *
     * @param spell the spell to cast
     */
    @Override
    public void castSpell(Spellcaster.Spell spell) {
        setSpell(spell);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        switch (spell) {
            case FANGS:
                // todo
                break;
            case SUMMON_VEX:
                world
                    .playSound(location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);
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
}
