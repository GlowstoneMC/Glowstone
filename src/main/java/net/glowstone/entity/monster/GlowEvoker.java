package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;

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
}
