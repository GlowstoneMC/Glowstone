package net.glowstone.util;

import net.glowstone.GlowServer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utilities for handling the server configuration files.
 */
public class ServerConfig {

    /**
     * The directory configurations are stored in.
     */
    public static final File CONFIG_DIR = new File("config");

    /**
     * The main configuration file.
     */
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "glowstone.yml");

    /**
     * The actual configuration data.
     */
    private final YamlConfiguration config = new YamlConfiguration();

    public ServerConfig() {
        config.options().indent(4);
    }

    private void createDefaults() {
        InputStream in = getClass().getClassLoader().getResourceAsStream("defaults/glowstone.yml");
        if (in == null) {
            GlowServer.logger.warning("Could not find default configuration on classpath");
            return;
        }

        try {
            OutputStream out = new FileOutputStream(CONFIG_FILE);
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Could not save default configuration", e);
            return;
        }

        GlowServer.logger.info("Created default configuration");
    }

    public void load() {
        // create default file if needed
        boolean exists = CONFIG_FILE.exists();
        if (!exists) {
            // create config directory
            if (!CONFIG_DIR.isDirectory() && !CONFIG_DIR.mkdirs()) {
                GlowServer.logger.severe("Cannot create directory: " + CONFIG_DIR);
                return;
            }

            createDefaults();
        }

        // load config
        try {
            config.load(CONFIG_FILE);
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to read config: " + CONFIG_FILE, e);
        } catch (InvalidConfigurationException e) {
            report(e);
        }

        // if we just created defaults, attempt to migrate
        if (migrate()) {
            // save config, including any new defaults
            try {
                config.save(CONFIG_FILE);
            } catch (IOException e) {
                GlowServer.logger.log(Level.SEVERE, "Failed to write config: " + CONFIG_FILE, e);
                return;
            }

            GlowServer.logger.info("Migrated configuration from CraftBukkit");
        }
    }

    private void report(InvalidConfigurationException e) {
        if (e.getCause() instanceof YAMLException) {
            GlowServer.logger.severe("Config file " + CONFIG_FILE + " isn't valid! " + e.getCause());
        } else if ((e.getCause() == null) || (e.getCause() instanceof ClassCastException)) {
            GlowServer.logger.severe("Config file " + CONFIG_FILE + " isn't valid!");
        } else {
            GlowServer.logger.log(Level.SEVERE, "Cannot load " + CONFIG_FILE + ": " + e.getCause().getClass(), e);
        }
    }

    public String getString(Key key) {
        return config.getString(key.path, key.def.toString());
    }

    public int getInt(Key key) {
        return config.getInt(key.path, (Integer) key.def);
    }

    public boolean getBoolean(Key key) {
        return config.getBoolean(key.path, (Boolean) key.def);
    }

    public ConfigurationSection getAliases() {
        return config.getConfigurationSection("aliases");
    }

    public ConfigurationSection getWorlds() {
        return config.getConfigurationSection("worlds");
    }

    private boolean migrate() {
        boolean migrateStatus = false;

        File bukkitYml = new File("bukkit.yml");
        if (bukkitYml.exists()) {
            YamlConfiguration bukkit = new YamlConfiguration();
            try {
                bukkit.load(bukkitYml);
            } catch (InvalidConfigurationException e) {
                report(e);
            } catch (IOException ignored) {
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.BUKKIT && bukkit.contains(key.migratePath)) {
                    config.set(key.path, bukkit.get(key.migratePath));
                    migrateStatus = true;
                }
            }

            config.set("aliases", bukkit.get("aliases"));
            config.set("worlds", bukkit.get("worlds"));
        }

        File serverProps = new File("server.properties");
        if (serverProps.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(serverProps));
            } catch (IOException ignored) {
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.PROPS && props.containsKey(key.migratePath)) {
                    config.set(key.path, props.get(key.migratePath));
                    migrateStatus = true;
                }
            }
        }

        return migrateStatus;
    }

    /**
     * An enum containing configuration keys used by the server.
     */
    public static enum Key {
        // server
        SERVER_IP("server.ip", "", Migrate.PROPS, "server-ip"),
        SERVER_PORT("server.port", 25565, Migrate.PROPS, "server-port"),
        SERVER_NAME("server.name", "Glowstone Server", Migrate.PROPS, "server-name"),
        LOG_FILE("server.log-file", "logs/log-%D.txt"),
        ONLINE_MODE("server.online-mode", true, Migrate.PROPS, "online-mode"),
        MAX_PLAYERS("server.max-players", 20, Migrate.PROPS, "max-players"),
        WHITELIST("server.whitelisted", false, Migrate.PROPS, "white-list"),
        MOTD("server.motd", "Glowstone Server", Migrate.PROPS, "motd"),
        SHUTDOWN_MESSAGE("server.shutdown-message", "Server shutting down", Migrate.BUKKIT, "settings.shutdown-message"),
        CONSOLE_MODE("server.console-mode", "jline"),

        // folders
        PLUGIN_FOLDER("folders.plugins", "plugins"),
        UPDATE_FOLDER("folders.update", "update", Migrate.BUKKIT, "settings.update-folder"),
        WORLD_FOLDER("folders.worlds", "worlds", Migrate.BUKKIT, "settings.world-container"),

        // advanced
        CONNECTION_THROTTLE("advanced.connection-throttle", 4000, Migrate.BUKKIT, "settings.connection-throttle"),
        //PING_PACKET_LIMIT("advanced.ping-packet-limit", 100, Migrate.BUKKIT, "settings.ping-packet-limit"),
        PLAYER_IDLE_TIMEOUT("advanced.idle-timeout", 0, Migrate.PROPS, "player-idle-timeout"),
        WARN_ON_OVERLOAD("advanced.warn-on-overload", true, Migrate.BUKKIT, "settings.warn-on-overload"),
        PERMISSIONS_FILE("advanced.permissions-file", "permissions.yml", Migrate.BUKKIT, "settings.permissions-file"),
        EXACT_LOGIN_LOCATION("advanced.exact-login-location", false, Migrate.BUKKIT, "settings.use-exact-login-location"),
        PLUGIN_PROFILING("advanced.plugin-profiling", false, Migrate.BUKKIT, "settings.plugin-profiling"),
        WARNING_STATE("advanced.deprecated-verbose", "false", Migrate.BUKKIT, "settings.deprecated-verbose"),

        // query rcon etc
        QUERY_ENABLED("extras.query-enabled", false, Migrate.PROPS, "enable-query"),
        QUERY_PORT("extras.query-port", 25614, Migrate.PROPS, "query.port"),
        QUERY_PLUGINS("extras.query-plugins", true, Migrate.BUKKIT, "settings.query-plugins"),
        RCON_ENABLED("extras.rcon-enabled", false, Migrate.PROPS, "enable-rcon"),
        RCON_PASSWORD("extras.rcon-password", "glowstone", Migrate.PROPS, "rcon.password"),
        RCON_PORT("extras.rcon-port", 25575, Migrate.PROPS, "rcon.port"),

        // level props
        LEVEL_NAME("world.name", "world", Migrate.PROPS, "level-name"),
        LEVEL_SEED("world.seed", "", Migrate.PROPS, "level-seed"),
        LEVEL_TYPE("world.level-type", "DEFAULT", Migrate.PROPS, "level-type"),
        SPAWN_RADIUS("world.spawn-radius", 16, Migrate.PROPS, "spawn-protection"),
        VIEW_DISTANCE("world.view-distance", 8, Migrate.PROPS, "view-distance"),
        GENERATE_STRUCTURES("world.gen-structures", true, Migrate.PROPS, "generate-structures"),
        GENERATOR_SETTINGS("world.gen-settings", "", Migrate.PROPS, "generator-settings"),
        ALLOW_NETHER("world.allow-nether", true, Migrate.PROPS, "allow-nether"),
        ALLOW_END("world.allow-end", true, Migrate.BUKKIT, "settings.allow-end"),

        // game props
        GAMEMODE("game.gamemode", "SURVIVAL", Migrate.PROPS, "gamemode"),
        FORCE_GAMEMODE("game.gamemode-force", "false", Migrate.PROPS, "force-gamemode"),
        DIFFICULTY("game.difficulty", "EASY", Migrate.PROPS, "difficulty"),
        HARDCORE("game.hardcore", false, Migrate.PROPS, "hardcore"),
        PVP_ENABLED("game.pvp", true, Migrate.PROPS, "pvp"),
        MAX_BUILD_HEIGHT("game.max-build-height", 256, Migrate.PROPS, "max-build-height"),
        ANNOUNCE_ACHIEVEMENTS("game.announce-achievements", true, Migrate.PROPS, "announce-player-achievements"),

        // server.properties keys
        ALLOW_FLIGHT("game.allow-flight", false, Migrate.PROPS, "allow-flight"),
        ENABLE_COMMAND_BLOCK("game.command-blocks", false, Migrate.PROPS, "enable-command-block"),
        //OP_PERMISSION_LEVEL(null, Migrate.PROPS, "op-permission-level"),
        RESOURCE_PACK("game.resource-pack", "", Migrate.PROPS, "resource-pack"),
        SNOOPER_ENABLED("server.snooper-enabled", false, Migrate.PROPS, "snooper-enabled"),

        // critters
        SPAWN_MONSTERS("creatures.enable.monsters", true, Migrate.PROPS, "spawn-monsters"),
        SPAWN_ANIMALS("creatures.enable.animals", true, Migrate.PROPS, "spawn-animals"),
        SPAWN_NPCS("creatures.enable.npcs", true, Migrate.PROPS, "spawn-npcs"),
        MONSTER_LIMIT("creatures.limit.monsters", 70, Migrate.BUKKIT, "spawn-limits.monsters"),
        ANIMAL_LIMIT("creatures.limit.animals", 15, Migrate.BUKKIT, "spawn-limits.animals"),
        WATER_ANIMAL_LIMIT("creatures.limit.water", 5, Migrate.BUKKIT, "spawn-limits.water-animals"),
        AMBIENT_LIMIT("creatures.limit.ambient", 15, Migrate.BUKKIT, "spawn-limits.ambient"),
        MONSTER_TICKS("creatures.ticks.monsters", 1, Migrate.BUKKIT, "ticks-per.monster-spawns"),
        ANIMAL_TICKS("creatures.ticks.animal", 400, Migrate.BUKKIT, "ticks-per.animal-spawns"),

        // database
        DB_DRIVER("database.driver", "org.sqlite.JDBC", Migrate.BUKKIT, "database.driver"),
        DB_URL("database.url", "jdbc:sqlite:config/database.db", Migrate.BUKKIT, "database.url"),
        DB_USERNAME("database.username", "glowstone", Migrate.BUKKIT, "database.username"),
        DB_PASSWORD("database.password", "nether", Migrate.BUKKIT, "database.password"),
        DB_ISOLATION("database.isolation", "SERIALIZABLE", Migrate.BUKKIT, "database.isolation"),

        ;

        private final String path;
        private final Object def;
        private final Migrate migrate;
        private final String migratePath;

        private Key(String path, Object def) {
            this(path, def, null, null);
        }

        private Key(String path, Object def, Migrate migrate, String migratePath) {
            this.path = path;
            this.def = def;
            this.migrate = migrate;
            this.migratePath = migratePath;
        }

        @Override
        public String toString() {
            return name() + "(" + path + ", " + def + ")";
        }
    }

    private static enum Migrate {
        BUKKIT, PROPS
    }

}
