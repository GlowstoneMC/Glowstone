package net.glowstone.command.minecraft;

import static net.glowstone.command.GameModeUtils.NAMES_LOWERCASE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GameModeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class GameModeCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public GameModeCommand() {
        super("gamemode", "Change the game mode of a player.", "/gamemode <mode> [player]",
            Collections.emptyList());
        setPermission("minecraft.command.gamemode");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || args.length == 1 && !(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String gm = args[0];
        GameMode gamemode = GameModeUtils.build(gm);
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
        String name = args[1];
        if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, name);
            Entity[] matched = target.getMatched(location);
            for (Entity entity : matched) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    updateGameMode(sender, player, gamemode);
                }
            }
        } else {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player '" + name + "' is not online.");
            } else {
                updateGameMode(sender, player, gamemode);
            }
        }
        return true;
    }

    private void updateGameMode(CommandSender sender, Player who, GameMode gameMode) {
        String gameModeName = GameModeUtils.prettyPrint(gameMode);
        who.setGameMode(gameMode);
        if (!sender.equals(who)) {
            sender.sendMessage(
                who.getDisplayName() + "'s game mode has been updated to " + ChatColor.GRAY + ""
                    + ChatColor.ITALIC + gameModeName + " Mode" + ChatColor.RESET);
        }
        who.sendMessage(
            "Your game mode has been updated to " + ChatColor.GRAY + "" + ChatColor.ITALIC
                + gameModeName + " Mode" + ChatColor.RESET);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], NAMES_LOWERCASE,
                    new ArrayList(((List<String>) NAMES_LOWERCASE).size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
