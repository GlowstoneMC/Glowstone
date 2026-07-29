package net.glowstone.entity.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EntityAiTaskTest {

    @Mock private World world;
    @Mock private GlowLivingEntity entity;
    private Location location;

    @BeforeEach
    void setUp() {
        location = new Location(world, 0, 64, 0);
        when(entity.getLocation()).thenReturn(location);
    }

    @Test void wanderTask_hasCorrectName() {
        assertEquals("wander", new WanderTask().getName());
    }

    @Test void wanderTask_isNotInstant() {
        assertFalse(new WanderTask().isInstant());
    }

    @Test void wanderTask_durationRangeIsValid() {
        WanderTask t = new WanderTask();
        assertTrue(t.getDurationMin() > 0);
        assertTrue(t.getDurationMax() >= t.getDurationMin());
    }

    @Test void wanderTask_shouldStartInWanderState() {
        WanderTask t = new WanderTask();
        when(entity.getState()).thenReturn(MobState.WANDER);
        boolean started = false;
        for (int i = 0; i < 200; i++) {
            if (t.shouldStart(entity)) { started = true; break; }
        }
        assertTrue(started, "Should start in WANDER state within 200 attempts");
    }

    @Test void meleeAttackTask_hasCorrectName() {
        assertEquals("melee_attack", new MeleeAttackTask().getName());
    }

    @Test void meleeAttackTask_isNotInstant() {
        assertFalse(new MeleeAttackTask().isInstant());
    }

    @Test void meleeAttackTask_shouldNotStartWithoutTarget() {
        MeleeAttackTask t = new MeleeAttackTask();
        when(entity.getTarget()).thenReturn(null);
        assertFalse(t.shouldStart(entity));
    }

    @Test void hurtByTargetTask_hasCorrectName() {
        assertEquals("hurt_by_target", new HurtByTargetTask().getName());
    }

    @Test void hurtByTargetTask_isInstant() {
        assertTrue(new HurtByTargetTask().isInstant());
    }

    @Test void hurtByTargetTask_zeroDurationForInstant() {
        HurtByTargetTask t = new HurtByTargetTask();
        assertEquals(0, t.getDurationMin());
        assertEquals(0, t.getDurationMax());
    }

    @Test void hurtByTargetTask_shouldNotStartWithoutDamage() {
        HurtByTargetTask t = new HurtByTargetTask();
        when(entity.getLastDamageCause()).thenReturn(null);
        assertFalse(t.shouldStart(entity));
    }
}
