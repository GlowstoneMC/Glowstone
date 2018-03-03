package net.glowstone.entity.passive;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import net.glowstone.EventFactory;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.FishingRewardManager;
import net.glowstone.entity.GlowEntityTest;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
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

    public GlowFishingHookTest() {
        super(location -> new GlowFishingHook(location, null, player));
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(block);
        when(block.getType()).thenReturn(Material.WATER);
        fishingRewardManager = new FishingRewardManager();
        when(server.getFishingRewardManager()).thenReturn(fishingRewardManager);
        when(player.getLocation()).thenReturn(location);
    }

    @Test
    public void testReelInNotPlayer() {
        GlowFishingHook hook = new GlowFishingHook(location, null, player);
        hook.setShooter(new GlowCreeper(location));
        hook.reelIn();
        assertTrue(hook.isRemoved());
        verify(world, never()).dropItemNaturally(any(Location.class), any(ItemStack.class));
        PowerMockito.verifyStatic(EventFactory.class, never());
        EventFactory.callEvent(any(PlayerFishEvent.class));
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
        PowerMockito.verifyStatic(EventFactory.class);
        EventFactory.callEvent(any(PlayerFishEvent.class));
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
        PowerMockito.verifyStatic(EventFactory.class, never());
        EventFactory.callEvent(any(PlayerFishEvent.class));
    }
}
