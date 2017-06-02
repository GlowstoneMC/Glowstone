package net.glowstone.command.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class ListCommand extends BukkitCommand {
    private static final String[] EMPTY = new String[0];

    public ListCommand() {
        super("list", "Lists players on the server.", "/op <player>", Collections.emptyList());
        setPermission("minecraft.command.list");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Collection<String> messages = new ArrayList<>();
        messages.add("There are " + players.size() + "/" + Bukkit.getMaxPlayers() + " players online:");
        if (args.length > 0 && Objects.equals(args[0], "uuids")) {
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(p.getName() + " (" + p.getUniqueId() + ')'));
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> messages.add(p.getName()));
        }
        sender.sendMessage(messages.toArray(EMPTY));
        return true;
    }
}
