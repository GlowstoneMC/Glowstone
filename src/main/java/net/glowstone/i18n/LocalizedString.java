package net.glowstone.i18n;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public interface LocalizedString {
    String get();

    String get(Object... args);

    void send(CommandSender recipient, Object... args);

    void sendInColor(CommandSender recipient, ChatColor color, Object... args);
}
