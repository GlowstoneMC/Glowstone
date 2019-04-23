package net.glowstone.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NonNls;

public class LocalizedStringImpl implements LocalizedString {
    private static final ResourceBundle STRINGS = ResourceBundle.getBundle("strings"); // NON-NLS

    @Getter
    @NonNls
    private final String key;

    private final ResourceBundle resourceBundle;

    LocalizedStringImpl(String key) {
        this.key = key;
        this.resourceBundle = STRINGS;
    }

    public LocalizedStringImpl(@NonNls String key, ResourceBundle resourceBundle) {
        this.key = key;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public String get() {
        return resourceBundle.getString(getKey());
    }

    @Override
    public String get(Object... args) {
        return MessageFormat.format(get(), args);
    }

    @Override
    public void send(CommandSender recipient, Object... args) {
        recipient.sendMessage(get(args));
    }

    @Override
    public void sendInColor(ChatColor color, CommandSender recipient, Object... args) {
        recipient.sendMessage(color.toString() + get(args));
    }

    @Override
    public String toString() {
        return get();
    }
}
