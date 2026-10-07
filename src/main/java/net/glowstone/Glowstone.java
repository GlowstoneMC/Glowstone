package net.glowstone;

import net.glowstone.i18n.I18nManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class Glowstone {
    
    private static final Logger logger = LoggerFactory.getLogger(Glowstone.class);
    private static Glowstone instance;
    
    public static void main(String[] args) {
        instance = new Glowstone();
        
        // Initialize internationalization manager
        I18nManager.init();
        
        logger.info("Glowstone starting up...");
        logger.info("Supported locales: pt_BR, en_US, es_ES, fr_FR, de_DE, ja_JP, ko_KR, zh_CN, ru_RU");
        
        // Start server initialization
        initializeServer();
    }
    
    private static void initializeServer() {
        try {
            // Load configuration
            logger.info("Loading configuration...");
            
            // Initialize game world
            logger.info("Initializing game world...");
            
            logger.info("Server ready!");
        } catch (Exception e) {
            logger.error("Failed to initialize server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public static Glowstone getInstance() {
        return instance;
    }
}
