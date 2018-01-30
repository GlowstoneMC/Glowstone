package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEvoker;
import org.bukkit.entity.EntityType;

public class EvokerStore extends MonsterStore<GlowEvoker> {

    public EvokerStore() {
        super(GlowEvoker.class, EntityType.EVOKER, GlowEvoker::new);
    }
}
