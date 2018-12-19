package net.glowstone.command.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import lombok.Data;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.jetbrains.annotations.NonNls;

/**
 * A subclass of {@link VanillaCommand} with the additional feature that when the command sender is
 * a {@link GlowPlayer}, description, usage and permission-error messages are looked up in the
 * client's locale, temporarily overriding whatever has been or is subsequently set in
 * {@link #setDescription(String)}, {@link #setUsage(String)} or
 * {@link #setPermissionMessage(String)}.
 */
public abstract class GlowVanillaCommand extends VanillaCommand {

    private static final String BUNDLE_BASE_NAME = "commands";
    private static final String DESCRIPTION_SUFFIX = ".description";
    private static final String USAGE_SUFFIX = ".usage";
    private static final String PERMISSION_SUFFIX = ".no-permission";
    private static final LoadingCache<String, Locale>
            STRING_TO_LOCALE_CACHE = CacheBuilder.newBuilder().build(CacheLoader.from(
            Locale::forLanguageTag));

    /**
     * Use the server locale when talking to non-player command senders, since if anyone reads the
     * output it will be a server admin.
     */
    private final CommandMessages defaultMessages;
    private final LoadingCache<Locale, CommandMessages> localeToMessageCache;

    private CommandMessages readResourceBundle(ResourceBundle bundle) {
        return new CommandMessages(
                bundle.getString(getName() + DESCRIPTION_SUFFIX),
                bundle.getString(getName() + USAGE_SUFFIX),
                bundle.getString(getName() + PERMISSION_SUFFIX));
    }

    /**
     * Creates an instance, using the command's name to look up the description etc.
     */
    public GlowVanillaCommand(@NonNls String name, @NonNls List<String> aliases) {
        super(name, "", "", aliases);
        localeToMessageCache = CacheBuilder.newBuilder().build(CacheLoader.from(
            locale -> readResourceBundle(ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale))));
        defaultMessages = readResourceBundle(ResourceBundle.getBundle(BUNDLE_BASE_NAME));
        setDescription(defaultMessages.getDescription());
        setUsage(defaultMessages.getUsageMessage());
        setPermissionMessage(defaultMessages.getPermissionMessage());
    }

    /**
     * {@inheritDoc}
     * <p>This delegates to {@link #execute(CommandSender, String, String[], CommandMessages)}. If
     * the command sender is a player, then the description and usage message are for that player's
     * locale; otherwise, the server locale is used.</p>
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        CommandMessages localizedMessages = defaultMessages;
        if (sender instanceof GlowPlayer) {
            try {
                localizedMessages = localeToMessageCache.get(
                        STRING_TO_LOCALE_CACHE.get(((GlowPlayer) sender).getLocale()));
            } catch (ExecutionException e) {
                ConsoleMessages.Warn.Command.L10N_FAILED.log(e, getName(), sender);
            }
        }
        return execute(sender, commandLabel, args, localizedMessages);
    }

    /**
     * Executes the command, returning its success.
     *
     * @param sender       Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args         All arguments passed to the command, split via ' '
     * @param localizedMessages Object containing the title, description and permission message in
     *                     the sender's locale.
     * @return true if the command was successful, otherwise false
     */
    protected abstract boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages localizedMessages);

    @Data
    private static class CommandMessages {
        private final String description;
        private final String usageMessage;
        private final String permissionMessage;
    }
}
