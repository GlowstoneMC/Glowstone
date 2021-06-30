package net.glowstone.entity.monster;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Raider;

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
}
