package net.glowstone.entity.passive;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.io.IOException;
import java.util.Collections;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.FishingRewardManager;
import net.glowstone.entity.GlowEntityTest;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.GreaterThan;
import org.powermock.api.mockito.PowerMockito;

public class GlowFishingHookTest extends GlowEntityTest<GlowFishingHook> {

    /** This needs to be static because it's used in the constructor's super call. */
    @Mock
    private static GlowPlayer player;
    @Mock
    private GlowBlock block;

    private FishingRewardManager fishingRewardManager;
    /** Necessary because of an issue with verifyStatic */
    protected Multimap<Class<? extends Event>, Event> eventsFired = ArrayListMultimap.create();

    public GlowFishingHookTest() {
        super(location -> new GlowFishingHook(location, null, player));
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        eventsFired.removeAll(PlayerFishEvent.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(block);
        when(block.getType()).thenReturn(Material.WATER);
        fishingRewardManager = new FishingRewardManager();
        when(server.getFishingRewardManager()).thenReturn(fishingRewardManager);
        when(player.getLocation()).thenReturn(location);
        mockStatic(EventFactory.class);
        when(EventFactory.callEvent(any(Event.class))).thenAnswer(invocation -> {
            Event e = invocation.getArgument(0);
            eventsFired.put(e.getClass(), e);
            return e;
        });
        when(EventFactory.onEntityDamage(any(EntityDamageEvent.class))).thenAnswer(
                RETURN_FIRST_ARG);
    }

    @After
    @Override
    public void tearDown() {
        super.tearDown();
        // https://www.atlassian.com/blog/archives/reducing_junit_memory_usage
        eventsFired = null;
    }

    @Test
    public void testReelInNotPlayer() {
        GlowFishingHook hook = new GlowFishingHook(location, null, player);
        hook.setShooter(new GlowCreeper(location));
        hook.reelIn();
        assertTrue(hook.isRemoved());
        verify(world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));
        assertTrue(eventsFired.get(PlayerFishEvent.class).isEmpty());
    }

    @Test
    public void testReelIn() {
        GlowFishingHook hook = new GlowFishingHook(location, null, player);
        hook.setShooter(player);
        hook.reelIn();
        assertTrue(hook.isRemoved());
        verify(player).giveExp(intThat(new GreaterThan<>(0)));
        verify(world).dropItemNaturally(eq(location),
                argThat(itemStack -> !InventoryUtil.isEmpty(itemStack)));
        assertEquals(1, eventsFired.get(PlayerFishEvent.class).size());
    }

    @Test
    public void testReelInNotInWater() {
        when(block.getType()).thenReturn(Material.DIRT);
        GlowFishingHook hook = new GlowFishingHook(location, null, player);
        hook.setShooter(player);
        hook.reelIn();
        assertTrue(hook.isRemoved());
        verify(world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));
        verify(player, never()).giveExp(anyInt());
        assertTrue(eventsFired.get(PlayerFishEvent.class).isEmpty());
    }
}
