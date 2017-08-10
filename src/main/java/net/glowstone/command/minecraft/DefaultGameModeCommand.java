package net.glowstone.command.minecraft;

import net.glowstone.command.GameModeUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultGameModeCommand extends VanillaCommand {

    public DefaultGameModeCommand() {
        super("defaultgamemode", I.tr("command.minecraft.defaultgamemode.description"), I.tr("command.minecraft.defaultgamemode.usage"), Collections.emptyList());
        setPermission("minecraft.command.defaultgamemode");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.defaultgamemode.usage")));
            return false;
        }

        final String inputMode = args[0];
        final GameMode gamemode = GameModeUtils.build(inputMode);

        if (gamemode == null) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.defaultgamemode.unknown", inputMode));
            return false;
        }

        Bukkit.getServer().setDefaultGameMode(gamemode);
        sender.sendMessage(I.tr(sender, "command.minecraft.defaultgamemode.unknown", ChatColor.GRAY + "" + ChatColor.ITALIC + GameModeUtils.prettyPrint(gamemode)));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], GameModeUtils.GAMEMODE_NAMES, new ArrayList(GameModeUtils.GAMEMODE_NAMES.size()));
        } else {
            return Collections.emptyList();
        }
    }
}
