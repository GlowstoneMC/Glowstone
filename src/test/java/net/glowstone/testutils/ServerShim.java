package net.glowstone.testutils;

import com.avaje.ebean.config.ServerConfig;
import net.glowstone.inventory.GlowItemFactory;
import org.bukkit.*;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Todo: Javadoc for ServerShim.
 */
public class ServerShim implements Server {

    public static void install() {
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(new ServerShim());
        }
    }

    // actual implementations

    public String getName() {
        return "Glowstone";
    }

    public String getVersion() {
        return "Test-Shim";
    }

    public String getBukkitVersion() {
        return "Test-Shim";
    }

    public ItemFactory getItemFactory() {
        return GlowItemFactory.instance();
    }

    public Logger getLogger() {
        return Logger.getLogger("Test-Shim");
    }

    // do nothing stubs

    @Override
    public Player[] _INVALID_getOnlinePlayers() {
        return new Player[0];
    }

    public Collection<? extends Player> getOnlinePlayers() {
        return Arrays.asList();
    }

    public int getMaxPlayers() {
        return 0;
    }

    public int getPort() {
        return 0;
    }

    public int getViewDistance() {
        return 0;
    }

    public String getIp() {
        return null;
    }

    public String getServerName() {
        return null;
    }

    public String getServerId() {
        return null;
    }

    public String getWorldType() {
        return null;
    }

    public boolean getGenerateStructures() {
        return false;
    }

    public boolean getAllowEnd() {
        return false;
    }

    public boolean getAllowNether() {
        return false;
    }

    public boolean hasWhitelist() {
        return false;
    }

    public void setWhitelist(boolean value) {

    }

    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return null;
    }

    public void reloadWhitelist() {

    }

    public int broadcastMessage(String message) {
        return 0;
    }

    public String getUpdateFolder() {
        return null;
    }

    public File getUpdateFolderFile() {
        return null;
    }

    public long getConnectionThrottle() {
        return 0;
    }

    public int getTicksPerAnimalSpawns() {
        return 0;
    }

    public int getTicksPerMonsterSpawns() {
        return 0;
    }

    public Player getPlayer(String name) {
        return null;
    }

    public Player getPlayerExact(String name) {
        return null;
    }

    public List<Player> matchPlayer(String name) {
        return null;
    }

    public Player getPlayer(UUID id) {
        return null;
    }

    public PluginManager getPluginManager() {
        return null;
    }

    public BukkitScheduler getScheduler() {
        return null;
    }

    public ServicesManager getServicesManager() {
        return null;
    }

    public List<World> getWorlds() {
        return null;
    }

    public World createWorld(WorldCreator creator) {
        return null;
    }

    public boolean unloadWorld(String name, boolean save) {
        return false;
    }

    public boolean unloadWorld(World world, boolean save) {
        return false;
    }

    public World getWorld(String name) {
        return null;
    }

    public World getWorld(UUID uid) {
        return null;
    }

    public MapView getMap(short id) {
        return null;
    }

    public MapView createMap(World world) {
        return null;
    }

    public void reload() {

    }

    public PluginCommand getPluginCommand(String name) {
        return null;
    }

    public void savePlayers() {

    }

    public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
        return false;
    }

    public void configureDbConfig(ServerConfig config) {

    }

    public boolean addRecipe(Recipe recipe) {
        return false;
    }

    public List<Recipe> getRecipesFor(ItemStack result) {
        return null;
    }

    public Iterator<Recipe> recipeIterator() {
        return null;
    }

    public void clearRecipes() {

    }

    public void resetRecipes() {

    }

    public Map<String, String[]> getCommandAliases() {
        return null;
    }

    public int getSpawnRadius() {
        return 0;
    }

    public void setSpawnRadius(int value) {

    }

    public boolean getOnlineMode() {
        return false;
    }

    public boolean getAllowFlight() {
        return false;
    }

    public boolean isHardcore() {
        return false;
    }

    public boolean useExactLoginLocation() {
        return false;
    }

    public void shutdown() {

    }

    public int broadcast(String message, String permission) {
        return 0;
    }

    public OfflinePlayer getOfflinePlayer(String name) {
        return null;
    }

    public OfflinePlayer getOfflinePlayer(UUID id) {
        return null;
    }

    public Set<String> getIPBans() {
        return null;
    }

    public void banIP(String address) {

    }

    public void unbanIP(String address) {

    }

    public Set<OfflinePlayer> getBannedPlayers() {
        return null;
    }

    public BanList getBanList(BanList.Type type) {
        return null;
    }

    public Set<OfflinePlayer> getOperators() {
        return null;
    }

    public GameMode getDefaultGameMode() {
        return null;
    }

    public void setDefaultGameMode(GameMode mode) {

    }

    public ConsoleCommandSender getConsoleSender() {
        return null;
    }

    public File getWorldContainer() {
        return null;
    }

    public OfflinePlayer[] getOfflinePlayers() {
        return new OfflinePlayer[0];
    }

    public Messenger getMessenger() {
        return null;
    }

    public HelpMap getHelpMap() {
        return null;
    }

    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return null;
    }

    public Inventory createInventory(InventoryHolder owner, InventoryType type, String title) {
        return null;
    }

    public Inventory createInventory(InventoryHolder owner, int size) throws IllegalArgumentException {
        return null;
    }

    public Inventory createInventory(InventoryHolder owner, int size, String title) throws IllegalArgumentException {
        return null;
    }

    public int getMonsterSpawnLimit() {
        return 0;
    }

    public int getAnimalSpawnLimit() {
        return 0;
    }

    public int getWaterAnimalSpawnLimit() {
        return 0;
    }

    public int getAmbientSpawnLimit() {
        return 0;
    }

    public boolean isPrimaryThread() {
        return false;
    }

    public String getMotd() {
        return null;
    }

    public String getShutdownMessage() {
        return null;
    }

    public Warning.WarningState getWarningState() {
        return null;
    }

    public ScoreboardManager getScoreboardManager() {
        return null;
    }

    public CachedServerIcon getServerIcon() {
        return null;
    }

    public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception {
        return null;
    }

    public CachedServerIcon loadServerIcon(BufferedImage image) throws IllegalArgumentException, Exception {
        return null;
    }

    public void setIdleTimeout(int threshold) {

    }

    public int getIdleTimeout() {
        return 0;
    }

    public UnsafeValues getUnsafe() {
        return null;
    }

    public void sendPluginMessage(Plugin source, String channel, byte[] message) {

    }

    public Set<String> getListeningPluginChannels() {
        return null;
    }
}
