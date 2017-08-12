package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;

import java.util.Collections;

public class SayCommand extends VanillaCommand {

    public SayCommand() {
        super("say", I.tr("command.minecraft.say.description"), I.tr("command.minecraft.say.usage"), Collections.emptyList());
        setPermission("minecraft.command.say");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        if (args.length == 0) {
            sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.say.usage")));
            return false;
        }
        StringBuilder message = new StringBuilder("[").append(sender.getName() == null ? I.tr("command.minecraft.say.server") : sender.getName()).append("] ");
        for (String arg : args) {
            if (arg.startsWith("@") && arg.length() >= 2 && CommandUtils.isPhysical(sender)) {
                // command targets
                Location location = CommandUtils.getLocation(sender);
                CommandTarget target = new CommandTarget(sender, arg);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    sender.sendMessage(I.tr(sender, "command.generic.selector", arg));
                    return false;
                }
                message.append(CommandUtils.prettyPrint(matched)).append(" ");
            } else {
                message.append(arg).append(" ");
            }
        }
        Bukkit.broadcastMessage(message.toString());
        return true;
    }
}
