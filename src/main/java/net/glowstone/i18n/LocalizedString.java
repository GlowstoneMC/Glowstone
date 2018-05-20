package net.glowstone.i18n;

import org.bukkit.command.CommandSender;

public interface LocalizedString {
    String get();

    String get(Object... args);

    void send(CommandSender recipient, Object... args);
}
