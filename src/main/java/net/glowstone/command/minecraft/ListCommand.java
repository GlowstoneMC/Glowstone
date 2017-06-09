package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;

import java.util.*;

public class ListCommand extends VanillaCommand {
    private static final String[] EMPTY = new String[0];

    public ListCommand() {
        super("list", "Lists players on the server.", "/list [uuids]", Collections.emptyList());
        setPermission("minecraft.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Collection<String> messages = new ArrayList<>();
        messages.add("There are " + players.size() + "/" + Bukkit.getMaxPlayers() + " players online:");
        if (args.length > 0 && (Objects.equals(args[0], "uuids") || Objects.equals(args[0], "ids"))) {
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(p.getName() + " (" + p.getUniqueId() + ')'));
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(p.getName()));
        }
        sender.sendMessage(messages.toArray(EMPTY));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return ImmutableList.of("uuids");
        }
        return Collections.emptyList();
    }
}
