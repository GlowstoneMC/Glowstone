package net.glowstone.util.loot;

import net.glowstone.entity.GlowLivingEntity;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class LootingUtil {

    public static final Random random = ThreadLocalRandom.current();

    public static boolean is(Object a, Object b) {
        return Objects.equals(a, b);
    }

    public static boolean and(boolean a, boolean b) {
        return a && b;
    }

    public static boolean or(boolean a, boolean b) {
        return a || b;
    }

    public static boolean not(boolean b) {
        return !b;
    }

    public static boolean isNot(Object a, Object b) {
        return !is(a, b);
    }

    public static boolean conditionValue(GlowLivingEntity entity, String condition) {
        if (condition.equals("ENTITY_ONFIRE")) {
            return entity.getFireTicks() > 0;
        }
        if (condition.startsWith("ENTITY_KILLER_")) {
            EntityType type = EntityType.valueOf(condition.substring("ENTITY_KILLER_".length()));
            if (entity.getLastDamager() == null) {
                return false;
            }
            return entity.getLastDamager().getType() == type;
        }
        if (condition.contains(".")) {
            return (boolean) process(entity, condition);
        }
        // todo: more conditions, reflection
        return false;
    }

    public static Object process(LivingEntity entity, String line) {
        return new ReflectionProcessor(line, entity).process();
    }

    public static long randomFishType() {
        return random.nextInt(4);
    }
}
