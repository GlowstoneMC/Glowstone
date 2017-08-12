package net.glowstone;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.lang.I;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

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
                GlowServer.logger.log(Level.WARNING, I.tr("event.interrupted", event.getClass().getSimpleName()));
                return event;
            } catch (CancellationException e) {
                GlowServer.logger.log(Level.WARNING, I.tr("event.shutdown", event.getClass().getSimpleName()));
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
            event.disallow(Result.KICK_BANNED,
                    I.tr("event.banned", nameBans.getBanEntry(player.getName()).getReason()));
        } else if (ipBans.isBanned(addressString)) {
            event.disallow(Result.KICK_BANNED,
                    I.tr("event.banned", ipBans.getBanEntry(addressString).getReason()));
        } else if (server.hasWhitelist() && !player.isWhitelisted()) {
            event.disallow(Result.KICK_WHITELIST,
                    I.tr("event.whitelist.missing"));
        } else if (server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            event.disallow(Result.KICK_FULL,
                    I.tr("server.full", player.getServer().getMaxPlayers()));
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
        return callEvent(new PlayerJoinEvent(player, I.tr("event.player.joined", player.getName())));
    }

    public static PlayerKickEvent onPlayerKick(Player player, String reason) {
        return callEvent(new PlayerKickEvent(player, reason, null));
    }

    public static PlayerQuitEvent onPlayerQuit(Player player) {
        return callEvent(new PlayerQuitEvent(player, I.tr("event.player.left", player.getName())));
    }

    public static PlayerInteractEvent onPlayerInteract(Player player, Action action) {
        return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), null, null));
    }

    public static PlayerInteractEvent onPlayerInteract(Player player, Action action, Block clicked, BlockFace face) {
        return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), clicked, face));
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
