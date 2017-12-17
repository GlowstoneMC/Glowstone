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
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

/**
 * Central class for the calling of events.
 */
public final class EventFactory {

    private EventFactory() {
    }

    /**
     * Calls an event through the plugin manager.
     *
     * @param event The event to throw.
     * @param <T> The type of the event.
     * @return the called event
     */
    public static <T extends Event> T callEvent(T event) {
        GlowServer server = (GlowServer) Bukkit.getServer();

        if (event.isAsynchronous()) {
            server.getPluginManager().callEvent(event);
            return event;
        } else {
            FutureTask<T> task = new FutureTask<>(() -> server.getPluginManager().callEvent(event), event);
            server.getScheduler().scheduleInTickExecution(task);
            try {
                return task.get();
            } catch (InterruptedException e) {
                GlowServer.logger.log(Level.WARNING, "Interrupted while handling " + event.getClass().getSimpleName());
                return event;
            } catch (CancellationException e) {
                GlowServer.logger.log(Level.WARNING, "Not handling event " + event.getClass().getSimpleName() + " due to shutdown");
                return event;
            } catch (ExecutionException e) {
                throw new RuntimeException(e); // No checked exceptions declared for callEvent
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Player Events

    @SuppressWarnings("deprecation")
    public static AsyncPlayerPreLoginEvent onPlayerPreLogin(String name, InetSocketAddress address, UUID uuid) {
        // call async event
        AsyncPlayerPreLoginEvent event = new AsyncPlayerPreLoginEvent(name, address.getAddress(), uuid);
        callEvent(event);

        // call sync event only if needed
        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length > 0) {
            // initialize event to match current state from async event
            PlayerPreLoginEvent syncEvent = new PlayerPreLoginEvent(name, address.getAddress(), uuid);
            if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                syncEvent.disallow(event.getResult(), event.getKickMessage());
            }

            // call event synchronously and copy data back to original event
            callEvent(syncEvent);
            event.disallow(syncEvent.getResult(), syncEvent.getKickMessage());
        }

        return event;
    }

    public static PlayerLoginEvent onPlayerLogin(GlowPlayer player, String hostname) {
        GlowServer server = player.getServer();
        InetAddress address = player.getAddress().getAddress();
        String addressString = address.getHostAddress();
        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, address);

        BanList nameBans = server.getBanList(Type.NAME);
        BanList ipBans = server.getBanList(Type.IP);

        if (nameBans.isBanned(player.getName())) {
            event.disallow(Result.KICK_BANNED, "Banned: " + nameBans.getBanEntry(player.getName()).getReason());
        } else if (ipBans.isBanned(addressString)) {
            event.disallow(Result.KICK_BANNED, "Banned: " + ipBans.getBanEntry(addressString).getReason());
        } else if (server.hasWhitelist() && !player.isWhitelisted()) {
            event.disallow(Result.KICK_WHITELIST, "You are not whitelisted on this server.");
        } else if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            event.disallow(Result.KICK_FULL, "The server is full (" + player.getServer().getMaxPlayers() + " players).");
        }

        return callEvent(event);
    }

    @SuppressWarnings("deprecation")
    public static AsyncPlayerChatEvent onPlayerChat(boolean async, Player player, String message) {
        // call async event
        Set<Player> recipients = new HashSet<>(player.getServer().getOnlinePlayers());
        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, message, recipients);
        callEvent(event);

        // call sync event only if needed
        if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length > 0) {
            // initialize event to match current state from async event
            PlayerChatEvent syncEvent = new PlayerChatEvent(player, event.getMessage(), event.getFormat(), recipients);
            syncEvent.setCancelled(event.isCancelled());

            // call event synchronously and copy data back to original event
            callEvent(syncEvent);
            event.setMessage(syncEvent.getMessage());
            event.setFormat(syncEvent.getFormat());
            event.setCancelled(syncEvent.isCancelled());
        }

        return event;
    }

    public static PlayerJoinEvent onPlayerJoin(Player player) {
        return callEvent(new PlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game"));
    }

    public static PlayerKickEvent onPlayerKick(Player player, String reason) {
        return callEvent(new PlayerKickEvent(player, reason, null));
    }

    public static PlayerQuitEvent onPlayerQuit(Player player) {
        return callEvent(new PlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game"));
    }

    public static PlayerInteractEvent onPlayerInteract(Player player, Action action, EquipmentSlot hand) {
        return callEvent(new PlayerInteractEvent(player, action,
            hand == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand(), null, null, hand));
    }

    public static PlayerInteractEvent onPlayerInteract(Player player, Action action, EquipmentSlot hand, Block clicked, BlockFace face) {
        return callEvent(new PlayerInteractEvent(player, action,
            hand == EquipmentSlot.OFF_HAND ? player.getInventory().getItemInOffHand() : player.getInventory().getItemInMainHand(), clicked, face, hand));
    }

    public static <T extends EntityDamageEvent> T onEntityDamage(T event) {
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
