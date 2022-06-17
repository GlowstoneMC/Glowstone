package net.glowstone.command.minecraft;

import net.glowstone.i18n.LocalizedStringImpl;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class KickCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public KickCommand() {
        super("kick");
        setPermission("minecraft.command.kick"); // NON-NLS
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
        String playerName = args[0];
        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) {
            commandMessages.getGeneric(GenericMessage.OFFLINE)
                .sendInColor(ChatColor.RED, sender, playerName);
            return false;
        }
        if (args.length == 1) {
            player.kickPlayer(null);
            new LocalizedStringImpl("kick.done.no-reason", commandMessages.getResourceBundle())
                .send(sender, player.getName());
            return true;
        }
        String reason = StringUtils.join(args, ' ', 1, args.length);
        player.kickPlayer(reason);
        new LocalizedStringImpl("kick.done", commandMessages.getResourceBundle())
            .send(sender, player.getName(), reason);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
