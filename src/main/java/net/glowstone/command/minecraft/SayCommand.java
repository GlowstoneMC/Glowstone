package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.GlowstoneMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import java.util.Arrays;

public class SayCommand extends GlowVanillaCommand {

    public SayCommand() {
        super("say");
        setPermission("minecraft.command.say"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        StringBuilder message = new StringBuilder("[")
                .append(sender.getName() == null
                        ? GlowstoneMessages.Command.SAY_SERVER.get() : sender.getName())
                .append("] ");
        for (String arg : args) {
            if (arg.startsWith("@") && arg.length() >= 2 && CommandUtils.isPhysical(sender)) {
                // command targets
                Location location = CommandUtils.getLocation(sender);
                CommandTarget target = new CommandTarget(sender, arg);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                            .sendInColor(ChatColor.RED, sender, arg);
                    return false;
                }
                message.append(commandMessages.joinList(
                        Arrays.stream(matched).map(Entity::getName))).append(' ');
            } else {
                message.append(arg).append(' ');
            }
        }
        Bukkit.broadcastMessage(message.toString());
        return true;
    }
}
