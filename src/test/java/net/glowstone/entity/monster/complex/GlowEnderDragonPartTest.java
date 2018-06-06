package net.glowstone.entity.monster.complex;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import net.glowstone.entity.GlowEntityTest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GlowEnderDragonPartTest extends GlowEntityTest<GlowEnderDragonPart> {
    public GlowEnderDragonPartTest() {
        super(location -> new GlowEnderDragonPart(new GlowEnderDragon(location)));
    }

    @Override
    public boolean createEntityInSuperSetUp() {
        return false;
    }

    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        when(server.createBossBar(anyString(), any(BarColor.class), any(BarStyle.class),
                any(BarFlag[].class))).thenCallRealMethod();
        entity = entityCreator.apply(location);
    }

    @Test
    @Override
    public void testCreateSpawnMessage() {
        // An ender-dragon part has no spawn messages, because the client derives it from the parent
        // entity's spawn message.
        assertEquals(entity.createSpawnMessage(), Collections.emptyList());
    }
}
