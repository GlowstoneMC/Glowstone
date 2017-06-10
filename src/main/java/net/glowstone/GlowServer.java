package net.glowstone;

import com.flowpowered.network.Message;
import com.google.common.base.Preconditions;
import com.jogamp.opencl.CLDevice;
import com.jogamp.opencl.CLPlatform;
import io.netty.channel.epoll.Epoll;
import lombok.Getter;
import net.glowstone.block.BuiltinMaterialValueManager;
import net.glowstone.block.MaterialValueManager;
import net.glowstone.block.state.GlowDispenser;
import net.glowstone.boss.BossBarManager;
import net.glowstone.boss.GlowBossBar;
import net.glowstone.client.GlowClient;
import net.glowstone.command.glowstone.ColorCommand;
import net.glowstone.command.glowstone.GlowstoneCommand;
import net.glowstone.command.minecraft.*;
import net.glowstone.constants.GlowEnchantment;
import net.glowstone.constants.GlowPotionEffect;
import net.glowstone.entity.EntityIdManager;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.profile.PlayerProfile;
import net.glowstone.generator.*;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.inventory.crafting.CraftingManager;
import net.glowstone.io.PlayerDataService;
import net.glowstone.io.PlayerStatisticIoService;
import net.glowstone.io.ScoreboardIoService;
import net.glowstone.map.GlowMapView;
import net.glowstone.net.GameServer;
import net.glowstone.net.SessionRegistry;
import net.glowstone.net.query.QueryServer;
import net.glowstone.net.rcon.RconServer;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.scheduler.WorldScheduler;
import net.glowstone.scoreboard.GlowScoreboardManager;
import net.glowstone.util.*;
import net.glowstone.util.bans.GlowBanList;
import net.glowstone.util.bans.UuidListFile;
import net.glowstone.util.config.ServerConfig;
import net.glowstone.util.config.ServerConfig.Key;
import net.glowstone.util.config.WorldConfig;
import net.glowstone.util.loot.LootingManager;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.BanList.Type;
import org.bukkit.Warning.WarningState;
import org.bukkit.World.Environment;
import org.bukkit.advancement.Advancement;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.util.CachedServerIcon;
import org.bukkit.util.permissions.DefaultPermissions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The core class of the Glowstone server.
 *
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
    public static final String GAME_VERSION = "1.12";
    /**
     * The protocol version supported by the server.
     */
    public static final int PROTOCOL_VERSION = 335;
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
    private final SimplePluginManager pluginManager = new SimplePluginManager(this, commandMap);
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
    private final GlowScoreboardManager scoreboardManager = new GlowScoreboardManager(this);
    /**
     * The crafting manager for this server.
     */
    private final CraftingManager craftingManager = new CraftingManager();
    /**
     * The configuration for the server.
     */
    private final ServerConfig config;
    /**
     * The world config for extended world customization.
     */
    private static WorldConfig worldConfig;
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
     * The EntityIdManager for this server.
     */
    private final EntityIdManager entityIdManager = new EntityIdManager();
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
     * A RSA key pair used for encryption and authentication
     */
    private final KeyPair keyPair = SecurityUtils.generateKeyPair();
    /**
     * The network server used for network communication
     */
    @Getter
    private GameServer networkServer;
    /**
     * A set of all online players.
     */
    private final Set<GlowPlayer> onlinePlayers = new HashSet<>();
    /**
     * A view of all online players.
     */
    private final Set<GlowPlayer> onlineView = Collections.unmodifiableSet(onlinePlayers);
    /**
     * The plugin type detector of thi server.
     */
    private GlowPluginTypeDetector pluginTypeDetector;
    /**
     * The server's default game mode
     */
    private GameMode defaultGameMode = GameMode.CREATIVE;

    /**
     * The setting for verbose deprecation warnings.
     */
    private WarningState warnState = WarningState.DEFAULT;

    /**
     * Whether the server is shutting down
     */
    private boolean isShuttingDown;

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
     * The query server for this server, or null if disabled.
     */
    private QueryServer queryServer;
    /**
     * The Rcon server for this server, or null if disabled.
     */
    private RconServer rconServer;
    /**
     * The default icon, usually blank, used for the server list.
     */
    private GlowServerIcon defaultIcon;
    /**
     * The server port.
     */
    private int port;
    /**
     * The server ip.
     */
    private String ip;
    /**
     * The {@link MaterialValueManager} of this server.
     */
    private MaterialValueManager materialValueManager;
    /**
     * The {@link BossBarManager} of this server.
     */
    private BossBarManager bossBarManager;
    /*
     * Default root permissions
     */
    public Permission permissionRoot, permissionCommand;

    /**
     * Creates a new server.
     *
     * @param config This server's config.
     */
    public GlowServer(ServerConfig config) {
        materialValueManager = new BuiltinMaterialValueManager();
        bossBarManager = new BossBarManager(this);

        this.config = config;
        // stuff based on selected config directory
        opsList = new UuidListFile(config.getFile("ops.json"));
        whitelist = new UuidListFile(config.getFile("whitelist.json"));
        nameBans = new GlowBanList(this, Type.NAME);
        ipBans = new GlowBanList(this, Type.IP);

        Bukkit.setServer(this);
        loadConfig();
    }

    /**
     * The client instance backed by this server.
     */
    public static GlowClient client;

    /**
     * Creates a new server on TCP port 25565 and starts listening for
     * connections.
     *
     * @param args The command-line arguments.
     */
    public static void main(String... args) {
        try {
            Thread.currentThread().setContextClassLoader(LibraryManager.getLibraryClassLoader());
            GlowServer server = createFromArguments(args);

            // we don't want to run a server when called with --version
            if (server == null) {
                return;
            }

            server.run();
        } catch (SecurityException e) {
            logger.log(Level.WARNING, "Error loading classpath!", e);
        } catch (Throwable t) {
            // general server startup crash
            logger.log(Level.SEVERE, "Error during server startup.", t);
            System.exit(1);
        }
    }

    public static GlowServer createFromArguments(String... args) {
        ServerConfig config = parseArguments(args);

        // we don't want to create a server when called with --version
        if (config == null) {
            return null;
        }

        ConfigurationSerialization.registerClass(GlowOfflinePlayer.class);
        GlowPotionEffect.register();
        GlowEnchantment.register();
        GlowDispenser.register();

        return new GlowServer(config);
    }

    private static ServerConfig parseArguments(String... args) {
        Map<Key, Object> parameters = new EnumMap<>(Key.class);
        String configDirName = "config";
        String configFileName = "glowstone.yml";

        // Calculate acceptable parameters
        for (int i = 0; i < args.length; i++) {
            String opt = args[i];

            if (opt.isEmpty() || opt.charAt(0) != '-') {
                System.err.println("Ignored invalid option: " + opt);
                continue;
            }

            // Help and version
            if ("--help".equals(opt) || "-h".equals(opt) || "-?".equals(opt)) {
                System.out.println("Available command-line options:");
                System.out.println("  --help, -h, -?                 Shows this help message and exits.");
                System.out.println("  --version, -v                  Shows version information and exits.");
                System.out.println("  --configdir <directory>        Sets the configuration directory.");
                System.out.println("  --configfile <file>            Sets the configuration file.");
                System.out.println("  --port, -p <port>              Sets the server listening port.");
                System.out.println("  --host, -H <ip | hostname>     Sets the server listening address.");
                System.out.println("  --onlinemode, -o <onlinemode>  Sets the server's online-mode.");
                System.out.println("  --jline <true/false>           Enables or disables JLine console.");
                System.out.println("  --plugins-dir, -P <directory>  Sets the plugin directory to use.");
                System.out.println("  --worlds-dir, -W <directory>   Sets the world directory to use.");
                System.out.println("  --update-dir, -U <directory>   Sets the plugin update folder to use.");
                System.out.println("  --max-players, -M <director>   Sets the maximum amount of players.");
                System.out.println("  --world-name, -N <name>        Sets the main world name.");
                System.out.println("  --log-pattern, -L <pattern>    Sets the log file pattern (%D for date).");
                return null;
            } else if ("--version".equals(opt) || "-v".equals(opt)) {
                System.out.println("Glowstone version: " + GlowServer.class.getPackage().getImplementationVersion());
                System.out.println("Bukkit version:    " + GlowServer.class.getPackage().getSpecificationVersion());
                System.out.println("Minecraft version: " + GAME_VERSION + " protocol " + PROTOCOL_VERSION);
                return null;
            }

            // Below this point, options require parameters
            if (i == args.length - 1) {
                System.err.println("Ignored option specified without value: " + opt);
                continue;
            }

            switch (opt) {
                case "--configdir":
                    configDirName = args[++i];
                    break;
                case "--configfile":
                    configFileName = args[++i];
                    break;
                case "--port":
                case "-p":
                    parameters.put(Key.SERVER_PORT, Integer.valueOf(args[++i]));
                    break;
                case "--host":
                case "-H":
                    parameters.put(Key.SERVER_IP, args[++i]);
                    break;
                case "--onlinemode":
                case "-o":
                    parameters.put(Key.ONLINE_MODE, Boolean.valueOf(args[++i]));
                    break;
                case "--jline":
                    parameters.put(Key.USE_JLINE, Boolean.valueOf(args[++i]));
                    break;
                case "--plugins-dir":
                case "-P":
                    parameters.put(Key.PLUGIN_FOLDER, args[++i]);
                    break;
                case "--worlds-dir":
                case "-W":
                    parameters.put(Key.WORLD_FOLDER, args[++i]);
                    break;
                case "--update-dir":
                case "-U":
                    parameters.put(Key.UPDATE_FOLDER, args[++i]);
                    break;
                case "--max-players":
                case "-M":
                    parameters.put(Key.MAX_PLAYERS, Integer.valueOf(args[++i]));
                    break;
                case "--world-name":
                case "-N":
                    parameters.put(Key.LEVEL_NAME, args[++i]);
                    break;
                case "--log-pattern":
                case "-L":
                    parameters.put(Key.LOG_FILE, args[++i]);
                    break;
                default:
                    System.err.println("Ignored invalid option: " + opt);
            }
        }

        File configDir = new File(configDirName);
        worldConfig = new WorldConfig(configDir, new File(configDir, "worlds.yml"));
        File configFile = new File(configDir, configFileName);

        return new ServerConfig(configDir, configFile, parameters);
    }

    public void run() {
        start();
        bind();
        logger.info("Ready for connections.");

        if (doMetrics()) {
            new Metrics(this, config.getString(Key.METRICS_UUID), false);
        }

        if (config.getBoolean(Key.RUN_CLIENT)) {
            client = new GlowClient(this);
            client.run();
        }
    }

    /**
     * Whether OpenCL is to be used by the server on this run.
     */
    private boolean isCLApplicable = true;

    /**
     * Starts this server.
     */
    public void start() {
        // Determine console mode and start reading input
        consoleManager.startConsole(config.getBoolean(Key.USE_JLINE));
        consoleManager.startFile(config.getString(Key.LOG_FILE));

        if (getProxySupport()) {
            if (getOnlineMode()) {
                logger.warning("Proxy support is enabled, but online mode is enabled.");
            } else {
                logger.info("Proxy support is enabled.");
            }
        } else if (!getOnlineMode()) {
            logger.warning("The server is running in offline mode! Only do this if you know what you're doing.");
        }

        int openCLMajor = 1;
        int openCLMinor = 2;

        if (doesUseGPGPU()) {
            int maxGpuFlops = 0;
            int maxIntelFlops = 0;
            int maxCpuFlops = 0;
            CLPlatform bestPlatform = null;
            CLPlatform bestIntelPlatform = null;
            CLPlatform bestCpuPlatform = null;
            // gets the max flops device across platforms on the computer
            for (CLPlatform platform : CLPlatform.listCLPlatforms()) {
                if (platform.isAtLeast(openCLMajor, openCLMinor) && platform.isExtensionAvailable("cl_khr_fp64")) {
                    for (CLDevice device : platform.listCLDevices()) {
                        if (device.getType() == CLDevice.Type.GPU) {
                            int flops = device.getMaxComputeUnits() * device.getMaxClockFrequency();
                            logger.info("Found " + device + " with " + flops + " flops");
                            if (device.getVendor().contains("Intel")) {
                                if (flops > maxIntelFlops) {
                                    maxIntelFlops = flops;
                                    logger.info("Device is best platform so far, on " + platform);
                                    bestIntelPlatform = platform;
                                } else if (flops == maxIntelFlops) {
                                    if (bestIntelPlatform != null && bestIntelPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                        maxIntelFlops = flops;
                                        logger.info("Device tied for flops, but had higher version on " + platform);
                                        bestIntelPlatform = platform;
                                    }
                                }
                            } else {
                                if (flops > maxGpuFlops) {
                                    maxGpuFlops = flops;
                                    logger.info("Device is best platform so far, on " + platform);
                                    bestPlatform = platform;
                                } else if (flops == maxGpuFlops) {
                                    if (bestPlatform != null && bestPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                        maxGpuFlops = flops;
                                        logger.info("Device tied for flops, but had higher version on " + platform);
                                        bestPlatform = platform;
                                    }
                                }
                            }
                        } else {
                            int flops = device.getMaxComputeUnits() * device.getMaxClockFrequency();
                            logger.info("Found " + device + " with " + flops + " flops");
                            if (flops > maxCpuFlops) {
                                maxCpuFlops = flops;
                                logger.info("Device is best platform so far, on " + platform);
                                bestCpuPlatform = platform;
                            } else if (flops == maxCpuFlops) {
                                if (bestCpuPlatform != null && bestCpuPlatform.getVersion().compareTo(platform.getVersion()) < 0) {
                                    maxCpuFlops = flops;
                                    logger.info("Device tied for flops, but had higher version on " + platform);
                                    bestCpuPlatform = platform;
                                }
                            }
                        }
                    }
                }
            }

            if (config.getBoolean(Key.GPGPU_ANY_DEVICE)) {
                if (maxGpuFlops - maxIntelFlops < 0 && maxCpuFlops - maxIntelFlops <= 0) {
                    bestPlatform = bestIntelPlatform;
                } else if (maxGpuFlops - maxCpuFlops < 0 && maxIntelFlops - maxCpuFlops < 0) {
                    bestPlatform = bestCpuPlatform;
                }
            } else {
                if (maxGpuFlops == 0) {
                    if (maxIntelFlops == 0) {
                        logger.info("No Intel graphics found, best platform is the best CPU platform we could find...");
                        bestPlatform = bestCpuPlatform;
                    } else {
                        logger.info("No dGPU found, best platform is the best Intel graphics we could find...");
                        bestPlatform = bestIntelPlatform;
                    }
                }
            }

            if (bestPlatform == null) {
                isCLApplicable = false;
                logger.info("Your system does not meet the OpenCL requirements for Glowstone. See if driver updates are available.");
                logger.info("Required version: " + openCLMajor + '.' + openCLMinor);
                logger.info("Required extensions: [ cl_khr_fp64 ]");
            } else {
                OpenCL.initContext(bestPlatform);
            }
        }

        // Load player lists
        opsList.load();
        whitelist.load();
        nameBans.load();
        ipBans.load();
        setPort(config.getInt(Key.SERVER_PORT));
        setIp(config.getString(Key.SERVER_IP));

        try {
            LootingManager.load();
        } catch (Exception e) {
            GlowServer.logger.severe("Failed to load looting manager: ");
            e.printStackTrace();
        }

        // Start loading plugins
        new LibraryManager().run();
        loadPlugins();
        enablePlugins(PluginLoadOrder.STARTUP);

        // Create worlds
        String name = config.getString(Key.LEVEL_NAME);
        String seedString = config.getString(Key.LEVEL_SEED);
        boolean structs = getGenerateStructures();
        WorldType type = WorldType.getByName(getWorldType());
        if (type == null) {
            type = WorldType.NORMAL;
        }

        long seed = new Random().nextLong();
        if (!seedString.isEmpty()) {
            try {
                long parsed = Long.parseLong(seedString);
                if (parsed != 0) {
                    seed = parsed;
                }
            } catch (NumberFormatException ex) {
                seed = seedString.hashCode();
            }
        }

        createWorld(WorldCreator.name(name).environment(Environment.NORMAL).seed(seed).type(type).generateStructures(structs));
        if (getAllowNether()) {
            checkTransfer(name, "_nether", Environment.NETHER);
            createWorld(WorldCreator.name(name + "_nether").environment(Environment.NETHER).seed(seed).type(type).generateStructures(structs));
        }
        if (getAllowEnd()) {
            checkTransfer(name, "_the_end", Environment.THE_END);
            createWorld(WorldCreator.name(name + "_the_end").environment(Environment.THE_END).seed(seed).type(type).generateStructures(structs));
        }

        // Finish loading plugins
        enablePlugins(PluginLoadOrder.POSTWORLD);
        commandMap.registerServerAliases();
        scheduler.start();
    }

    private void checkTransfer(String name, String suffix, Environment environment) {
        // todo: import things like per-dimension villages.dat when those are implemented
        Path srcPath = new File(new File(getWorldContainer(), name), "DIM" + environment.getId()).toPath();
        Path destPath = new File(getWorldContainer(), name + suffix).toPath();
        if (Files.exists(srcPath) && !Files.exists(destPath)) {
            logger.info("Importing " + destPath + " from " + srcPath);
            try {
                Files.walkFileTree(srcPath, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        Path target = destPath.resolve(srcPath.relativize(dir));
                        if (!Files.exists(target)) {
                            Files.createDirectory(target);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.copy(file, destPath.resolve(srcPath.relativize(file)), StandardCopyOption.COPY_ATTRIBUTES);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        logger.warning("Importing file " + srcPath.relativize(file) + " + failed: " + exc);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
                Files.copy(srcPath.resolve("../level.dat"), destPath.resolve("level.dat"));
            } catch (IOException e) {
                logger.log(Level.WARNING, "Import of " + srcPath + " failed", e);
            }
        }
    }

    private void bind() {
        if (Epoll.isAvailable()) {
            logger.info("Native epoll transport is enabled.");
        }

        CountDownLatch latch = new CountDownLatch(3);

        networkServer = new GameServer(this, latch);
        networkServer.bind(getBindAddress(Key.SERVER_PORT));

        if (config.getBoolean(Key.QUERY_ENABLED)) {
            queryServer = new QueryServer(this, latch, config.getBoolean(Key.QUERY_PLUGINS));
            queryServer.bind(getBindAddress(Key.QUERY_PORT));
        } else {
            latch.countDown();
        }

        if (config.getBoolean(Key.RCON_ENABLED)) {
            rconServer = new RconServer(this, latch, config.getString(Key.RCON_PASSWORD));
            rconServer.bind(getBindAddress(Key.RCON_PORT));
        } else {
            latch.countDown();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Bind interrupted! ", e);
            System.exit(1);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get the SocketAddress to bind to for a specified service.
     *
     * @param portKey The configuration key for the port to use.
     * @return The SocketAddress
     */
    private InetSocketAddress getBindAddress(Key portKey) {
        String ip = config.getString(Key.SERVER_IP);
        int port = config.getInt(portKey);
        if (ip.isEmpty()) {
            return new InetSocketAddress(port);
        } else {
            return new InetSocketAddress(ip, port);
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
        for (GlowPlayer player : new ArrayList<>(getRawOnlinePlayers())) {
            player.kickPlayer(getShutdownMessage(), false);
        }

        // Stop the network servers - starts the shutdown process
        // It may take a second or two for Netty to totally clean up
        if (networkServer != null) {
            networkServer.shutdown();
        }
        if (queryServer != null) {
            queryServer.shutdown();
        }
        if (rconServer != null) {
            rconServer.shutdown();
        }

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
        worldConfig.load();

        // modifiable values
        spawnRadius = config.getInt(Key.SPAWN_RADIUS);
        whitelistEnabled = config.getBoolean(Key.WHITELIST);
        idleTimeout = config.getInt(Key.PLAYER_IDLE_TIMEOUT);
        craftingManager.initialize();

        // special handling
        warnState = WarningState.value(config.getString(Key.WARNING_STATE));
        try {
            defaultGameMode = GameMode.valueOf(config.getString(Key.GAMEMODE));
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
        // glowstone commands
        commandMap.register("glowstone", new ColorCommand());
        commandMap.register("glowstone", new GlowstoneCommand());
        // vanilla commands
        commandMap.register("minecraft", new TellrawCommand());
        commandMap.register("minecraft", new TitleCommand());
        commandMap.register("minecraft", new TeleportCommand());
        commandMap.register("minecraft", new SummonCommand());
        commandMap.register("minecraft", new WorldBorderCommand());
        commandMap.register("minecraft", new SayCommand());
        commandMap.register("minecraft", new StopCommand());
        commandMap.register("minecraft", new OpCommand());
        commandMap.register("minecraft", new GameModeCommand());
        commandMap.register("minecraft", new FunctionCommand());
        commandMap.register("minecraft", new DeopCommand());
        commandMap.register("minecraft", new KickCommand());
        commandMap.register("minecraft", new GameRuleCommand());
        commandMap.register("minecraft", new TellCommand());
        commandMap.register("minecraft", new ListCommand());
        commandMap.register("minecraft", new BanCommand());
        commandMap.register("minecraft", new BanIpCommand());
        commandMap.register("minecraft", new GiveCommand());

        File folder = new File(config.getString(Key.PLUGIN_FOLDER));
        if (!folder.isDirectory() && !folder.mkdirs()) {
            logger.log(Level.SEVERE, "Could not create plugins directory: " + folder);
        }

        // detect plugin types
        pluginTypeDetector = new GlowPluginTypeDetector(folder);
        pluginTypeDetector.scan();

        // clear plugins and prepare to load (Bukkit)
        pluginManager.clearPlugins();
        pluginManager.registerInterface(JavaPluginLoader.class);
        Plugin[] plugins = pluginManager.loadPlugins(folder.getPath(), pluginTypeDetector.bukkitPlugins.toArray(new File[pluginTypeDetector.bukkitPlugins.size()]));

        // call onLoad methods
        for (Plugin plugin : plugins) {
            try {
                plugin.onLoad();
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error loading " + plugin.getDescription().getFullName(), ex);
            }
        }

        if (!pluginTypeDetector.spongePlugins.isEmpty()) {
            boolean hasSponge = false;
            for (Plugin plugin : plugins) {
                if (plugin.getName().equals("Bukkit2Sponge")) {
                    hasSponge = true; // TODO: better detection method, plugin description file annotation APIs?
                    break;
                }
            }

            boolean spongeOnlyPlugins = false;
            for (File spongePlugin : pluginTypeDetector.spongePlugins) {
                if (!pluginTypeDetector.bukkitPlugins.contains(spongePlugin)) {
                    spongeOnlyPlugins = true;
                }
            }

            if (!hasSponge && spongeOnlyPlugins) {
                logger.log(Level.WARNING, "SpongeAPI plugins found, but no Sponge bridge present! They will be ignored.");
                for (File file : getSpongePlugins()) {
                    logger.log(Level.WARNING, "Ignored SpongeAPI plugin: " + file.getPath());
                }
                logger.log(Level.WARNING, "Suggestion: install https://github.com/GlowstoneMC/Bukkit2Sponge to load these plugins");
            }
        }

        if (!pluginTypeDetector.canaryPlugins.isEmpty() ||
                !pluginTypeDetector.forgefPlugins.isEmpty() ||
                !pluginTypeDetector.forgenPlugins.isEmpty() ||
                !pluginTypeDetector.unrecognizedPlugins.isEmpty()) {
            logger.log(Level.WARNING, "Unsupported plugin types found, will be ignored:");

            for (File file : pluginTypeDetector.canaryPlugins)
                logger.log(Level.WARNING, "Canary plugin not supported: " + file.getPath());

            for (File file : pluginTypeDetector.forgefPlugins)
                logger.log(Level.WARNING, "Forge plugin not supported: " + file.getPath());

            for (File file : pluginTypeDetector.forgenPlugins)
                logger.log(Level.WARNING, "Forge plugin not supported: " + file.getPath());

            for (File file : pluginTypeDetector.unrecognizedPlugins)
                logger.log(Level.WARNING, "Unrecognized plugin not supported: " + file.getPath());
        }

    }

    // API for Bukkit2Sponge
    public List<File> getSpongePlugins() {
        return pluginTypeDetector.spongePlugins;
    }

    /**
     * Enable all plugins of the given load order type.
     *
     * @param type The type of plugin to enable.
     */
    private void enablePlugins(PluginLoadOrder type) {
        if (type == PluginLoadOrder.STARTUP) {
            helpMap.clear();
            helpMap.loadConfig(config.getConfigFile(Key.HELP_FILE));
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
            // Default permissions
            this.permissionRoot = DefaultPermissions.registerPermission("minecraft", "Gives the user the ability to use all Minecraft utilities and commands");
            this.permissionCommand = DefaultPermissions.registerPermission("minecraft.command", "Gives the user the ability to use all Minecraft commands", permissionRoot);
            DefaultPermissions.registerPermission("minecraft.command.tell", "Allows the user to send a private message", PermissionDefault.TRUE, permissionCommand);
            permissionCommand.recalculatePermissibles();
            permissionRoot.recalculatePermissibles();
            helpMap.initializeCommands();
            helpMap.amendTopics(config.getConfigFile(Key.HELP_FILE));

            // load permissions.yml
            ConfigurationSection permConfig = config.getConfigFile(Key.PERMISSIONS_FILE);

            Map<String, Map<String, Object>> data = new HashMap<>();

            permConfig.getValues(false).forEach((key, value) -> data.put(key, ((MemorySection) value).getValues(false)));

            List<Permission> perms = Permission.loadPermissions(data, "Permission node '%s' in permissions config is invalid", PermissionDefault.OP);

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
    public void reloadData() {

    }

    @Override
    public String toString() {
        return "GlowServer{name=" + getName() + ",version=" + getVersion() + ",minecraftVersion=" + GAME_VERSION + "}";
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to internals

    /**
     * Gets the command map.
     *
     * @return The {@link SimpleCommandMap}.
     */
    public SimpleCommandMap getCommandMap() {
        return commandMap;
    }

    @Override
    public Advancement getAdvancement(NamespacedKey namespacedKey) {
        return null;
    }

    @Override
    public Iterator<Advancement> advancementIterator() {
        return null;
    }

    /**
     * Gets the session registry.
     *
     * @return The {@link SessionRegistry}.
     */
    public SessionRegistry getSessionRegistry() {
        return sessions;
    }

    /**
     * Gets the entity id manager.
     *
     * @return The {@link EntityIdManager}.
     */
    public EntityIdManager getEntityIdManager() {
        return entityIdManager;
    }

    /**
     * Returns the list of operators on this server.
     *
     * @return A file containing a list of UUIDs for this server's operators.
     */
    public UuidListFile getOpsList() {
        return opsList;
    }

    /**
     * Returns the list of whitelisted players on this server.
     *
     * @return A file containing a list of UUIDs for this server's whitelisted players.
     */
    public UuidListFile getWhitelist() {
        return whitelist;
    }

    @Override
    public void setWhitelist(boolean enabled) {
        whitelistEnabled = enabled;
        config.set(Key.WHITELIST, whitelistEnabled);
        config.save();
    }

    /**
     * Returns the folder where configuration files are stored
     *
     * @return The server's configuration folder.
     */
    public File getConfigDir() {
        return config.getDirectory();
    }

    /**
     * Return the crafting manager.
     *
     * @return The server's crafting manager.
     */
    public CraftingManager getCraftingManager() {
        return craftingManager;
    }

    /**
     * The key pair generated at server start up
     *
     * @return The key pair generated at server start up
     */
    public KeyPair getKeyPair() {
        return keyPair;
    }

    /**
     * Returns the player data service attached to the first world.
     *
     * @return The server's player data service.
     */
    public PlayerDataService getPlayerDataService() {
        return worlds.getWorlds().get(0).getStorage().getPlayerDataService();
    }

    /**
     * Returns the scoreboard I/O service attached to the first world.
     *
     * @return The server's scoreboard I/O service
     */
    public ScoreboardIoService getScoreboardIoService() {
        return worlds.getWorlds().get(0).getStorage().getScoreboardIoService();
    }

    /**
     * Returns the player statitics I/O service attached to the first world.
     *
     * @return the server's statistics I/O service
     */
    public PlayerStatisticIoService getPlayerStatisticIoService() {
        return worlds.getWorlds().get(0).getStorage().getPlayerStatisticIoService();
    }

    /**
     * Get the threshold to use for network compression defined in the config.
     *
     * @return The compression threshold, or -1 for no compression.
     */
    public int getCompressionThreshold() {
        return config.getInt(Key.COMPRESSION_THRESHOLD);
    }

    /**
     * Get the default game difficulty defined in the config.
     *
     * @return The default difficulty.
     */
    public Difficulty getDifficulty() {
        try {
            return Difficulty.valueOf(config.getString(Key.DIFFICULTY));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Difficulty.NORMAL;
        }
    }

    /**
     * Get whether worlds should keep their spawns loaded by default.
     *
     * @return Whether to keep spawns loaded by default.
     */
    public boolean keepSpawnLoaded() {
        return config.getBoolean(Key.PERSIST_SPAWN);
    }

    /**
     * Get whether to populate chunks when they are anchored.
     *
     * @return Whether to populate chunks when they are anchored.
     */
    public boolean populateAnchoredChunks() {
        return config.getBoolean(Key.POPULATE_ANCHORED_CHUNKS);
    }

    /**
     * Get whether parsing of data provided by a proxy is enabled.
     *
     * @return True if a proxy is providing data to use.
     */
    public boolean getProxySupport() {
        return config.getBoolean(Key.PROXY_SUPPORT);
    }

    /**
     * Get whether to use color codes in Rcon responses.
     *
     * @return True if color codes will be present in Rcon responses
     */
    public boolean useRconColors() {
        return config.getBoolean(Key.RCON_COLORS);
    }

    public MaterialValueManager getMaterialValueManager() {
        return materialValueManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    /**
     * Get the resource pack url for this server, or {@code null} if not set.
     *
     * @return The url of the resource pack to use, or {@code null}
     */
    public String getResourcePackURL() {
        return config.getString(Key.RESOURCE_PACK);
    }

    /**
     * Get the resource pack hash for this server, or the empty string if not set.
     *
     * @return The hash of the resource pack, or the empty string
     */
    public String getResourcePackHash() {
        return config.getString(Key.RESOURCE_PACK_HASH);
    }

    /**
     * Get whether achievements should be announced.
     *
     * @return True if achievements should be announced in chat.
     */
    public boolean getAnnounceAchievements() {
        return config.getBoolean(Key.ANNOUNCE_ACHIEVEMENTS);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Static server properties

    /**
     * Sets a player as being online internally.
     *
     * @param player player to set online/offline
     * @param online whether the player is online or offline
     */
    public void setPlayerOnline(GlowPlayer player, boolean online) {
        Preconditions.checkNotNull(player);
        if (online) {
            onlinePlayers.add(player);
        } else {
            onlinePlayers.remove(player);
        }
    }

    @Override
    public String getName() {
        return getClass().getPackage().getImplementationTitle();
    }

    @Override
    public String getVersion() {
        return GlowServer.class.getPackage().getImplementationVersion() + "-MC" + GAME_VERSION;
    }

    @Override
    public String getBukkitVersion() {
        return GlowServer.class.getPackage().getSpecificationVersion();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Access to Bukkit API

    @Override
    public boolean isPrimaryThread() {
        return scheduler.isPrimaryThread();
    }

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
    public GlowScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    @Override
    @Deprecated
    public UnsafeValues getUnsafe() {
        return unsafeAccess;
    }

    private Spigot spigot = new Spigot() {
        public org.bukkit.configuration.file.YamlConfiguration getConfig() {
            return config.getConfig();
        }
    };

    @Override
    public Spigot spigot() {
        return spigot;
    }

    public static WorldConfig getWorldConfig() {
        return worldConfig;
    }

    @Override
    public void reloadPermissions() {

    }

    @Override
    public Entity getEntity(UUID uuid) {
        for (World world : getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getUniqueId().equals(uuid)) {
                    return entity;
                }
            }
        }
        return null;
    }

    @Override
    public boolean reloadCommandAliases() {
        commandMap.registerServerAliases();
        return true; // TODO: better error detection?
    }

    ////////////////////////////////////////////////////////////////////////////
    // Commands and console

    @Override
    public BanList getBanList(Type type) {
        switch (type) {
            case NAME:
                return nameBans;
            case IP:
                return ipBans;
            default:
                throw new IllegalArgumentException("Unknown BanList type " + type);
        }
    }

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
        ConfigurationSection section = config.getConfigFile(Key.COMMANDS_FILE).getConfigurationSection("aliases");
        if (section == null) {
            return aliases;
        }
        for (String key : section.getKeys(false)) {
            List<String> list = section.getStringList(key);
            aliases.put(key, list.toArray(new String[list.size()]));
        }
        return aliases;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player management

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

    @Override
    public Set<OfflinePlayer> getOperators() {
        return opsList.getUUIDs().stream().map(this::getOfflinePlayer).collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        return onlineView;
    }

    public Collection<GlowPlayer> getRawOnlinePlayers() {
        return onlinePlayers;
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
        getPlayerDataService().getOfflinePlayers().stream().filter(offline -> !uuids.contains(offline.getUniqueId())).forEach(offline -> {
            result.add(offline);
            uuids.add(offline.getUniqueId());
        });

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

    public OfflinePlayer getOfflinePlayer(PlayerProfile profile) {
        return new GlowOfflinePlayer(this, profile);
    }

    @Override
    @Deprecated
    public OfflinePlayer getOfflinePlayer(String name) {
        Player onlinePlayer = getPlayerExact(name);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }
        OfflinePlayer result = getPlayerExact(name);
        if (result == null) {
            //probably blocking (same player once per minute)
            PlayerProfile profile = PlayerProfile.getProfile(name);
            if (profile == null) {
                result = getOfflinePlayer(new PlayerProfile(name, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes())));
            } else {
                result = getOfflinePlayer(profile);
            }
        }
        return result;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid) {
        Player onlinePlayer = getPlayer(uuid);
        if (onlinePlayer != null) {
            return onlinePlayer;
        }
        OfflinePlayer result = getPlayer(uuid);
        if (result == null) {
            result = new GlowOfflinePlayer(this, uuid);
        }
        return result;
    }

    @Override
    public void savePlayers() {
        getOnlinePlayers().forEach(Player::saveData);
    }

    @Override
    public int broadcastMessage(String message) {
        return broadcast(message, BROADCAST_CHANNEL_USERS);
    }

    @Override
    public void broadcast(BaseComponent component) {

    }

    @Override
    public void broadcast(BaseComponent... components) {

    }

    public void broadcastPacket(Message message) {
        for (GlowPlayer player : getRawOnlinePlayers()) {
            player.getSession().send(message);
        }
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
        return whitelist.getProfiles().stream().map(this::getOfflinePlayer).collect(Collectors.toSet());
    }

    @Override
    public void reloadWhitelist() {
        whitelist.load();
    }

    @Override
    public Set<String> getIPBans() {
        return ipBans.getBanEntries().stream().map(BanEntry::getTarget).collect(Collectors.toSet());
    }

    @Override
    public void banIP(String address) {
        ipBans.addBan(address, null, null, null);
    }

    @Override
    public void unbanIP(String address) {
        ipBans.pardon(address);
    }

    ////////////////////////////////////////////////////////////////////////////
    // World management

    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        return nameBans.getBanEntries().stream().map(entry -> getOfflinePlayer(entry.getTarget())).collect(Collectors.toSet());
    }

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
     *
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
            return new NetherGenerator();
        } else if (environment == Environment.THE_END) {
            return new TheEndGenerator();
        } else {
            if (type == WorldType.FLAT) {
                return new SuperflatGenerator();
            } else {
                return new OverworldGenerator();
            }
        }
    }

    @Override
    public GlowWorld createWorld(WorldCreator creator) {
        GlowWorld world = getWorld(creator.name());
        if (world != null) {
            return world;
        }
        if (isGenerationDisabled()) {
            logger.warning("World generation is disabled! World '" + creator.name() + "' will be empty.");
        }

        if (creator.generator() == null) {
            creator.generator(getGenerator(creator.name(), creator.environment(), creator.type()));
        }

        // GlowWorld's constructor calls addWorld below.
        return new GlowWorld(this, creator);
    }

    /**
     * Add a world to the internal world collection.
     *
     * @param world The world to add.
     */
    void addWorld(GlowWorld world) {
        worlds.addWorld(world);
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
            return true;
        }
        return false;
    }

    @Override
    public GlowMapView getMap(short id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    ////////////////////////////////////////////////////////////////////////////
    // Inventory and crafting

    @Override
    public GlowMapView createMap(World world) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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
    public Merchant createMerchant(String title) {
        // todo: 1.11... ???
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Server icons

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return new GlowInventory(owner, type, type.getDefaultSize(), title);
    }

    @Override
    public GlowServerIcon getServerIcon() {
        return defaultIcon;
    }

    @Override
    public CachedServerIcon loadServerIcon(File file) throws Exception {
        return new GlowServerIcon(file);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Plugin messages

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage image) throws Exception {
        return new GlowServerIcon(image);
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {
        StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);
        for (Player player : getOnlinePlayers()) {
            player.sendPluginMessage(source, channel, message);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration with special handling

    @Override
    public Set<String> getListeningPluginChannels() {
        HashSet<String> result = new HashSet<>();
        for (Player player : getOnlinePlayers()) {
            result.addAll(player.getListeningPluginChannels());
        }
        return result;
    }

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
    public WarningState getWarningState() {
        return warnState;
    }

    @Override
    public int getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void setIdleTimeout(int timeout) {
        idleTimeout = timeout;
    }

    @Override
    public ChunkData createChunkData(World world) {
        return new GlowChunkData(world);
    }

    @Override
    public BossBar createBossBar(String title, BarColor color, BarStyle style, BarFlag... flags) {
        GlowBossBar bossBar = new GlowBossBar(title, color, style, flags);
        bossBarManager.register(bossBar);
        return bossBar;
    }

    @Override
    public double[] getTPS() {
        return new double[]{20, 20, 20}; // TODO: show TPS
    }

    ////////////////////////////////////////////////////////////////////////////
    // Configuration

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public int getPort() {
        return port;
    }

    public ServerConfig getConfig() {
        return config;
    }

    @Override
    public String getServerName() {
        return config.getString(Key.SERVER_NAME);
    }

    @Override
    public String getServerId() {
        return Integer.toHexString(getServerName().hashCode());
    }

    @Override
    public int getMaxPlayers() {
        return config.getInt(Key.MAX_PLAYERS);
    }

    @Override
    public String getUpdateFolder() {
        return config.getString(Key.UPDATE_FOLDER);
    }

    @Override
    public File getUpdateFolderFile() {
        return new File(getUpdateFolder());
    }

    @Override
    public boolean getOnlineMode() {
        return config.getBoolean(Key.ONLINE_MODE);
    }

    @Override
    public boolean getAllowNether() {
        return config.getBoolean(Key.ALLOW_NETHER);
    }

    @Override
    public boolean getAllowEnd() {
        return config.getBoolean(Key.ALLOW_END);
    }

    @Override
    public int getViewDistance() {
        return config.getInt(Key.VIEW_DISTANCE);
    }

    @Override
    public String getMotd() {
        return ChatColor.translateAlternateColorCodes('&', config.getString(Key.MOTD));
    }

    @Override
    public File getWorldContainer() {
        return new File(config.getString(Key.WORLD_FOLDER));
    }

    @Override
    public String getWorldType() {
        return config.getString(Key.LEVEL_TYPE);
    }

    @Override
    public boolean getGenerateStructures() {
        return config.getBoolean(Key.GENERATE_STRUCTURES);
    }

    @Override
    public long getConnectionThrottle() {
        return config.getInt(Key.CONNECTION_THROTTLE);
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        return config.getInt(Key.ANIMAL_TICKS);
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        return config.getInt(Key.MONSTER_TICKS);
    }

    @Override
    public boolean isHardcore() {
        return config.getBoolean(Key.HARDCORE);
    }

    @Override
    public int getMonsterSpawnLimit() {
        return config.getInt(Key.MONSTER_LIMIT);
    }

    @Override
    public int getAnimalSpawnLimit() {
        return config.getInt(Key.ANIMAL_LIMIT);
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return config.getInt(Key.WATER_ANIMAL_LIMIT);
    }

    @Override
    public int getAmbientSpawnLimit() {
        return config.getInt(Key.AMBIENT_LIMIT);
    }

    @Override
    public String getShutdownMessage() {
        return config.getString(Key.SHUTDOWN_MESSAGE);
    }

    @Override
    public boolean getAllowFlight() {
        return config.getBoolean(Key.ALLOW_FLIGHT);
    }

    public int getMaxBuildHeight() {
        return Math.max(64, Math.min(256, config.getInt(Key
                .MAX_BUILD_HEIGHT)));
    }

    public boolean getClassicWater() {
        return config.getBoolean(Key.WATER_CLASSIC);
    }

    public String getConsolePrompt() {
        return config.getString(Key.CONSOLE_PROMPT);
    }

    public String getConsoleDateFormat() {
        return config.getString(Key.CONSOLE_DATE);
    }

    public String getConsoleLogDateFormat() {
        return config.getString(Key.CONSOLE_LOG_DATE);
    }

    public String getServerType() {
        return "VANILLA";
    }

    public boolean doMetrics() {
        return config.getBoolean(Key.METRICS);
    }

    public boolean getAllowClientMods() {
        return config.getBoolean(Key.ALLOW_CLIENT_MODS);
    }

    public int getPlayerSampleCount() {
        return config.getInt(Key.PLAYER_SAMPLE_COUNT);
    }

    public boolean isGenerationDisabled() {
        return config.getBoolean(Key.DISABLE_GENERATION);
    }

    public boolean doesUseGPGPU() {
        return isCLApplicable && config.getBoolean(Key.GPGPU);
    }
}
