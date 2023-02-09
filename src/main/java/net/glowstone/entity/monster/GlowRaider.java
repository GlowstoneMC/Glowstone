package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;
import org.jetbrains.annotations.NotNull;

/**
 * A mob that can raid villages.
 */
public abstract class GlowRaider extends GlowMonster implements Raider {
    @Getter
    @Setter
    private Block patrolTarget;
    @Getter
    @Setter
    private boolean patrolLeader;
    @Getter
    @Setter
    private boolean canJoinRaid;

    public GlowRaider(Location loc, EntityType type, double maxHealth) {
        super(loc, type, maxHealth);
    }

    @Override
    public boolean isCelebrating() {
        return false;
    }

    @Override
    public void setCelebrating(boolean celebrating) {

    }

    @Override
    public @NotNull Sound getCelebrationSound() {
        return Sound.ENTITY_PILLAGER_CELEBRATE;
    }
}
