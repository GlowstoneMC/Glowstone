package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.UuidUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommand extends GlowVanillaCommand {

    private static final String[] EMPTY = new String[0];

    public ListCommand() {
        super("list");
        setPermission("minecraft.command.list"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Collection<String> messages =
            new ArrayList<>(players.size() + 1);
        messages.add(new LocalizedStringImpl("list.header", commandMessages.getResourceBundle())
            .get(players.size(), Bukkit.getMaxPlayers()));
        if (args.length > 0 && (Objects.equals(args[0], "uuids" /* NON-NLS */) || Objects
            .equals(args[0], "ids" /* NON-NLS */))) {
            LocalizedStringImpl nameAndUuidMessage = new LocalizedStringImpl("list.name-and-uuid",
                commandMessages.getResourceBundle());
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(
                nameAndUuidMessage.get(p.getName(), UuidUtils.toString(p.getUniqueId()))));
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(p.getName()));
        }
        messages.forEach(sender::sendMessage);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return ImmutableList.of("uuids"); // NON-NLS
        }
        return Collections.emptyList();
    }
}
