package net.glowstone.command.minecraft;

import net.glowstone.ServerProvider;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class SetIdleTimeoutCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SetIdleTimeoutCommand() {
        super("setidletimeout");
        setPermission("minecraft.command.setidletimeout"); // NON-NLS
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

        final String stringTimeout = args[0];
        int timeout;

        try {
            timeout = Integer.parseInt(stringTimeout);
        } catch (NumberFormatException ex) {
            commandMessages.getGeneric(GenericMessage.NAN)
                .sendInColor(ChatColor.RED, sender, stringTimeout);
            return false;
        }

        if (timeout <= 0) {
            new LocalizedStringImpl("setidletimeout.too-low", commandMessages.getResourceBundle())
                .sendInColor(ChatColor.RED, sender, timeout);
            return false;
        }

        ServerProvider.getServer().setIdleTimeout(timeout);
        new LocalizedStringImpl("setidletimeout.done", commandMessages.getResourceBundle())
            .send(sender, timeout);

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
