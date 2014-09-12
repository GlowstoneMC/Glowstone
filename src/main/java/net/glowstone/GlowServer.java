package net.glowstone;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.glowstone.command.ColorCommand;
import net.glowstone.command.TellrawCommand;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.CraftingManager;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.io.PlayerDataService;
import net.glowstone.map.GlowMapView;
import net.glowstone.net.GlowNetworkServer;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.scheduler.WorldScheduler;
import net.glowstone.util.*;
import net.glowstone.util.bans.GlowBanList;
import net.glowstone.util.bans.UuidListFile;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.permissions.DefaultPermissions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The core class of the Glowstone server.
 * @author Graham Edgecombe
 */
public final class GlowServer implements Server {

    /**
     * The logger for this class.
     */
    public static final Logger logger = Logger.getLogger("Minecraft");

    /**
     * The game version supported by the server.
     */
    public static final String GAME_VERSION = "1.8";

    /**
     * The protocol version supported by the server.
     */
    public static final int PROTOCOL_VERSION = 47;

    /**
     * Creates a new server on TCP port 25565 and starts listening for
     * connections.
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        try {
            ConfigurationSerialization.registerClass(GlowOfflinePlayer.class);

            // parse arguments and read config
            final ServerConfig config = parseArguments(args);
            if (config == null) {
                return;
            }

            // start server
            final GlowServer server = new GlowServer(config);
            server.start();
            server.bind();
            logger.info("Ready for connections.");
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error during server startup.", t);
            System.exit(1);
        }
    }

    private static ServerConfig parseArguments(String[] args) {
        final Map<ServerConfig.Key, Object> parameters = new EnumMap<>(ServerConfig.Key.class);
        String configDirName = "config";
        String configFileName = "glowstone.yml";

        // Calculate acceptable parameters
        for (int i = 0; i < args.length; i++) {
            final String opt = args[i];

            if (!opt.startsWith("-")) {
                System.err.println("Invalid Option: " + opt);
                System.exit(1);
                return null;
            }

            // Help
            if ("--help".equals(opt) || "-h".equals(opt) || "-?".endsWith(opt)) {
                System.out.println("Available command-line options:");
                System.out.println("  --help, -h, -?                 Shows this help message and exits.");
                System.out.println("  --version, -v                  Displays version information and exits.");
                System.out.println("  --configdir <directory>        Sets the configuration directory.");
                System.out.println("  --configfile <file>            Sets the configuration file.");
                System.out.println("  --port, -p <port>              Sets the server listening port.");
                System.out.println("  --host, -H <ip | hostname>     Sets the server listening address.");
                System.out.println("  --onlinemode, -o <onlinemode>  Sets the server's online-mode.");
                return null;
            }

            // Version
            if ("--version".equals(opt) || "-v".equals(opt)) {
                System.out.println("Glowstone version: " + GlowServer.class.getPackage().getImplementationVersion());
                System.out.println("Bukkit version:    " + GlowServer.class.getPackage().getSpecificationVersion());
                System.out.println("Minecraft version: " + GAME_VERSION + " protocol " + PROTOCOL_VERSION);
                return null;
            }

            // Below this point, options require parameters
            if (i == args.length - 1) {
                System.err.println("Option specified without value: " + opt);
                System.exit(1);
                return null;
            }

            if ("--configdir".equals(opt)) {
                configDirName = args[++i];
                continue;
            }

            if ("--configfile".equals(opt)) {
                configFileName = args[++i];
                continue;
            }

            if ("--port".equals(opt) || "-p".equals(opt)) {
                parameters.put(ServerConfig.Key.SERVER_PORT, Integer.valueOf(args[++i]));
                continue;
            }

            if ("--host".equals(opt) || "-H".equals(opt)) {
                parameters.put(ServerConfig.Key.SERVER_IP, args[++i]);
                continue;
            }

            if ("--onlinemode".equals(opt) || "-o".equals(opt)) {
                parameters.put(ServerConfig.Key.ONLINE_MODE, Boolean.valueOf(args[++i]));
                continue;
            }

            System.err.println("Unknown option: " + opt);
            System.exit(1);
            return null;
        }

        final File configDir = new File(configDirName);
        final File configFile = new File(configDir, configFileName);

        return new ServerConfig(configDir, configFile, parameters);
    }

    /**
     * A list of all the active {@link net.glowstone.net.GlowSession}s.
     */
    private final SessionRegistry sessions = new SessionRegistry();

    /**
     * The console manager of this server.
     */
    private final ConsoleManager consoleManager = new ConsoleManager(this);

    /**
     * The services manager of this server.
     */
    private final SimpleServicesManager servicesManager = new SimpleServicesManager();

    /**
     * The command map of this server.
     */
    private final SimpleCommandMap commandMap = new SimpleCommandMap(this);

    /**
     * The plugin manager of this server.
     */
    private final PluginManager pluginManager = new SimplePluginManager(this, commandMap);

    /**
     * The plugin channel messenger for the server.
     */
    private final Messenger messenger = new StandardMessenger();

    /**
     * The help map for the server.
     */
    private final GlowHelpMap helpMap = new GlowHelpMap(this);

    /**
     * The scoreboard manager for the server.
     */
    private final ScoreboardManager scoreboardManager = null;

    /**
     * The crafting manager for this server.
     */
    private final CraftingManager craftingManager = new CraftingManager();

    /**
     * The configuration for the server.
     */
    private final ServerConfig config;

    /**
     * The list of OPs on the server.
     */
    private final UuidListFile opsList;

    /**
     * The list of players whitelisted on the server.
     */
    private final UuidListFile whitelist;

    /**
     * The BanList for player names.
     */
    private final GlowBanList nameBans;

    /**
     * The BanList for IP addresses.
     */
    private final GlowBanList ipBans;

    /**
     * The world this server is managing.
     */
    private final WorldScheduler worlds = new WorldScheduler();

    /**
     * The task scheduler used by this server.
     */
    private final GlowScheduler scheduler = new GlowScheduler(this, worlds);

    /**
     * The Bukkit UnsafeValues implementation.
     */
    private final UnsafeValues unsafeAccess = new GlowUnsafeValues();

    /**
     * An empty player array used for deprecated getOnlinePlayers.
     */
    private final Player[] EMPTY_PLAYER_ARRAY = new Player[0];

    /**
     * The server's default game mode
     */
    private GameMode defaultGameMode = GameMode.CREATIVE;

    /**
     * The setting for verbose deprecation warnings.
     */
    private Warning.WarningState warnState = Warning.WarningState.DEFAULT;

    /**
     * Whether the server is shutting down
     */
    private boolean isShuttingDown = false;

    /**
     * Whether the whitelist is in effect.
     */
    private boolean whitelistEnabled;

    /**
     * The size of the area to keep protected around the spawn point.
     */
    private int spawnRadius;

    /**
     * The ticks until a player who has not played the game has been kicked, or 0.
     */
    private int idleTimeout;

    /**
     * A RSA key pair used for encryption and authentication
     */
    private final KeyPair keyPair = SecurityUtils.generateKeyPair();

    /**
     * The network server used for network communication
     */
    private final GlowNetworkServer networkServer = new GlowNetworkServer(this);

    /**
     * The default icon, usually blank, used for the server list.
     */
    private GlowServerIcon defaultIcon;

    /**
     * Creates a new server.
     */
    public GlowServer(ServerConfig config) {
        this.config = config;
        // stuff based on selected config directory
        opsList = new UuidListFile(config.getFile("ops.json"));
        whitelist = new UuidListFile(config.getFile("whitelist.json"));
        nameBans = new GlowBanList(this, BanList.Type.NAME);
        ipBans = new GlowBanList(this, BanList.Type.IP);

        Bukkit.setServer(this);
        loadConfig();
    }

    /**
     * Starts this server.
     */
    public void start() {
        // Determine console mode and start reading input
        consoleManager.startConsole(config.getBoolean(ServerConfig.Key.USE_JLINE));
        consoleManager.startFile(config.getString(ServerConfig.Key.LOG_FILE));

        if (!getOnlineMode()) {
            logger.log(Level.WARNING, "The server is running in offline mode! Only do this if you know what you're doing.");
        }

        // Load player lists
        opsList.load();
        whitelist.load();
        nameBans.load();
        ipBans.load();

        // Start loading plugins
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);

        // Create worlds
        String name = config.getString(ServerConfig.Key.LEVEL_NAME);
        String seedString = config.getString(ServerConfig.Key.LEVEL_SEED);
        long seed = seedString.isEmpty() ? System.currentTimeMillis() : seedString.hashCode();
        boolean structs = getGenerateStructures();
        WorldType type = WorldType.getByName(getWorldType());
        if (type == null) {
            type = WorldType.NORMAL;
        }

        createWorld(WorldCreator.name(name).environment(Environment.NORMAL).seed(seed).type(type).generateStructures(structs));
        if (getAllowNether()) {
            createWorld(WorldCreator.name(name + "_nether").environment(Environment.NETHER).seed(seed).type(type).generateStructures(structs));
        }
        if (getAllowEnd()) {
            createWorld(WorldCreator.name(name + "_the_end").environment(Environment.THE_END).seed(seed).type(type).generateStructures(structs));
        }

        // Finish loading plugins
        enablePlugins(PluginLoadOrder.POSTWORLD);
        commandMap.registerServerAliases();
        scheduler.start();
    }

    /**
     * Binds this server to the address specified in the configuration.
     */
    private void bind() {
        String ip = getIp();
        int port = getPort();

        SocketAddress address;
        if (ip.length() == 0) {
            address = new InetSocketAddress(port);
        } else {
            address = new InetSocketAddress(ip, port);
        }

        logger.info("Binding to address: " + address + "...");
        ChannelFuture future = networkServer.bind(address);
        Channel channel = future.awaitUninterruptibly().channel();
        if (!channel.isActive()) {
            throw new RuntimeException("Failed to bind to address. Maybe it is already in use?");
        }
    }

    /**
     * Stops this server.
     */
    @Override
    public void shutdown() {
        // Just in case this gets called twice
        if (isShuttingDown) {
            return;
        }
        isShuttingDown = true;
        logger.info("The server is shutting down...");

        // Disable plugins
        pluginManager.clearPlugins();

        // Kick all players (this saves their data too)
        for (Player player : getOnlinePlayers()) {
            player.kickPlayer(getShutdownMessage());
        }

        // Stop the network server - starts the shutdown process
        // It may take a second or two for Netty to totally clean up
        networkServer.shutdown();

        // Save worlds
        for (World world : getWorlds()) {
            logger.info("Saving world: " + world.getName());
            unloadWorld(world, true);
        }

        // Stop scheduler and console
        scheduler.stop();
        consoleManager.stop();

        // Wait for a while and terminate any rogue threads
        new ShutdownMonitorThread().start();
    }

    /**
     * Load the server configuration.
     */
    private void loadConfig() {
        config.load();

        // modifiable values
        spawnRadius = config.getInt(ServerConfig.Key.SPAWN_RADIUS);
        whitelistEnabled = config.getBoolean(ServerConfig.Key.WHITELIST);
        idleTimeout = config.getInt(ServerConfig.Key.PLAYER_IDLE_TIMEOUT);
        craftingManager.initialize();

        // special handling
        warnState = Warning.WarningState.value(config.getString(ServerConfig.Key.WARNING_STATE));
        try {
            defaultGameMode = GameMode.valueOf(GameMode.class, config.getString(ServerConfig.Key.GAMEMODE));
        } catch (IllegalArgumentException | NullPointerException e) {
            defaultGameMode = GameMode.SURVIVAL;
        }

        // server icon
        defaultIcon = new GlowServerIcon();
        try {
            File file = config.getFile("server-icon.png");
            if (file.isFile()) {
                defaultIcon = new GlowServerIcon(file);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load server-icon.png", e);
        }
    }

    /**
     * Loads all plugins, calling onLoad, &c.
     */
    private void loadPlugins() {
        // clear the map
        commandMap.clearCommands();
        commandMap.register("glowstone", new ColorCommand("colors"));
        commandMap.register("glowstone", new TellrawCommand());

        File folder = new File(config.getString(ServerConfig.Key.PLUGIN_FOLDER));
        if (!folder.isDirectory() && !folder.mkdirs()) {
            logger.log(Level.SEVERE, "Could not create plugins directory: " + folder);
        }

        // clear plugins and prepare to load
        pluginManager.clearPlugins();
        pluginManager.registerInterface(JavaPluginLoader.class);
        Plugin[] plugins = pluginManager.loadPlugins(folder);

        // call onLoad methods
        for (Plugin plugin : plugins) {
            try {
                plugin.onLoad();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error loading " + plugin.getDescription().getFullName(), ex);
            }
        }
    }

    /**
     * Enable all plugins of the given load order type.
     * @param type The type of plugin to enable.
     */
    private void enablePlugins(PluginLoadOrder type) {
        if (type == PluginLoadOrder.STARTUP) {
            helpMap.clear();
            helpMap.initializeGeneralTopics();
        }

        // load all the plugins
        Plugin[] plugins = pluginManager.getPlugins();
        for (Plugin plugin : plugins) {
            if (!plugin.isEnabled() && plugin.getDescription().getLoad() == type) {
                List<Permission> perms = plugin.getDescription().getPermissions();
                for (Permission perm : perms) {
                    try {
                        pluginManager.addPermission(perm);
                    } catch (IllegalArgumentException ex) {
                        getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
                    }
                }

                try {
                    pluginManager.enablePlugin(plugin);
                } catch (Throwable ex) {
                    logger.log(Level.SEVERE, "Error loading " + plugin.getDescription().getFullName(), ex);
                }
            }
        }

        if (type == PluginLoadOrder.POSTWORLD) {
            commandMap.setFallbackCommands();
            commandMap.registerServerAliases();
            DefaultPermissions.registerCorePermissions();
            helpMap.initializeCommands();

            // load permissions.yml
            ConfigurationSection permConfig = config.getConfigFile(ServerConfig.Key.PERMISSIONS_FILE);
            List<Permission> perms = Permission.loadPermissions(permConfig.getValues(false), "Permission node '%s' in permissions config is invalid", PermissionDefault.OP);
            for (Permission perm : perms) {
                try {
                    pluginManager.addPermission(perm);
                } catch (IllegalArgumentException ex) {
                    getLogger().log(Level.WARNING, "Permission config tried to register '" + perm.getName() + "' but it's already registered", ex);
                }
            }
        }
    }

    /**
     * Reloads the server, refreshing settings and plugin information
     */
    @Override
    public void reload() {
        try {
            // Reload relevant configuration
            loadConfig();
            opsList.load();
            whitelist.load();
            nameBans.load();
            ipBans.load();

            // Reset crafting
            craftingManager.resetRecipes();

            // Load plugins
            loadPlugins();
            enablePlugins(PluginLoadOrder.STARTUP);
            enablePlugins(PluginLoadOrder.POSTWORLD);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Uncaught error while reloading", ex);
        }
    }

    @Override
    public String toString() {
        return "GlowServer{name=" + getName() + ",version=" + getVersion() + ",minecraftVersion=" + GAME_VERSION + "}";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to internals

    /**
     * Gets the command map.
     * @return The {@link SimpleCommandMap}.
     */
    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * Gets the session registry.
     * @return The {@link SessionRegistry}.
     */
    public SessionRegistry getSessionRegistry() {
        return sessions;
    }

    /**
     * Returns the list of OPs on this server.
     */
    public UuidListFile getOpsList() {
        return opsList;
    }

    /**
     * Returns the list of whitelisted players on this server.
     */
    public UuidListFile getWhitelist() {
        return whitelist;
    }

    /**
     * Returns the folder where configuration files are stored
     */
    public File getConfigDir() {
        return config.getDirectory();
    }

    /**
     * Return the crafting manager.
     * @return The server's crafting manager.
     */
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    /**
     * The key pair generated at server start up
     * @return The key pair generated at server start up
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Returns the player data service attached to the first world.
     * @return The server's player data service.
     */
    public PlayerDataService getPlayerDataService() {
        return worlds.getWorlds().get(0).getStorage().getPlayerDataService();
    }

    /**
     * Get the threshold to use for network compression defined in the config.
     * @return The compression threshold, or -1 for no compression.
     */
    public int getCompressionThreshold() {
        return config.getInt(ServerConfig.Key.COMPRESSION_THRESHOLD);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Static server properties

    @Override
    public String getName() {
        return "Glowstone";
    }

    @Override
    public String getVersion() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public String getBukkitVersion() {
        return getClass().getPackage().getSpecificationVersion();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isPrimaryThread() {
        return scheduler.isPrimaryThread();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to Bukkit API

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public GlowScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public HelpMap getHelpMap() {
        return helpMap;
    }

    @Override
    public ItemFactory getItemFactory() {
        return GlowItemFactory.instance();
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    @Deprecated
    public UnsafeValues getUnsafe() {
        return unsafeAccess;
    }

    @Override
    public BanList getBanList(BanList.Type type) {
        switch (type) {
            case NAME:
                return nameBans;
            case IP:
                return ipBans;
            default:
                throw new IllegalArgumentException("Unknown BanList type " + type);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Commands and console

    @Override
    public ConsoleCommandSender getConsoleSender() {
        return consoleManager.getSender();
    }

    @Override
    public PluginCommand getPluginCommand(String name) {
        Command command = commandMap.getCommand(name);
        if (command instanceof PluginCommand) {
            return (PluginCommand) command;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String[]> getCommandAliases() {
        Map<String, String[]> aliases = new HashMap<>();
        ConfigurationSection section = config.getConfigFile(ServerConfig.Key.COMMANDS_FILE).getConfigurationSection("aliases");
        if (section == null) {
            return aliases;
        }
        for (String key : section.getKeys(false)) {
            List<String> list = section.getStringList(key);
            aliases.put(key, list.toArray(new String[list.size()]));
        }
        return aliases;
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
        if (commandMap.dispatch(sender, commandLine)) {
            return true;
        }

        String firstword = commandLine;
        if (firstword.indexOf(' ') >= 0) {
            firstword = firstword.substring(0, firstword.indexOf(' '));
        }

        sender.sendMessage(ChatColor.GRAY + "Unknown command \"" + firstword + "\", try \"help\"");
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management

    @Override
    public Set<OfflinePlayer> getOperators() {
        Set<OfflinePlayer> offlinePlayers = new HashSet<>();
        for (UUID uuid : opsList.getUUIDs()) {
            offlinePlayers.add(getOfflinePlayer(uuid));
        }
        return offlinePlayers;
    }

    @Override
    @Deprecated
    public Player[] _INVALID_getOnlinePlayers() {
        return getOnlinePlayers().toArray(EMPTY_PLAYER_ARRAY);
    }

    @Override
    public Collection<GlowPlayer> getOnlinePlayers() {
        // todo: provide a view instead of reassembling the list each time
        ArrayList<GlowPlayer> result = new ArrayList<>();
        for (GlowWorld world : worlds.getWorlds()) {
            for (GlowPlayer player : world.getRawPlayers()) {
                result.add(player);
            }
        }
        return result;
    }

    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        Set<OfflinePlayer> result = new HashSet<>();
        Set<UUID> uuids = new HashSet<>();

        // add the currently online players
        for (World world : getWorlds()) {
            for (Player player : world.getPlayers()) {
                result.add(player);
                uuids.add(player.getUniqueId());
            }
        }

        // add all offline players that aren't already online
        for (OfflinePlayer offline : getPlayerDataService().getOfflinePlayers()) {
            if (!uuids.contains(offline.getUniqueId())) {
                result.add(offline);
                uuids.add(offline.getUniqueId());
            }
        }

        return result.toArray(new OfflinePlayer[result.size()]);
    }

    @Override
    public Player getPlayer(String name) {
        name = name.toLowerCase();
        Player bestPlayer = null;
        int bestDelta = -1;
        for (Player player : getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(name)) {
                int delta = player.getName().length() - name.length();
                if (bestPlayer == null || delta < bestDelta) {
                    bestPlayer = player;
                }
            }
        }
        return bestPlayer;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        for (Player player : getOnlinePlayers()) {
            if (player.getUniqueId().equals(uuid)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public Player getPlayerExact(String name) {
        for (Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public List<Player> matchPlayer(String name) {
        name = name.toLowerCase();

        ArrayList<Player> result = new ArrayList<>();
        for (Player player : getOnlinePlayers()) {
            String lower = player.getName().toLowerCase();
            if (lower.equals(name)) {
                result.clear();
                result.add(player);
                break;
            } else if (lower.contains(name)) {
                result.add(player);
            }
        }
        return result;
    }

    @Override
    @Deprecated
    public OfflinePlayer getOfflinePlayer(String name) {
        Player onlinePlayer = getPlayerExact(name);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }
        return new GlowOfflinePlayer(this, name);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        Player onlinePlayer = getPlayer(uuid);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }
        return new GlowOfflinePlayer(this, uuid);
    }

    @Override
    public void savePlayers() {
        for (Player player : getOnlinePlayers()) {
            player.saveData();
        }
    }

    @Override
    public int broadcastMessage(String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    @Override
    public int broadcast(String message, String permission) {
        int count = 0;
        for (Permissible permissible : getPluginManager().getPermissionSubscriptions(permission)) {
            if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
                ((CommandSender) permissible).sendMessage(message);
                ++count;
            }
        }
        return count;
    }

    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        Set<OfflinePlayer> players = new HashSet<>();
        for (UUID uuid : whitelist.getUUIDs()) {
            players.add(getOfflinePlayer(uuid));
        }
        return players;
    }

    @Override
    public void reloadWhitelist() {
        whitelist.load();
    }

    @Override
    public Set<String> getIPBans() {
        Set<String> result = new HashSet<>();
        for (BanEntry entry : ipBans.getBanEntries()) {
            result.add(entry.getTarget());
        }
        return result;
    }

    @Override
    public void banIP(String address) {
        ipBans.addBan(address, null, null, null);
    }

    @Override
    public void unbanIP(String address) {
        ipBans.pardon(address);
    }

    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        Set<OfflinePlayer> bannedPlayers = new HashSet<>();
        for (BanEntry entry : nameBans.getBanEntries()) {
            bannedPlayers.add(getOfflinePlayer(entry.getTarget()));
        }
        return bannedPlayers;
    }

    ////////////////////////////////////////////////////////////////////////////
    // World management

    @Override
    public GlowWorld getWorld(String name) {
        return worlds.getWorld(name);
    }

    @Override
    public GlowWorld getWorld(UUID uid) {
        for (GlowWorld world : worlds.getWorlds()) {
            if (uid.equals(world.getUID())) {
                return world;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<World> getWorlds() {
        // Shenanigans needed to cast List<GlowWorld> to List<World>
        return (List) worlds.getWorlds();
    }

    /**
     * Gets the default ChunkGenerator for the given environment and type.
     * @return The ChunkGenerator.
     */
    private ChunkGenerator getGenerator(String name, Environment environment, WorldType type) {
        // find generator based on configuration
        ConfigurationSection worlds = config.getWorlds();
        if (worlds != null) {
            String genName = worlds.getString(name + ".generator", null);
            ChunkGenerator generator = WorldCreator.getGeneratorForName(name, genName, getConsoleSender());
            if (generator != null) {
                return generator;
            }
        }

        // find generator based on environment and world type
        if (environment == Environment.NETHER) {
            return new net.glowstone.generator.UndergroundGenerator();
        } else if (environment == Environment.THE_END) {
            return new net.glowstone.generator.CakeTownGenerator();
        } else {
            return new net.glowstone.generator.SurfaceGenerator();
        }
    }

    @Override
    public GlowWorld createWorld(WorldCreator creator) {
        GlowWorld world = getWorld(creator.name());
        if (world != null) {
            return world;
        }

        if (creator.generator() == null) {
            creator.generator(getGenerator(creator.name(), creator.environment(), creator.type()));
        }

        world = new GlowWorld(this, creator);
        return worlds.addWorld(world);
    }

    @Override
    public boolean unloadWorld(String name, boolean save) {
        GlowWorld world = getWorld(name);
        return world != null && unloadWorld(world, save);
    }

    @Override
    public boolean unloadWorld(World bWorld, boolean save) {
        if (!(bWorld instanceof GlowWorld)) {
            return false;
        }
        GlowWorld world = (GlowWorld) bWorld;
        if (save) {
            world.setAutoSave(false);
            world.save(false);
        }
        if (worlds.removeWorld(world)) {
            world.unload();
            EventFactory.onWorldUnload(world);
            return true;
        }
        return false;
    }

    @Override
    public GlowMapView getMap(short id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GlowMapView createMap(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory and crafting

    @Override
    public List<Recipe> getRecipesFor(ItemStack result) {
        return craftingManager.getRecipesFor(result);
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        return craftingManager.iterator();
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        return craftingManager.addRecipe(recipe);
    }

    @Override
    public void clearRecipes() {
        craftingManager.clearRecipes();
    }

    @Override
    public void resetRecipes() {
        craftingManager.resetRecipes();
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return new GlowInventory(owner, type);
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size) {
        return new GlowInventory(owner, InventoryType.CHEST, size);
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size, String title) {
        return new GlowInventory(owner, InventoryType.CHEST, size, title);
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return new GlowInventory(owner, type, type.getDefaultSize(), title);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Server icons

    @Override
    public GlowServerIcon getServerIcon() {
        return defaultIcon;
    }

    @Override
    public CachedServerIcon loadServerIcon(File file) throws Exception {
        return new GlowServerIcon(file);
    }

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage image) throws Exception {
        return new GlowServerIcon(image);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);
        for (Player player : getOnlinePlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        HashSet<String> result = new HashSet<>();
        for (Player player : getOnlinePlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration with special handling

    @Override
    public GameMode getDefaultGameMode() {
        return defaultGameMode;
    }

    @Override
    public void setDefaultGameMode(GameMode mode) {
        defaultGameMode = mode;
    }

    @Override
    public int getSpawnRadius() {
        return spawnRadius;
    }

    @Override
    public void setSpawnRadius(int value) {
        spawnRadius = value;
    }

    @Override
    public boolean hasWhitelist() {
        return whitelistEnabled;
    }

    @Override
    public void setWhitelist(boolean enabled) {
        whitelistEnabled = enabled;
    }

    @Override
    public Warning.WarningState getWarningState() {
        return warnState;
    }

    @Override
    public void setIdleTimeout(int timeout) {
        idleTimeout = timeout;
    }

    @Override
    public int getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void configureDbConfig(com.avaje.ebean.config.ServerConfig dbConfig) {
        com.avaje.ebean.config.DataSourceConfig ds = new com.avaje.ebean.config.DataSourceConfig();
        ds.setDriver(config.getString(ServerConfig.Key.DB_DRIVER));
        ds.setUrl(config.getString(ServerConfig.Key.DB_URL));
        ds.setUsername(config.getString(ServerConfig.Key.DB_USERNAME));
        ds.setPassword(config.getString(ServerConfig.Key.DB_PASSWORD));
        ds.setIsolationLevel(com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation.getLevel(config.getString(ServerConfig.Key.DB_ISOLATION)));

        if (ds.getDriver().contains("sqlite")) {
            dbConfig.setDatabasePlatform(new com.avaje.ebean.config.dbplatform.SQLitePlatform());
            dbConfig.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
        }

        dbConfig.setDataSourceConfig(ds);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration

    @Override
    public String getIp() {
        return config.getString(ServerConfig.Key.SERVER_IP);
    }

    @Override
    public int getPort() {
        return config.getInt(ServerConfig.Key.SERVER_PORT);
    }

    @Override
    public String getServerName() {
        return config.getString(ServerConfig.Key.SERVER_NAME);
    }

    @Override
    public String getServerId() {
        return Integer.toHexString(getServerName().hashCode());
    }

    @Override
    public int getMaxPlayers() {
        return config.getInt(ServerConfig.Key.MAX_PLAYERS);
    }

    @Override
    public String getUpdateFolder() {
        return config.getString(ServerConfig.Key.UPDATE_FOLDER);
    }

    @Override
    public File getUpdateFolderFile() {
        return new File(getUpdateFolder());
    }

    @Override
    public boolean getOnlineMode() {
        return config.getBoolean(ServerConfig.Key.ONLINE_MODE);
    }

    @Override
    public boolean getAllowNether() {
        return config.getBoolean(ServerConfig.Key.ALLOW_NETHER);
    }

    @Override
    public boolean getAllowEnd() {
        return config.getBoolean(ServerConfig.Key.ALLOW_END);
    }

    @Override
    public int getViewDistance() {
        return config.getInt(ServerConfig.Key.VIEW_DISTANCE);
    }

    @Override
    public String getMotd() {
        return config.getString(ServerConfig.Key.MOTD);
    }

    @Override
    public File getWorldContainer() {
        return new File(config.getString(ServerConfig.Key.WORLD_FOLDER));
    }

    @Override
    public String getWorldType() {
        return config.getString(ServerConfig.Key.LEVEL_TYPE);
    }

    @Override
    public boolean getGenerateStructures() {
        return config.getBoolean(ServerConfig.Key.GENERATE_STRUCTURES);
    }

    @Override
    public long getConnectionThrottle() {
        return config.getInt(ServerConfig.Key.CONNECTION_THROTTLE);
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        return config.getInt(ServerConfig.Key.ANIMAL_TICKS);
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        return config.getInt(ServerConfig.Key.MONSTER_TICKS);
    }

    @Override
    public boolean isHardcore() {
        return config.getBoolean(ServerConfig.Key.HARDCORE);
    }

    @Override
    public boolean useExactLoginLocation() {
        return config.getBoolean(ServerConfig.Key.EXACT_LOGIN_LOCATION);
    }

    @Override
    public int getMonsterSpawnLimit() {
        return config.getInt(ServerConfig.Key.MONSTER_LIMIT);
    }

    @Override
    public int getAnimalSpawnLimit() {
        return config.getInt(ServerConfig.Key.ANIMAL_LIMIT);
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return config.getInt(ServerConfig.Key.WATER_ANIMAL_LIMIT);
    }

    @Override
    public int getAmbientSpawnLimit() {
        return config.getInt(ServerConfig.Key.AMBIENT_LIMIT);
    }

    @Override
    public String getShutdownMessage() {
        return config.getString(ServerConfig.Key.SHUTDOWN_MESSAGE);
    }

    @Override
    public boolean getAllowFlight() {
        return config.getBoolean(ServerConfig.Key.ALLOW_FLIGHT);
    }
}
