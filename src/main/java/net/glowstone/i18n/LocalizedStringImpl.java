package net.glowstone.i18n;

import java.text.Format;
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

    private final MessageFormat format;

    private final String pattern;

    LocalizedStringImpl(String key) {
        this(key, STRINGS);
    }

    public LocalizedStringImpl(@NonNls String key, ResourceBundle resourceBundle) {
        this.key = key;
        this.resourceBundle = resourceBundle;
        pattern = resourceBundle.getString(getKey());
        format = new MessageFormat(pattern, resourceBundle.getLocale());
    }

    /**
     * Use this to format a specific argument with a format other than the locale's format.
     * Wraps {@link MessageFormat#setFormatByArgumentIndex}.
     *
     * @param argumentIndex the argument index
     * @param format the new format
     * @return this
     */
    LocalizedStringImpl setFormatByArgumentIndex(int argumentIndex, Format format) {
        this.format.setFormatByArgumentIndex(argumentIndex, format);
        return this;
    }

    @Override
    public String get() {
        return pattern;
    }

    @Override
    public String get(Object... args) {
        return format.format(args);
    }

    @Override
    public void send(CommandSender recipient, Object... args) {
        recipient.sendMessage(get(args));
    }

    @Override
    public void sendInColor(ChatColor color, CommandSender recipient, Object... args) {
        recipient.sendMessage(color.toString() + get(args));
    }
}
