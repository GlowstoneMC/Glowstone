package net.glowstone.entity;

import com.flowpowered.networking.Message;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;

import java.util.*;

public class AttributeManager {
    private static final List<Modifier> EMPTY_LIST = new ArrayList<>();

    private final GlowLivingEntity entity;
    private final Map<String, Property> properties;

    private boolean needsUpdate;

    public AttributeManager(GlowLivingEntity entity) {
        this.entity = entity;
        this.properties = new HashMap<>();
        this.needsUpdate = false;
    }

    public void applyMessages(Collection<Message> messages) {
        if (!needsUpdate)
            return;
        messages.add(new EntityPropertyMessage(entity.id, properties));
        needsUpdate = false;
    }

    public void sendMessages(GlowSession session) {
        if (!needsUpdate)
            return;
        session.send(new EntityPropertyMessage(entity.id, properties));
        needsUpdate = false;
    }

    public void setProperty(Key key, double value) {
        setProperty(key.toString(), Math.max(0, Math.min(value, key.max)), null);
    }

    public double getPropertyValue(Key key) {
        if (properties.containsKey(key.toString())) {
            return properties.get(key.toString()).value;
        }

        return key.def;
    }

    public void setProperty(String key, double value, List<Modifier> modifiers) {
        if (properties.containsKey(key)) {
            properties.get(key).value = value;
            properties.get(key).modifiers = modifiers;
        } else {
            properties.put(key, new Property(value, modifiers == null ? EMPTY_LIST : modifiers));
        }

        needsUpdate = true;
    }

    public Map<String, Property> getAllProperties() {
        return properties;
    }

    public static final class Property {
        @Getter
        private double value;
        @Getter
        private List<Modifier> modifiers;

        public Property(double value, List<Modifier> modifiers) {
            this.value = value;
            this.modifiers = modifiers;
        }
    }

    @Data
    public static final class Modifier {
        private final String name;
        private final UUID uuid;
        private final double amount;
        private final byte operation;
    }

    @RequiredArgsConstructor
    public enum Key {
        KEY_MAX_HEALTH("generic.maxHealth", 20, Double.MAX_VALUE),
        KEY_FOLLOW_RANGE("generic.followRange", 32, 2048),
        KEY_KNOCKBACK_RESISTANCE("generic.knockbackResistance", 0, 1),
        KEY_MOVEMENT_SPEED("generic.movementSpeed", 0.699999988079071, Double.MAX_VALUE),
        KEY_ATTACK_DAMAGE("generic.attackDamage", 2, Double.MAX_VALUE),
        KEY_HORSE_JUMP_STRENGTH("horse.jumpStrength", 0.7, 2),
        KEY_ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawnReinforcements", 0, 1),
        ;


        private final String name;
        @Getter
        private final double def, max;

        @Override
        public String toString() {
            return name;
        }
    }
}
