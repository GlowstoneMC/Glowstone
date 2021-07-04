package net.glowstone.entity.monster;

import net.glowstone.entity.GlowCreatureTest;
import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowMonsterTest<T extends GlowMonster> extends GlowCreatureTest<T> {

    protected GlowMonsterTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
