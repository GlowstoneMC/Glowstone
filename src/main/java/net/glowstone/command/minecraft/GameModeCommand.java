package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameModeCommand extends VanillaCommand {

    private static final List<String> GAMEMODES = Arrays.asList("survival", "creative", "adventure", "spectator");

    public GameModeCommand() {
        super("gamemode", "Change the game mode of a player.", "/gamemode <mode> [player]", Collections.emptyList());
        setPermission("minecraft.command.gamemode");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0 || args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String gm = args[0].toLowerCase();
        GameMode gamemode = null;
        switch (gm) {
            case "c":
            case "1":
            case "creative":
                gamemode = GameMode.CREATIVE;
                break;
            case "s":
            case "0":
            case "survival":
                gamemode = GameMode.SURVIVAL;
                break;
            case "a":
            case "2":
            case "adventure":
                gamemode = GameMode.ADVENTURE;
                break;
            case "sp":
            case "3":
            case "spectator":
                gamemode = GameMode.SPECTATOR;
                break;
        }
        if (gamemode == null) {
            sender.sendMessage(ChatColor.RED + "'" + gm + "' is not a valid number");
            return false;
        }
        if (args.length == 1) {
            // self
            Player player = (Player) sender;
            updateGameMode(sender, player, gamemode);
            return true;
        }
        // with target
        boolean targetsSupported = sender instanceof Entity || sender instanceof BlockCommandSender;
        String name = args[1];
        if (name.startsWith("@") && name.length() >= 2 && targetsSupported) {
            Location location = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
            CommandTarget target = new CommandTarget(sender, name);
            Entity[] matched = target.getMatched(location);
            for (Entity entity : matched) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    updateGameMode(sender, player, gamemode);
                }
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online.");
            } else {
                updateGameMode(sender, player, gamemode);
            }
        }
        return true;
    }

    private void updateGameMode(CommandSender sender, Player who, GameMode gameMode) {
        String gameModeName = "Unknown";
        switch (gameMode) {
            case CREATIVE:
                gameModeName = "Creative";
                break;
            case SURVIVAL:
                gameModeName = "Survival";
                break;
            case ADVENTURE:
                gameModeName = "Adventure";
                break;
            case SPECTATOR:
                gameModeName = "Spectator";
                break;
        }
        who.setGameMode(gameMode);
        if (!sender.equals(who)) {
            sender.sendMessage(who.getDisplayName() + "'s game mode has been updated to " + ChatColor.GRAY + "" + ChatColor.ITALIC + gameModeName + " Mode" + ChatColor.RESET);
        }
        who.sendMessage("Your game mode has been updated to " + ChatColor.GRAY + "" + ChatColor.ITALIC + gameModeName + " Mode" + ChatColor.RESET);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], GAMEMODES, new ArrayList(GAMEMODES.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
