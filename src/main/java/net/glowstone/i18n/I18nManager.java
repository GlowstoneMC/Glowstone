package net.glowstone.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class I18nManager {
    
    private static final Logger logger = LoggerFactory.getLogger(I18nManager.class);
    private static final String DEFAULT_LOCALE = "en_US";
    private static final String RESOURCE_PREFIX = "/i18n/";
    
    private static Map<String, Map<String, String>> localeData = new ConcurrentHashMap<>();
    private static Locale currentLocale = Locale.US;
    
    /**
     * Initialize the internationalization system
     */
    public static void init() {
        loadLocale(DEFAULT_LOCALE);
        logger.info("I18n initialized with default locale: " + DEFAULT_LOCALE);
    }
    
    /**
     * Load a specific locale from resources
     */
    public static void loadLocale(String locale) {
        try {
            String resourceName = RESOURCE_PREFIX + locale + ".properties";
            Map<String, String> translations = loadProperties(resourceName);
            
            if (translations != null) {
                localeData.put(locale, translations);
                logger.info("Loaded locale: " + locale + " (" + translations.size() + " translations)");
            } else {
                logger.warn("Could not load locale: " + locale + ", falling back to default");
            }
        } catch (Exception e) {
            logger.error("Error loading locale " + locale + ": " + e.getMessage());
        }
    }
    
    /**
     * Set the current active locale
     */
    public static void setCurrentLocale(Locale locale) {
        String localeString = locale.getLanguage() + "_" + locale.getCountry();
        
        if (localeData.containsKey(localeString)) {
            currentLocale = locale;
            logger.info("Active locale set to: " + localeString);
        } else {
            logger.warn("Locale not available: " + localeString + ", using default");
            currentLocale = Locale.US;
        }
    }
    
    /**
     * Get the current locale
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Translate a message key
     */
    public static String translate(String key) {
        return translate(key, new Object[0]);
    }
    
    /**
     * Translate a message key with arguments
     */
    public static String translate(String key, Object... args) {
        String value = getMessage(key);
        
        if (value != null) {
            return String.format(value, args);
        }
        
        logger.debug("Missing translation for key: " + key);
        return key;
    }
    
    /**
     * Get raw message value without formatting
     */
    public static String getMessage(String key) {
        // Try current locale first
        String currentLocaleString = currentLocale.getLanguage() + "_" + currentLocale.getCountry();
        Map<String, String> currentTranslations = localeData.get(currentLocaleString);
        
        if (currentTranslations != null && currentTranslations.containsKey(key)) {
            return currentTranslations.get(key);
        }
        
        // Fall back to default locale
        Map<String, String> defaultTranslations = localeData.get(DEFAULT_LOCALE);
        if (defaultTranslations != null) {
            return defaultTranslations.getOrDefault(key, key);
        }
        
        return key;
    }
    
    /**
     * Check if a translation exists
     */
    public static boolean hasTranslation(String key) {
        return getMessage(key) != null && !getMessage(key).equals(key);
    }
    
    /**
     * Load properties file from classpath
     */
    private static Map<String, String> loadProperties(String resourceName) {
        Map<String, String> properties = new LinkedHashMap<>();
        
        try (InputStream input = I18nManager.class.getResourceAsStream(resourceName)) {
            if (input == null) {
                logger.warn("Resource not found: " + resourceName);
                return null;
            }
            
            Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name());
            scanner.useDelimiter("\\A");
            
            String content = scanner.hasNext() ? scanner.next() : "";
            
            // Parse properties file manually for simplicity
            String[] lines = content.split("\n");
            for (String line : lines) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
                    continue;
                }
                
                // Parse key=value
                int separatorIndex = line.indexOf('=');
                if (separatorIndex == -1) {
                    separatorIndex = line.indexOf(':');
                }
                
                if (separatorIndex > 0) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();
                    
                    // Handle Unicode escapes
                    value = value.replace("\\n", "\n")
                                .replace("\\t", "\t")
                                .replace("\\r", "\r");
                    
                    properties.put(key, value);
                }
            }
            
            logger.debug("Loaded " + properties.size() + " translations from " + resourceName);
            
        } catch (IOException e) {
            logger.error("Error reading properties file " + resourceName + ": " + e.getMessage());
            return null;
        }
        
        return properties;
    }
    
    /**
     * Get list of available locales
     */
    public static List<String> getAvailableLocales() {
        return new ArrayList<>(localeData.keySet());
    }
}
