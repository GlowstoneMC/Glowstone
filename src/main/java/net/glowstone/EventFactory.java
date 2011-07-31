package net.glowstone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.inventory.ItemStack;

/**
 * Central class for the calling of events.
 */
public final class EventFactory {

    // Private to prevent creation
    private EventFactory() {}

    /**
     * Calls an event through the plugin manager.
     * @param event The event to throw.
     */
    private static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    // -- Player Events

    public static PlayerChatEvent onPlayerChat(Player player, String message) {
        return callEvent(new PlayerChatEvent(player, message));
    }

    public static PlayerCommandPreprocessEvent onPlayerCommand(Player player, String message) {
        return callEvent(new PlayerCommandPreprocessEvent(player, message));
    }

    public static PlayerJoinEvent onPlayerJoin(Player player) {
        return callEvent(new PlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game"));
    }

    public static PlayerKickEvent onPlayerKick(Player player, String reason) {
        return callEvent(new PlayerKickEvent(player, reason, ChatColor.YELLOW + player.getName() + " left the game"));
    }

    public static PlayerQuitEvent onPlayerQuit(Player player) {
        return callEvent(new PlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game"));
    }

    public static PlayerMoveEvent onPlayerMove(Player player, Location from, Location to) {
        return callEvent(new PlayerMoveEvent(player, from, to));
    }
    
    public static PlayerInteractEvent onPlayerInteract(Player player, Action action) {
        return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), null, null));
    }
    
    public static PlayerInteractEvent onPlayerInteract(Player player, Action action, Block clicked, BlockFace face) {
        return callEvent(new PlayerInteractEvent(player, action, player.getItemInHand(), clicked, face));
    }
    
    // -- Block Events

    public static BlockBreakEvent onBlockBreak(Block block, Player player) {
        return callEvent(new BlockBreakEvent(block, player));
    }

    public static BlockDamageEvent onBlockDamage(Player player, Block block) {
        return onBlockDamage(player, block, player.getItemInHand(), false);
    }

    public static BlockDamageEvent onBlockDamage(Player player, Block block, ItemStack tool, boolean instaBreak) {
        return callEvent(new BlockDamageEvent(player, block, tool, instaBreak));
    }
    
    public static BlockPlaceEvent onBlockPlace(Block block, BlockState newState, Block against, Player player) {
        return callEvent(new BlockPlaceEvent(block, newState, against, player.getItemInHand(), player, true));
    }
}
