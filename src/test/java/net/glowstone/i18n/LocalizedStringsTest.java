package net.glowstone.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class LocalizedStringsTest {
    /**
     * Keys in strings.properties used in ways other than in LocalizedString instances in
     * GlowstoneMessages, and which GlowstoneMessages should therefore not be expected to cover.
     */
    private static final Set<String> EXEMPT_KEYS = ImmutableSet.of(
            "glowstone.difficulty.names",
            "glowstone.difficulty.unknown",
            "glowstone.gamemode.names",
            "glowstone.gamemode.unknown");
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("strings");
    private static final String MOCK_KEY = "foo";
    private static final String MOCK_VALUE_NO_FORMAT = "bar";
    private static final String MOCK_VALUE_WITH_FORMAT = "bar {0} baz";
    private static final String MOCK_FORMAT_PARAMETER = "zappa";
    private static final String MOCK_VALUE_WITH_FORMAT_EXPECTED = "bar zappa baz";
    private static final Level MOCK_LOG_LEVEL = Level.FINEST;

    @Test
    void testGetNoFormat() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_NO_FORMAT));

        LocalizedString localizedString = new LocalizedStringImpl(MOCK_KEY, mockBundle);
        String actualValue = localizedString.get();

        assertEquals(MOCK_VALUE_NO_FORMAT, actualValue);
    }

    @Test
    void testGetWithFormat() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_WITH_FORMAT));

        LocalizedString localizedString = new LocalizedStringImpl(MOCK_KEY, mockBundle);
        String actualValue = localizedString.get(MOCK_FORMAT_PARAMETER);

        assertEquals(MOCK_VALUE_WITH_FORMAT_EXPECTED, actualValue);
    }

    @Test
    void testLogNoFormat() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_NO_FORMAT));

        Logger mockLogger = mock(Logger.class);

        LoggableLocalizedString localizedString = new LoggableLocalizedStringImpl(MOCK_KEY, MOCK_LOG_LEVEL, mockBundle, mockLogger);
        localizedString.log();

        verify(mockLogger).log(MOCK_LOG_LEVEL, MOCK_VALUE_NO_FORMAT);
        verifyNoMoreInteractions(mockLogger);
    }

    @Test
    void testLogWithFormat() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_WITH_FORMAT));

        Logger mockLogger = mock(Logger.class);

        LoggableLocalizedString localizedString = new LoggableLocalizedStringImpl(MOCK_KEY, MOCK_LOG_LEVEL, mockBundle, mockLogger);
        localizedString.log(MOCK_FORMAT_PARAMETER);

        verify(mockLogger).log(MOCK_LOG_LEVEL, MOCK_VALUE_WITH_FORMAT_EXPECTED);
        verifyNoMoreInteractions(mockLogger);
    }

    @Test
    void testLogNoFormatWithException() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_NO_FORMAT));

        Logger mockLogger = mock(Logger.class);

        Exception exception = new Exception();

        LoggableLocalizedString localizedString = new LoggableLocalizedStringImpl(MOCK_KEY, MOCK_LOG_LEVEL, mockBundle, mockLogger);
        localizedString.log(exception);

        verify(mockLogger).log(MOCK_LOG_LEVEL, MOCK_VALUE_NO_FORMAT, exception);
        verifyNoMoreInteractions(mockLogger);
    }

    @Test
    void testLogWithFormatWithException() {
        ResourceBundle mockBundle = new MockResourceBundle(ImmutableMap.of(MOCK_KEY, MOCK_VALUE_WITH_FORMAT));

        Logger mockLogger = mock(Logger.class);

        Exception exception = new Exception();

        LoggableLocalizedString localizedString = new LoggableLocalizedStringImpl(MOCK_KEY, MOCK_LOG_LEVEL, mockBundle, mockLogger);
        localizedString.log(exception, MOCK_FORMAT_PARAMETER);

        verify(mockLogger).log(MOCK_LOG_LEVEL, MOCK_VALUE_WITH_FORMAT_EXPECTED, exception);
        verifyNoMoreInteractions(mockLogger);
    }

    /**
     * This test verifies that each {@link LocalizedString} instance in {@link ConsoleMessages} and
     * {@link GlowstoneMessages} corresponds to an entry in {@code strings.properties}, and that
     * each entry in {@code strings.properties} corresponds to <em>at least</em> one
     * {@link LocalizedString}. More than one are allowed for the same entry, since a string may be
     * used for logging at multiple levels.
     *
     * @throws Exception if refactoring causes reflection issues
     */
    @Test
    void testForBundleCompatibility() throws Exception {
        final Set<String> bundleKeys = STRINGS.keySet();
        final Set<String> unusedKeys = new HashSet<>(bundleKeys);
        final Set<String> missingRegisteredKeys = new HashSet<>();

        Deque<Class<?>> classesToScan = new ArrayDeque<>(Arrays.asList(
                ConsoleMessages.class.getDeclaredClasses()));
        classesToScan.addAll(Arrays.asList(GlowstoneMessages.class.getDeclaredClasses()));
        while (!classesToScan.isEmpty()) {
            Class<?> innerClass = classesToScan.removeFirst();
            classesToScan.addAll(Arrays.asList(innerClass.getDeclaredClasses()));
            for (Field field : innerClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers())) {
                    Object value = field.get(null);
                    if (value instanceof LocalizedStringImpl) {
                        validateLocalizedString(bundleKeys, missingRegisteredKeys, unusedKeys,
                                (LocalizedStringImpl) value);
                    } else {
                        fail("Field '" + field + "' does not contain an object of type '" + LocalizedStringImpl.class.getName() + "'.");
                    }
                } else {
                    fail("Class '" + innerClass.getName() + "' contains the non-static, non-final field '" + field + "'.");
                }
            }
        }
        unusedKeys.removeAll(EXEMPT_KEYS);

        assertTrue("Resource file contains unused keys: " + unusedKeys, unusedKeys.isEmpty());
        assertTrue("Nonexistent keys are being referenced: " + missingRegisteredKeys,
                missingRegisteredKeys.isEmpty());
    }

    private void validateLocalizedString(Set<String> bundleKeys, Set<String> missingRegisteredKeys,
                                         Set<String> unusedKeys, LocalizedStringImpl localized) {
        String key = localized.getKey();
        if (bundleKeys.contains(key)) {
            unusedKeys.remove(key);
            String configValue = STRINGS.getString(key);
            String objectValue = localized.get();

            assertEquals(objectValue, configValue);
            assertFalse(objectValue.isEmpty());
            assertFalse(CharMatcher.whitespace().matchesAllOf(objectValue));
        } else {
            missingRegisteredKeys.add(key);
        }
    }

    // ResourceBundles cannot be mocked using regular Mockito. You can use PowerMockito but that adds a lot of time to
    // run the tests on the build server. So, instead, I created a minimal resource bundle class instead.
    private static class MockResourceBundle extends ResourceBundle {
        private final Map<String, Object> resources;

        private MockResourceBundle(Map<String, Object> resources) {
            this.resources = resources;
        }

        @Override
        protected Object handleGetObject(@NotNull String key) {
            return resources.get(key);
        }

        @NotNull
        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(resources.keySet());
        }
    }
}
