package net.glowstone.entity;

import com.flowpowered.network.Message;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

/**
 * Manages the attributes described at https://minecraft.gamepedia.com/Attribute
 */
public class AttributeManager {

    private final GlowLivingEntity entity;
    private final Map<String, Property> properties;

    private boolean needsUpdate;

    /**
     * Create an instance for the given entity.
     *
     * @param entity the entity whose attributes will be managed
     */
    public AttributeManager(GlowLivingEntity entity) {
        this.entity = entity;
        properties = Maps.newHashMap();
        needsUpdate = false;
    }

    /**
     * Adds an {@link EntityPropertyMessage} with our entity's properties to the given collection of
     * messages, if the client's snapshot is stale.
     *
     * @param messages the message collection to add to
     */
    public void applyMessages(Collection<Message> messages) {
        if (!needsUpdate) {
            return;
        }
        messages.add(new EntityPropertyMessage(entity.entityId, properties));
        needsUpdate = false;
    }

    /**
     * Sends the managed entity's properties to the client, if the client's snapshot is stale.
     *
     * @param session the client's session
     */
    public void sendMessages(GlowSession session) {
        if (!needsUpdate) {
            return;
        }
        int id = entity.entityId;
        if (entity instanceof GlowPlayer) {
            GlowPlayer player = (GlowPlayer) entity;
            if (player.getUniqueId().equals(session.getPlayer().getUniqueId())) {
                id = 0;
            }
        }
        session.send(new EntityPropertyMessage(id, properties));
        needsUpdate = false;
    }

    /**
     * Updates a property and removes all modifiers.
     *
     * @param key the property to update
     * @param value the new value
     */
    public void setProperty(Key key, double value) {
        setProperty(key, Math.max(key.min, Math.min(value, key.max)), null);
    }

    /**
     * Updates a property and its modifiers.
     *
     * @param key the property to update
     * @param value the new base value
     * @param modifiers the new and retained modifiers, or {@code null} to remove all modifiers
     */
    public void setProperty(Key key, double value, Collection<AttributeModifier> modifiers) {
        if (modifiers == null) {
            modifiers = Collections.emptyList();
        }

        properties.put(key.toString(), new Property(key, value, modifiers));
        needsUpdate = true;
    }

    /**
     * Returns the base value of the given property.
     *
     * @param key the property to look up
     * @return the property's base value, or its default value if it's not set
     */
    public double getPropertyValue(Key key) {
        if (properties.containsKey(key.toString())) {
            return properties.get(key.toString()).value;
        }

        return key.def;
    }

    /**
     * Returns all the properties stored in the manager.
     * @return a unmodifiable map of all the properties
     */
    public Map<String, Property> getAllProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @RequiredArgsConstructor
    public enum Key {
        KEY_MAX_HEALTH("generic.maxHealth", Attribute.GENERIC_MAX_HEALTH, 20, 1024.0),
        KEY_FOLLOW_RANGE("generic.followRange", Attribute.GENERIC_FOLLOW_RANGE, 32, 2048),
        KEY_KNOCKBACK_RESISTANCE("generic.knockbackResistance",
                Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0, 1),
        KEY_MOVEMENT_SPEED("generic.movementSpeed",
                Attribute.GENERIC_MOVEMENT_SPEED, 0.699999988079071, 1024.0),
        KEY_ATTACK_DAMAGE("generic.attackDamage", Attribute.GENERIC_ATTACK_DAMAGE, 2, 2048.0),
        KEY_ATTACK_SPEED("generic.attackSpeed", Attribute.GENERIC_ATTACK_SPEED, 4.0, 1024.0),
        KEY_ARMOR("generic.armor", Attribute.GENERIC_ARMOR, 0.0, 30.0),
        KEY_ARMOR_TOUGHNESS("generic.armorToughness", Attribute.GENERIC_ARMOR_TOUGHNESS, 0.0, 20.0),
        KEY_LUCK("generic.luck", Attribute.GENERIC_LUCK, 0, -1024, 1024),
        KEY_FLYING_SPEED("generic.flyingSpeed", Attribute.GENERIC_FLYING_SPEED, 0.4, 1024),
        KEY_HORSE_JUMP_STRENGTH("horse.jumpStrength", Attribute.HORSE_JUMP_STRENGTH, 0.7, 2),
        KEY_ZOMBIE_SPAWN_REINFORCEMENTS("zombie.spawnReinforcements",
                Attribute.ZOMBIE_SPAWN_REINFORCEMENTS, 0, 1);

        /**
         * Get a {@link Key} by its {@link Key#name attribute name}.
         *
         * @param name of the attribute to return
         * @return the attribute with the specified name or {@code null}
         */
        public static Key fromName(String name) {
            for (Key key : values()) {
                if (key.name.equals(name)) {
                    return key;
                }
            }

            return null;
        }

        /**
         * Attribute name from https://minecraft.gamepedia.com/Attribute
         */
        private final String name;

        /**
         * Bukkit {@link Attribute} corresponding to this key.
         */
        @Getter
        private final Attribute attribute;

        /**
         * Default attribute value.
         */
        @Getter
        private final double def;
        /**
         * Minimum attribute value.
         */
        @Getter
        private final double min;
        /**
         * Maximum attribute value.
         */
        @Getter
        private final double max;

        /**
         * Creates an instance with a minimum value of 0.
         *
         * @param name attribute name
         * @param def default value
         * @param max maximum value
         */
        Key(String name, Attribute attribute, double def, double max) {
            this(name, attribute, def, 0, max);
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @AllArgsConstructor
    public class Property implements AttributeInstance {
        @Getter
        private Key key;
        private double value;
        private Map<UUID, AttributeModifier> modifiers;

        private double cachedValue;
        private boolean isCacheUpToDate = false;

        /**
         * Create a new property instance.
         *
         * @param key of the property
         * @param value of the property
         * @param modifiers of the property
         */
        public Property(Key key, double value, Collection<AttributeModifier> modifiers) {
            this.key = key;
            this.value = value;
            this.modifiers = modifiers.stream()
                    .collect(Collectors.toMap(AttributeModifier::getUniqueId, Function.identity()));
        }

        @Override
        public Attribute getAttribute() {
            return key.getAttribute();
        }

        @Override
        public double getDefaultValue() {
            return key.getDef();
        }

        /**
         * Get the value before modifiers have been applied.
         *
         * @return base value
         */
        @Override
        public double getBaseValue() {
            return value;
        }

        /**
         * Set the base value on which modifiers are applied.
         *
         * @param value new base value
         */
        @Override
        public void setBaseValue(double value) {
            this.value = value;
            onMutation();
        }

        /**
         * Add a modifier to this property.
         *
         * <p>
         * Attributes with the same uuid will be overridden according to
         * https://minecraft.gamepedia.com/Attribute#Attributes
         * </p>
         *
         * @param attributeModifier to add to this property
         */
        @Override
        public void addModifier(AttributeModifier attributeModifier) {
            this.modifiers.put(attributeModifier.getUniqueId(), attributeModifier);
            onMutation();
        }

        /**
         * Remove an attribute from this property.
         *
         * @param attributeModifier to remove from this property
         */
        @Override
        public void removeModifier(AttributeModifier attributeModifier) {
            this.modifiers.remove(attributeModifier.getUniqueId());
            onMutation();
        }

        private void onMutation() {
            this.isCacheUpToDate = false;
            AttributeManager.this.needsUpdate = true;
        }

        /**
         * Get value of this property after all modifiers have been applied.
         *
         * @return the resulting attribute value
         */
        @Override
        public double getValue() {
            if (!isCacheUpToDate) {
                cachedValue = computeValue();
                isCacheUpToDate = true;
            }

            return cachedValue;
        }

        private double computeValue() {
            double result = getBaseValue();

            for (AttributeModifier modifier : modifiers.values()) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADD_NUMBER) {
                    result += modifier.getAmount();
                }
            }

            double multiplier = 1.0;
            for (AttributeModifier modifier : modifiers.values()) {
                if (modifier.getOperation() == AttributeModifier.Operation.ADD_SCALAR) {
                    multiplier += modifier.getAmount();
                }
            }
            result *= multiplier;

            for (AttributeModifier modifier : modifiers.values()) {
                if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_SCALAR_1) {
                    result *= 1.0 + modifier.getAmount();
                }
            }

            return result;
        }

        /**
         * Get all modifiers assigned to this property.
         *
         * @return the modifiers of this property
         */
        public Collection<AttributeModifier> getModifiers() {
            return modifiers.values();
        }
    }

}
