package net.glowstone.command.minecraft;

import java.util.Arrays;
import java.util.Collections;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MeCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public MeCommand() {
        super("me", Collections.emptyList());
        setPermission("minecraft.command.me");
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

        final StringBuilder message = new StringBuilder("* ").append(CommandUtils.getName(sender));
        Arrays.stream(args).forEach(parameter -> message.append(' ').append(parameter));

        Bukkit.broadcastMessage(message.toString());

        return true;
    }
}
