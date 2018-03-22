package net.glowstone;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.scheduler.GlowScheduler;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Central class for the calling of events.
 */
public class EventFactory {

    public EventFactory() {
    }

    /**
     * Calls an event through the plugin manager.
     *
     * @param event The event to throw.
     * @param <T> The type of the event.
     * @return the called event
     */
    public <T extends Event> T callEvent(T event) {
        Server server = Bukkit.getServer();

        if (event.isAsynchronous()) {
            server.getPluginManager().callEvent(event);
            return event;
        } else {
            FutureTask<T> task = new FutureTask<>(
                () -> server.getPluginManager().callEvent(event), event);
            BukkitScheduler scheduler = server.getScheduler();
            ((GlowScheduler) scheduler).scheduleInTickExecution(task);
            try {
                return task.get();
            } catch (InterruptedException e) {
                GlowServer.logger.log(Level.WARNING,
                        "Interrupted while handling " + event.getClass().getSimpleName());
                return event;
            } catch (CancellationException e) {
                GlowServer.logger.log(Level.WARNING,
                        "Not handling event " + event.getClass().getSimpleName()
                                + " due to shutdown");
                return event;
            } catch (ExecutionException e) {
                throw new RuntimeException(e); // No checked exceptions declared for callEvent
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player Events

    /**
     * Handles pre-hooks for a player login.
     *
     * @param name the name of the player who is logging in
     * @param address the address of the player who is logging in
     * @param uuid the UUID of the player who is logging in, provided by Mojang
     * @return an AsyncPlayerPreLoginEvent
     */
    @SuppressWarnings("deprecation")
    public AsyncPlayerPreLoginEvent onPlayerPreLogin(String name, InetSocketAddress address,
            UUID uuid) {
        // call async event
        AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(name, address
                .getAddress(), uuid);
        callEvent(event);

        // call sync event only if needed
        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length > 0) {
            // initialize event to match current state from async event
            PlayerPreLoginEvent syncEvent = new PlayerPreLoginEvent(name, address
                    .getAddress(), uuid);
            if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                syncEvent.disallow(event.getResult(), event.getKickMessage());
            }

            // call event synchronously and copy data back to original event
            callEvent(syncEvent);
            event.disallow(syncEvent.getResult(), syncEvent.getKickMessage());
        }

        return event;
    }

    /**
     * Handles post-hooks for a player login, including the name and IP banlists, whitelist policy
     * and occupancy limit.
     *
     * @param player the login
     * @param hostname the hostname that was used to connect to the server
     * @return the completed event
     */
    public PlayerLoginEvent onPlayerLogin(GlowPlayer player, String hostname) {
        Server server = player.getServer();
        InetAddress address = player.getAddress().getAddress();
        String addressString = address.getHostAddress();
        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, address);

        BanList nameBans = server.getBanList(Type.NAME);
        BanList ipBans = server.getBanList(Type.IP);

        if (nameBans.isBanned(player.getName())) {
            event.disallow(Result.KICK_BANNED,
                    "Banned: " + nameBans.getBanEntry(player.getName()).getReason());
        } else if (ipBans.isBanned(addressString)) {
            event.disallow(Result.KICK_BANNED,
                    "Banned: " + ipBans.getBanEntry(addressString).getReason());
        } else if (server.hasWhitelist() && !player.isWhitelisted()) {
            event.disallow(Result.KICK_WHITELIST, "You are not whitelisted on this server.");
        } else if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            event.disallow(Result.KICK_FULL,
                    "The server is full (" + player.getServer().getMaxPlayers() + " players).");
        }

        return callEvent(event);
    }

    /**
     * Handles an incoming chat message.
     *
     * @param async This changes the event to a synchronous state.
     * @param player the sending player
     * @param message the message
     * @return the completed event
     */
    @SuppressWarnings("deprecation")
    public AsyncPlayerChatEvent onPlayerChat(boolean async, Player player, String message) {
        // call async event
        Set<Player> recipients = new HashSet<>(player.getServer().getOnlinePlayers());
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, message, recipients);
        callEvent(event);

        // call sync event only if needed
        if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length > 0) {
            // initialize event to match current state from async event
            PlayerChatEvent syncEvent = new PlayerChatEvent(player, event.getMessage(), event
                    .getFormat(), recipients);
            syncEvent.setCancelled(event.isCancelled());

            // call event synchronously and copy data back to original event
            callEvent(syncEvent);
            event.setMessage(syncEvent.getMessage());
            event.setFormat(syncEvent.getFormat());
            event.setCancelled(syncEvent.isCancelled());
        }

        return event;
    }

    public PlayerJoinEvent onPlayerJoin(Player player) {
        return callEvent(new PlayerJoinEvent(player,
                ChatColor.YELLOW + player.getName() + " joined the game"));
    }

    public PlayerKickEvent onPlayerKick(Player player, String reason) {
        return callEvent(new PlayerKickEvent(player, reason, null));
    }

    public PlayerQuitEvent onPlayerQuit(Player player) {
        return callEvent(new PlayerQuitEvent(player,
                ChatColor.YELLOW + player.getName() + " left the game"));
    }

    /**
     * Handles a click in the air.
     *
     * @param player the player
     * @param action the click action
     * @param hand the active hand
     * @return the completed event
     */
    public PlayerInteractEvent onPlayerInteract(Player player, Action action,
            EquipmentSlot hand) {
        return onPlayerInteract(player, action, hand, null, BlockFace.SELF);
    }

    /**
     * Handles a click on a block.
     *
     * @param player the player
     * @param action the click action
     * @param hand the active hand
     * @param clicked the block clicked
     * @param face the side of the block clicked
     * @return the completed event
     */
    public PlayerInteractEvent onPlayerInteract(Player player, Action action,
            EquipmentSlot hand, Block clicked, BlockFace face) {
        return callEvent(new PlayerInteractEvent(player, action,
                hand == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand()
                        : player.getInventory().getItemInMainHand(), clicked, face, hand));
    }

    /**
     * Runs an EntityDamageEvent and updates {@link org.bukkit.entity.Entity#setLastDamageCause} and
     * (for a {@link LivingEntity} only) {@link LivingEntity#setLastDamage(double)}.
     *
     * @param event the event to run
     * @param <T> the event's type
     * @return the completed event
     */
    public <T extends EntityDamageEvent> T onEntityDamage(T event) {
        T result = callEvent(event);
        if (!result.isCancelled()) {
            result.getEntity().setLastDamageCause(result);
            if (result.getEntity() instanceof LivingEntity) {
                ((LivingEntity) result.getEntity()).setLastDamage(result.getDamage());
            }
        }
        return result;
    }
}
