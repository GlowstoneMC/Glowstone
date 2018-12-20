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
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.jetbrains.annotations.NonNls;

/**
 * A subclass of {@link VanillaCommand} with the additional feature that when the command sender is
 * a {@link GlowPlayer}, the description, usage and permission-error messages are looked up in the
 * client's locale, overriding whatever has been or is subsequently set in
 * {@link #setDescription(String)}, {@link #setUsage(String)} or
 * {@link #setPermissionMessage(String)}. For non-player command senders and players with unknown
 * locale ({@code {@link GlowPlayer#getLocale()} == null}), messages set with these setters will be
 * used, and the initial values are based on the server's locale.
 */
public abstract class GlowVanillaCommand extends VanillaCommand {
    private static final String BUNDLE_BASE_NAME = "commands";
    private static final ResourceBundle DEFAULT_RESOURCE_BUNDLE
            = ResourceBundle.getBundle(BUNDLE_BASE_NAME);
    private static final String DESCRIPTION_SUFFIX = ".description";
    private static final String USAGE_SUFFIX = ".usage";
    private static final String PERMISSION_SUFFIX = ".no-permission";
    private static final String DEFAULT_PERMISSION = "_generic.no-permission";
    private static final String USAGE_IS = "_generic.usage";

    private static final long CACHE_SIZE = 50;
    private static final LoadingCache<String, ResourceBundle> STRING_TO_BUNDLE_CACHE
        = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(CacheLoader.from(localeStr ->
                    ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.forLanguageTag(localeStr))));

    private final LoadingCache<ResourceBundle, CommandMessages> bundleToMessageCache;

    protected CommandMessages readResourceBundle(ResourceBundle bundle) {
        String name = getName();
        String permissionKey = name + PERMISSION_SUFFIX;
        return new CommandMessages(
                bundle.getString(name + DESCRIPTION_SUFFIX),
                bundle.getString(name + USAGE_SUFFIX),
                bundle.getString(
                        bundle.containsKey(permissionKey) ? permissionKey : DEFAULT_PERMISSION));
    }

    /**
     * Creates an instance, using the command's name to look up the description etc.
     */
    public GlowVanillaCommand(@NonNls String name, @NonNls List<String> aliases) {
        super(name, "", "", aliases);
        bundleToMessageCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build(
                CacheLoader.from(this::readResourceBundle));
        CommandMessages defaultMessages = readResourceBundle(DEFAULT_RESOURCE_BUNDLE);
        super.setDescription(defaultMessages.getDescription());
        super.setUsage(defaultMessages.getUsageMessage());
        super.setPermissionMessage(defaultMessages.getPermissionMessage());
    }

    /**
     * {@inheritDoc}
     * <p>This delegates to {@link #execute(CommandSender, String, String[], ResourceBundle,
     * CommandMessages)}. If the command sender is a player, then the description and usage message
     * are for that player's locale; otherwise, the server locale is used.</p>
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        CommandMessages localizedMessages = null;
        ResourceBundle bundle = null;
        if (sender instanceof GlowPlayer) {
            try {
                bundle = getBundle((GlowPlayer) sender);
                localizedMessages = bundleToMessageCache.get(bundle);
            } catch (ExecutionException e) {
                ConsoleMessages.Warn.Command.L10N_FAILED.log(e, getName(), sender);
            }
        }
        if (bundle == null) {
            bundle = DEFAULT_RESOURCE_BUNDLE;
        }
        if (localizedMessages == null) {
            localizedMessages
                    = new CommandMessages(getDescription(), getUsage(), getPermissionMessage());
        }
        return execute(sender, commandLabel, args, bundle, localizedMessages);
    }

    /**
     * Executes the command, returning its success.
     *
     * @param sender       Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args         All arguments passed to the command, split via ' '
     * @param resourceBundle The {@code commands.properties} resource bundle for the sender's locale
     * @param localizedMessages Object containing the title, description and permission message in
     *                     the sender's locale, or set with setters
     * @return true if the command was successful, otherwise false
     */
    protected abstract boolean execute(CommandSender sender, String commandLabel, String[] args,
            ResourceBundle resourceBundle, CommandMessages localizedMessages);

    protected static ResourceBundle getBundle(GlowPlayer sender)
            throws ExecutionException {
        return STRING_TO_BUNDLE_CACHE.get(sender.getLocale());
    }

    protected void sendUsageMessage(CommandSender sender, ResourceBundle resourceBundle) {
        new LocalizedStringImpl(USAGE_IS, resourceBundle)
                .sendInColor(sender, ChatColor.RED, usageMessage);
    }

    @Data
    protected static class CommandMessages {
        private final String description;
        private final String usageMessage;
        private final String permissionMessage;
    }
}
