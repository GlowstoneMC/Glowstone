package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spellcaster;

public abstract class GlowSpellcaster extends GlowIllager implements Spellcaster {

    @Getter
    @Setter
    private Spellcaster.Spell spell;

    public GlowSpellcaster(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }

    /**
     * Casts the given spell.
     *
     * @param spell the spell to cast
     */
    public abstract void castSpell(Spellcaster.Spell spell);
}
