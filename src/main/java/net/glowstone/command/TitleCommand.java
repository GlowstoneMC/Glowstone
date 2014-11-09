package net.glowstone.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.title.Title;
import org.bukkit.title.TitleOptions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Arrays;

public class TitleCommand extends BukkitCommand {

    public TitleCommand() {
        super("title");
        this.description = "Sends a title to the specified player(s)";
        this.usageMessage = "/title <player> <title|subtitle|clear|reset|times> ...";
        this.setAliases(Arrays.<String>asList());
        this.setPermission("glowstone.command.title");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        Player player = Bukkit.getPlayerExact(args[0]);

        if (player == null || (sender instanceof Player && !((Player) sender).canSee(player))) {
            sender.sendMessage("There's no player by that name online.");
        } else {
            if (args[1].equalsIgnoreCase("clear")) {
                player.clearTitle();
                sender.sendMessage("Cleared " + player.getName() + "'s title");
            } else if (args[1].equalsIgnoreCase("reset")) {
                player.resetTitle();
                sender.sendMessage("Reset " + player.getName() + "'s title");
            } else if (args[1].equalsIgnoreCase("times")) {
                if (args.length < 5) {
                    sender.sendMessage(ChatColor.RED + "Usage: /title <player> times <fadeIn> <stay> <fadeOut>");
                    return false;
                }

                TitleOptions options = player.getTitleOptions();

                int in = tryParseInt(sender, args[2], 0);
                int stay = tryParseInt(sender, args[3], in);
                int out = tryParseInt(sender, args[4], stay);

                if (out == -1) {
                    return false;
                }

                options.setFadeInTime(in);
                options.setFadeOutTime(out);
                options.setVisibleTime(stay);
                player.setTitleOptions(options);

                sender.sendMessage("Updated " + player.getName() + "'s title times");
            } else if (args[1].equalsIgnoreCase("title") || args[1].equalsIgnoreCase("subtitle")) {
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /title <player> " + args[1] + " <raw json>");
                    return false;
                }

                StringBuilder message = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    if (i > 2) message.append(" ");
                    message.append(args[i]);
                }

                String raw = message.toString().trim();

                if (raw.contains(" ")) {
                    // TODO: Improve parsing when text components are implemented

                    // Parse as JSON
                    Object parsed = JSONValue.parse(raw);
                    if (parsed == null || !(parsed instanceof JSONObject)) {
                        sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                        return false;
                    } else {
                        JSONObject object = (JSONObject) parsed;
                        if (!object.containsKey("text")) {
                            sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                            return false;
                        }
                        if (object.containsKey("extra")) {
                            Object extraRaw = object.get("extra");
                            if (!(extraRaw instanceof JSONArray)) {
                                sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                                return false;
                            }

                            JSONArray extra = (JSONArray) extraRaw;
                            if (extra.size() == 0) {
                                sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                                return false;
                            }
                            for (Object component : extra) {
                                if (!(component instanceof JSONObject) || !((JSONObject) component).containsKey("text")) {
                                    sender.sendMessage(ChatColor.RED + "Invalid JSON: Could not parse, invalid format?");
                                    return false;
                                }
                            }
                        }
                    }
                } // else single-word text is okay

                Title currentTitle = player.getTitle();
                if (args[1].equalsIgnoreCase("title")) {
                    currentTitle.setHeading(raw);
                } else {
                    currentTitle.setSubtitle(raw);
                }
                player.setTitle(currentTitle, args[1].equalsIgnoreCase("title"));

                sender.sendMessage("Updated " + player.getName() + "'s title");
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }
        }

        return true;
    }

    private int tryParseInt(CommandSender sender, String number, int lastParse) {
        if (lastParse == -1) {
            return -1;
        }

        int n = -1;
        try {
            n = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + number + "' is not a number");
        }
        return n;
    }
}
