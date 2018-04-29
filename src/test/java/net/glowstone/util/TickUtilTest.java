package net.glowstone.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class TickUtilTest {

    @Test
    public void testSecondsIntToTicks() {
        assertEquals(0, TickUtil.secondsToTicks(0));
        assertEquals(20, TickUtil.secondsToTicks(1));
        assertEquals(600, TickUtil.secondsToTicks(30));
        assertEquals(1200, TickUtil.secondsToTicks(60));
    }

    @Test
    public void testSecondsLongToTicks() {
        assertEquals(0L, TickUtil.secondsToTicks(0L));
        assertEquals(20L, TickUtil.secondsToTicks(1L));
        assertEquals(600L, TickUtil.secondsToTicks(30L));
        assertEquals(1200L, TickUtil.secondsToTicks(60L));
    }

    @Test
    public void testMinutesIntToTicks() {
        assertEquals(0, TickUtil.minutesToTicks(0));
        assertEquals(1200, TickUtil.minutesToTicks(1));
        assertEquals(6000, TickUtil.minutesToTicks(5));
        assertEquals(24000, TickUtil.minutesToTicks(20));
        assertEquals(72000, TickUtil.minutesToTicks(60));
    }

    @Test
    public void testMinutesDoubleToTicks() {
        assertEquals(0, TickUtil.minutesToTicks(0D));
        assertEquals(1200, TickUtil.minutesToTicks(1D));
        assertEquals(1800, TickUtil.minutesToTicks(1.5));
    }
}
