package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;

import java.util.Collections;

public class SayCommand extends BukkitCommand {
    public SayCommand() {
        super("say", "Say a message.", "/say <message ...>", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        StringBuilder message = new StringBuilder("[").append(sender.getName() == null ? "Server" : sender.getName()).append("] ");
        boolean targetsSupported = sender instanceof Entity || sender instanceof BlockCommandSender;
        for (String arg : args) {
            if (arg.startsWith("@") && arg.length() >= 2 && targetsSupported) {
                // command targets
                Location location = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
                CommandTarget target = new CommandTarget(arg);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    sender.sendMessage(ChatColor.RED + "Selector '" + arg + "' found nothing");
                    return false;
                }
                message.append(prettyPrint(matched)).append(" ");
            } else {
                message.append(arg).append(" ");
            }
        }
        Bukkit.broadcastMessage(message.toString());
        return true;
    }

    private String prettyPrint(Entity[] entities) {
        System.out.println(entities.length);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entities.length; i++) {
            Entity entity = entities[i];
            String name = entity.getName();
            if (name == null || name.isEmpty()) {
                name = entity.getType().getName();
            }
            if (i == entities.length - 1 && entities.length > 1) {
                builder.append(" and ");
            } else if (i > 0) {
                builder.append(", ");
            }
            builder.append(name);
        }
        return builder.toString();
    }
}
