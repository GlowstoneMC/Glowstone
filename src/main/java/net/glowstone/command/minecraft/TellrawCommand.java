package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.util.Collections;

public class TellrawCommand extends VanillaCommand {

    public TellrawCommand() {
        super("tellraw", I.tr("command.minecraft.tellraw.description"), I.tr("command.minecraft.tellraw.usage"), Collections.emptyList());
        setPermission("minecraft.command.tellraw");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.tellraw.usage")));
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || sender instanceof Player && !((Player) sender).canSee(player)) {
            sender.sendMessage(I.tr(sender, "command.generic.player.offline", args[0]));
            return false;
        } else {
            StringBuilder message = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                if (i > 1) message.append(" ");
                message.append(args[i]);
            }

            Object obj = null;
            String json = message.toString();
            try {
                obj = JSONValue.parseWithException(json);
            } catch (ParseException e) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.tellraw.parse.1", e.getMessage()));
                return false;
            }
            if (obj instanceof JSONArray || obj instanceof JSONObject) {
                BaseComponent[] components = ComponentSerializer.parse(json);
                player.sendMessage(components);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.tellraw.parse.2"));
                return false;
            }
        }
    }
}
