package net.glowstone;

import net.glowstone.entity.GlowPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public static void callEvent(Event event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    // -- Player Events

    public static PlayerChatEvent onPlayerChat(Player player, String message) {
        PlayerChatEvent event = new PlayerChatEvent(player, message);
        callEvent(event);
        return event;
    }

    public static PlayerCommandPreprocessEvent onPlayerCommand(Player player, String message) {
        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, message);
        callEvent(event);
        return event;
    }

    public static PlayerJoinEvent onPlayerJoin(Player player) {
        PlayerJoinEvent event = new PlayerJoinEvent(player, ChatColor.YELLOW + player.getName() + " joined the game");
        callEvent(event);
        return event;
    }

    public static PlayerKickEvent onPlayerKick(Player player, String reason) {
        PlayerKickEvent event = new PlayerKickEvent(player, reason, ChatColor.YELLOW + player.getName() + " left the game");
        callEvent(event);
        return event;
    }

    public static PlayerQuitEvent onPlayerQuit(Player player) {
        PlayerQuitEvent event = new PlayerQuitEvent(player, ChatColor.YELLOW + player.getName() + " left the game");
        callEvent(event);
        return event;
    }

    public static PlayerMoveEvent onPlayerMove(Player player, Location from, Location to) {
        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
        callEvent(event);
        return event;
    }

    public static BlockBreakEvent onBlockBreak(Block block, Player player) {
        BlockBreakEvent event = new BlockBreakEvent(block, player);
        callEvent(event);
        return event;
    }

    public static BlockDamageEvent onBlockDamage(Player player, Block block) {
        return onBlockDamage(player, block, player.getItemInHand(), false);
    }

    public static BlockDamageEvent onBlockDamage(Player player, Block block, ItemStack tool, boolean instaBreak) {
        BlockDamageEvent event = new BlockDamageEvent(player, block, tool, instaBreak);
        callEvent(event);
        return event;
    }
}
