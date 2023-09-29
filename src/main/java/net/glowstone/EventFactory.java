package net.glowstone;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.GlowstoneMessages;
import net.glowstone.i18n.GlowstoneMessages.Kick;
import net.glowstone.scheduler.GlowScheduler;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Central class for the calling of events.
 */
public class EventFactory {

    /**
     * The instance of this class. Setter should only be called in tests when mocking.
     */
    @Getter
    @Setter
    private static EventFactory instance = new EventFactory();

    private EventFactory() {
    }

    /**
     * Calls an event through the plugin manager.
     *
     * @param event The event to throw.
     * @param <T> The type of the event.
     * @return the called event
     */
    public <T extends Event> T callEvent(T event) {
        if (event.getHandlers().getRegisteredListeners().length == 0) {
            return event;
        }
        Server server = ServerProvider.getServer();
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
                ConsoleMessages.Warn.Event.INTERRUPTED.log(e,
                        event.getClass().getSimpleName());
                return event;
            } catch (CancellationException e) {
                ConsoleMessages.Warn.Event.SHUTDOWN.log(event.getClass().getSimpleName());
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
                    Kick.BANNED.get(nameBans.getBanEntry(player.getName()).getReason()));
        } else if (ipBans.isBanned(addressString)) {
            event.disallow(Result.KICK_BANNED,
                    Kick.BANNED.get(ipBans.getBanEntry(addressString).getReason()));
        } else if (checkWhitelisted(player, event)
                && server.getOnlinePlayers().size() >= server.getMaxPlayers()) {
            event.disallow(Result.KICK_FULL, Kick.FULL.get(server.getMaxPlayers()));
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
                GlowstoneMessages.Player.JOINED.get(ChatColor.YELLOW, player.getName())));
    }

    public PlayerKickEvent onPlayerKick(Player player, String reason) {
        return callEvent(new PlayerKickEvent(player, reason, ""));
    }

    public PlayerQuitEvent onPlayerQuit(Player player) {
        return callEvent(new PlayerQuitEvent(player,
                GlowstoneMessages.Player.LEFT.get(ChatColor.YELLOW, player.getName())));
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

    /**
     * Checks whether a player is whitelisted when joining the server,
     * and fires the {@link ProfileWhitelistVerifyEvent} in the process.
     *
     * <p>The supplied {@link PlayerLoginEvent} will be disallowed by this method
     * if the player is not whitelisted.
     *
     * @param player the player joining the server
     * @param loginEvent the {@link PlayerLoginEvent} that will follow this check
     * @return true if the player is whitelisted, false otherwise
     */
    private boolean checkWhitelisted(GlowPlayer player, PlayerLoginEvent loginEvent) {
        // check whether the player is whitelisted (explicitly or implicitly)
        boolean whitelisted = player.isOp()
                || !player.getServer().hasWhitelist()
                || player.isWhitelisted();
        // fire the event to allow plugins to change this behavior
        ProfileWhitelistVerifyEvent event = callEvent(new ProfileWhitelistVerifyEvent(
                player.getProfile(),
                player.getServer().hasWhitelist(),
                whitelisted,
                player.isOp(),
                Kick.WHITELIST.get()
        ));
        if (event.isWhitelisted()) {
            return true;
        }
        // note: the kick message is mutable by plugins
        loginEvent.disallow(Result.KICK_WHITELIST, event.getKickMessage());
        return false;
    }
}
