package net.glowstone.command.minecraft;

import java.util.Collections;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;

public class SayCommand extends VanillaCommand {

    public SayCommand() {
        super("say", "Say a message.", "/say <message ...>", Collections.emptyList());
        setPermission("minecraft.command.say");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        StringBuilder message = new StringBuilder("[")
            .append(sender.getName() == null ? "Server" : sender.getName()).append("] ");
        for (String arg : args) {
            if (arg.startsWith("@") && arg.length() >= 2 && CommandUtils.isPhysical(sender)) {
                // command targets
                Location location = CommandUtils.getLocation(sender);
                CommandTarget target = new CommandTarget(sender, arg);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Selector '" + arg + "' found nothing");
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
