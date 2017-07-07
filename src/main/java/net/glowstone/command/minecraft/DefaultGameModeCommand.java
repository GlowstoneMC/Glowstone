package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DefaultGameModeCommand extends VanillaCommand {
    private static final List<String> GAMEMODES = Arrays.asList("adventure", "creative", "survival", "spectator");

    public DefaultGameModeCommand() {
        super("defaultgamemode", "Sets the default game mode (creative, survival, etc.) for new players entering a multiplayer server.", "/defaultgamemode <mode>", Collections.emptyList());
        setPermission("minecraft.command.defaultgamemode");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Missing arguments. Usage: " + usageMessage);
            return false;
        }

        GameMode gamemode;
        final String inputMode = args[0];

        switch (inputMode.toLowerCase()) {
            case "s":
            case "0":
            case "survival":
                gamemode = GameMode.SURVIVAL;
                break;
            case "c":
            case "1":
            case "creative":
                gamemode = GameMode.CREATIVE;
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
            default:
                sender.sendMessage(ChatColor.RED + "Unknown mode '" + inputMode + "'.");
                return false;
        }

        Bukkit.getServer().setDefaultGameMode(gamemode);
        sender.sendMessage("Default game mode set to " + ChatColor.GRAY + "" + ChatColor.ITALIC + gamemode.toString().toLowerCase());

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], GAMEMODES, new ArrayList(GAMEMODES.size()));
        } else {
            return Collections.emptyList();
        }
    }
}
