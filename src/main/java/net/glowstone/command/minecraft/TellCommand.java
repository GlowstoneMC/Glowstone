package net.glowstone.command.minecraft;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TellCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TellCommand() {
        super("tell", Arrays.asList("msg", "w")); // NON-NLS
        setPermission("minecraft.command.tell"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length <= 1) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String name = args[0];
        Player[] players;
        if (name.charAt(0) == '@' && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, name);
            target.getArguments()
                .put("type", new CommandTarget.SelectorValue("player")); // NON-NLS; only players
            Entity[] matched = target.getMatched(location);
            if (matched.length == 0) {
                commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            }
            players = new Player[matched.length];
            for (int i = 0; i < matched.length; i++) {
                players[i] = (Player) matched[i];
            }
        } else {
            Player player = Bukkit.getPlayer(name);
            if (player == null) {
                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            }
            players = new Player[]{player};
        }
        String senderName = CommandUtils.getName(sender);
        String message = StringUtils.join(args, ' ', 1, args.length);
        ResourceBundle bundle = commandMessages.getResourceBundle();
        LocalizedStringImpl senderMessage = new LocalizedStringImpl("tell.sender", bundle);
        for (Player player : players) {
            if (sender.equals(player)) {
                new LocalizedStringImpl("tell.self", bundle)
                        .sendInColor(ChatColor.RED, sender);
                continue;
            }
            new LocalizedStringImpl("tell.recipient", getBundle(player))
                    .send(player, senderName, message);
            senderMessage.send(sender, senderName, player.getName(), message);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
