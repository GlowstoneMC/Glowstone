package net.glowstone.entity.ai;

import net.glowstone.entity.GlowLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Sets the entity's target to the last attacker, switching to ATTACKED state.
 */
public class HurtByTargetTask extends EntityTask {

    public HurtByTargetTask() {
        super("hurt_by_target");
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public int getDurationMin() {
        return 0;
    }

    @Override
    public int getDurationMax() {
        return 0;
    }

    @Override
    public boolean shouldStart(GlowLivingEntity entity) {
        EntityDamageEvent lastDamage = entity.getLastDamageCause();
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent dEvent = (EntityDamageByEntityEvent) lastDamage;
            if (dEvent.getDamager() instanceof LivingEntity) {
                LivingEntity damager = (LivingEntity) dEvent.getDamager();
                return damager != entity && !damager.isDead()
                    && entity.hasLineOfSight(damager);
            }
        }
        return false;
    }

    @Override
    public void start(GlowLivingEntity entity) {
        EntityDamageByEntityEvent dEvent =
            (EntityDamageByEntityEvent) entity.getLastDamageCause();
        if (dEvent != null && dEvent.getDamager() instanceof LivingEntity) {
            entity.setTarget((LivingEntity) dEvent.getDamager());
            entity.setState(MobState.ATTACKED);
        }
    }

    @Override
    public void end(GlowLivingEntity entity) {
    }

    @Override
    public void execute(GlowLivingEntity entity) {
    }
}
