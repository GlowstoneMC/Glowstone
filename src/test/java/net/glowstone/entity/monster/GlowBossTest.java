package net.glowstone.entity.monster;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public abstract class GlowBossTest<T extends GlowBoss> extends GlowMonsterTest<T> {

    protected GlowWorld secondWorld;
    @Mock
    protected GlowPlayer player;

    protected GlowBossTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        secondWorld = PowerMockito.mock(GlowWorld.class, Mockito.RETURNS_SMART_NULLS);
        when(secondWorld.getEntityManager()).thenReturn(entityManager);
        when(secondWorld.getBlockAt(any(Location.class))).thenReturn(block);
        doNothing().when(entityManager, "move", any(GlowEntity.class), any(Location.class));
        doNothing().when(entityManager, "register", any(GlowEntity.class));
        doNothing().when(entityManager, "unregister", any(GlowEntity.class));
        when(server.createBossBar(anyString(), any(BarColor.class), any(BarStyle.class),
                any(BarFlag[].class))).thenCallRealMethod();
        entity = entityCreator.apply(location); // must redo because of createBossBar stubbing
    }

    @Test
    public void testBossBar() {
        Location secondWorldLocation = new Location(secondWorld, 0, 0, 0);
        // Should add bar when player is in world
        AtomicReference<BossBar> barRef = new AtomicReference<>();
        doAnswer(invocation -> {
            BossBar bar = invocation.getArgument(0);
            assertNotNull(bar);
            barRef.set(bar);
            return null;
        }).when(player).addBossBar(any(BossBar.class));
        when(secondWorld.getRawPlayers()).thenReturn(Collections.singleton(player));
        entity.teleport(secondWorldLocation);
        verify(player).addBossBar(any(BossBar.class));
        BossBar bar = barRef.get();
        assertIterableEquals(Collections.singleton(player), bar.getPlayers());

        // Should remove bar when teleporting back to a world without the player
        entity.teleport(location);
        verify(player).removeBossBar(eq(barRef.get()));
    }
}
