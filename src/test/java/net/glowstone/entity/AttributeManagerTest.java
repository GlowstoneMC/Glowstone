package net.glowstone.entity;

import static org.junit.Assert.*;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AttributeManagerTest {
    private static final double DELTA = 0.001;

    public static class KeyTest {
        /**
         * Require that there is a corresponding key for each Attribute.
         * This test will fail if new attributes are introduced to the bukkit api.
         */
        @Test
        public void requireKeysForAttributes() {
            Set<Attribute> attributes = new HashSet<>(Arrays.asList(Attribute.values()));
            Set<Attribute> implementedAttributes = Arrays.stream(AttributeManager.Key.values())
                    .map(AttributeManager.Key::getAttribute)
                    .collect(Collectors.toSet());

            attributes.removeAll(implementedAttributes);

            if (!attributes.isEmpty()) {
                throw new RuntimeException("Some Attributes are not supported by glowstone: " + attributes);
            }
        }
    }

    public static class PropertyTest {
        private AttributeManager newManager() {
            GlowLivingEntity entity = PowerMockito.mock(GlowLivingEntity.class);
            return new AttributeManager(entity);
        }

        private AttributeManager.Property newProperty(AttributeManager.Key key, double value) {
            AttributeManager manager = newManager();
            manager.setProperty(key, value);
            return manager.getProperty(key);
        }

        private AttributeManager.Property defaultProperty(AttributeManager.Key key) {
            return newProperty(key, key.getDef());
        }

        @Test
        public void testAddition() {
            AttributeManager.Key key = AttributeManager.Key.KEY_LUCK;
            AttributeManager.Property property = defaultProperty(key);

            double def = key.getDef();
            assertEquals("prerequirement", def, property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 01",
                    15, AttributeModifier.Operation.ADD_NUMBER));

            assertEquals(def + 15, property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 02",
                    20, AttributeModifier.Operation.ADD_NUMBER));

            assertEquals(def + 15 + 20, property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 03",
                    -32, AttributeModifier.Operation.ADD_NUMBER));

            assertEquals(def + 15 + 20 - 32, property.getValue(), DELTA);
        }

        @Test
        public void testAddScalar() {
            double def = 5;
            AttributeManager.Property property = newProperty(AttributeManager.Key.KEY_LUCK, def);

            assertEquals("prerequirement", def, property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 01",
                    2, AttributeModifier.Operation.ADD_SCALAR));

            assertEquals(def * (1 + 2), property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 02",
                    3, AttributeModifier.Operation.ADD_SCALAR));

            assertEquals(def * (1 + 2 + 3), property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 03",
                    -9, AttributeModifier.Operation.ADD_SCALAR));

            assertEquals(def * (1 + 2 + 3 - 9), property.getValue(), DELTA);
        }

        @Test
        public void testMultiply1() {
            double def = 5;
            AttributeManager.Property property = newProperty(AttributeManager.Key.KEY_LUCK, def);

            assertEquals("prerequirement", def, property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 01",
                    2, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

            assertEquals(def * (1 + 2), property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 02",
                    3, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

            assertEquals(def * (1 + 2) * (1 + 3), property.getValue(), DELTA);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name 03",
                    -9, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

            assertEquals(def * (1 + 2) * (1 + 3) * (1 - 9), property.getValue(), DELTA);
        }

        @Test
        public void testMin() {
            AttributeManager.Property property = defaultProperty(AttributeManager.Key.KEY_LUCK);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name",
                    -10000, AttributeModifier.Operation.ADD_NUMBER));

            assertEquals(AttributeManager.Key.KEY_LUCK.getMin(), property.getValue(), DELTA);
        }

        @Test
        public void testMax() {
            AttributeManager.Property property = defaultProperty(AttributeManager.Key.KEY_LUCK);

            property.addModifier(new AttributeModifier(
                    UUID.randomUUID(), "random name",
                    100000, AttributeModifier.Operation.ADD_NUMBER));

            assertEquals(AttributeManager.Key.KEY_LUCK.getMax(), property.getValue(), DELTA);
        }
    }

}

