package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Arrays;
import java.util.Collections;

public class MeCommand extends VanillaCommand {
    public MeCommand() {
        super("me", I.tr("command.minecraft.me.description"), I.tr("command.minecraft.me.usage"), Collections.emptyList());
        setPermission("minecraft.command.me");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0)  {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.me.usage")));
            return false;
        }

        final StringBuilder message = new StringBuilder("* ").append(CommandUtils.getName(sender));
        Arrays.stream(args).forEach(parameter -> message.append(" ").append(parameter));

        Bukkit.broadcastMessage(message.toString());

        return true;
    }
}
