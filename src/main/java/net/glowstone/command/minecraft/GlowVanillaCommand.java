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
 * a {@link GlowPlayer}, the description, usage and permission-error messages are looked up in the
 * client's locale, overriding whatever has been or is subsequently set in
 * {@link #setDescription(String)}, {@link #setUsage(String)} or
 * {@link #setPermissionMessage(String)}. For non-player command senders, the server's locale is
 * still used, as are messages set with these setters.
 */
public abstract class GlowVanillaCommand extends VanillaCommand {
    private static final String BUNDLE_BASE_NAME = "commands";
    private static final ResourceBundle DEFAULT_RESOURCE_BUNDLE
            = ResourceBundle.getBundle(BUNDLE_BASE_NAME);
    private static final String DESCRIPTION_SUFFIX = ".description";
    private static final String USAGE_SUFFIX = ".usage";
    private static final String PERMISSION_SUFFIX = ".no-permission";
    private static final long CACHE_SIZE = 50;
    private static final LoadingCache<String, ResourceBundle> STRING_TO_BUNDLE_CACHE
        = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(CacheLoader.from(GlowVanillaCommand::bundleForMinecraftLocaleString));

    private final LoadingCache<ResourceBundle, LocalizedMessageTable> bundleToMessageCache;

    private static ResourceBundle bundleForMinecraftLocaleString(String localeStr) {
        if (localeStr == null) {
            return DEFAULT_RESOURCE_BUNDLE;
        }
        Locale locale;
        String[] pieces = localeStr.split("_");
        switch (pieces.length) {
            case 0:
                return DEFAULT_RESOURCE_BUNDLE;
            case 1:
                if (pieces[0].contains("-")) {
                    locale = Locale.forLanguageTag(localeStr);
                } else {
                    locale = new Locale(pieces[0]);
                }
                break;
            case 2:
                locale = new Locale(pieces[0], pieces[1].toUpperCase(Locale.ENGLISH));
                break;
            default:
                locale = new Locale(pieces[0], pieces[1].toUpperCase(Locale.ENGLISH), pieces[2]);
        }
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    private LocalizedMessageTable readResourceBundle(ResourceBundle bundle) {
        String name = getName();
        return new LocalizedMessageTable(
                bundle.getString(name + DESCRIPTION_SUFFIX),
                bundle.getString(name + USAGE_SUFFIX),
                bundle.getString(name + PERMISSION_SUFFIX));
    }

    /**
     * Creates an instance, using the command's name to look up the description etc.
     */
    public GlowVanillaCommand(@NonNls String name, @NonNls List<String> aliases) {
        super(name, "", "", aliases);
        bundleToMessageCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build(
                CacheLoader.from(this::readResourceBundle));
        LocalizedMessageTable defaultMessages = readResourceBundle(DEFAULT_RESOURCE_BUNDLE);
        super.setDescription(defaultMessages.getDescription());
        super.setUsage(defaultMessages.getUsageMessage());
        super.setPermissionMessage(defaultMessages.getPermissionMessage());
    }

    /**
     * {@inheritDoc}
     * <p>Delegates to {@link #execute(CommandSender, String, String[], LocalizedMessageTable)}. If
     * the command sender is a player, then the messages are localized for that player; otherwise,
     * the server locale is used.</p>
     */
    @Override
    public boolean execute(CommandSender sender, @NonNls String commandLabel, String[] args) {
        LocalizedMessageTable localizedMessages = null;
        if (sender instanceof GlowPlayer) {
            try {
                localizedMessages = bundleToMessageCache.get(
                        STRING_TO_BUNDLE_CACHE.get(((GlowPlayer) sender).getLocale()));
            } catch (ExecutionException e) {
                ConsoleMessages.Warn.Command.L10N_FAILED.log(e, getName(), sender);
            }
        }
        if (localizedMessages == null) {
            localizedMessages = new LocalizedMessageTable(
                    getDescription(), getUsage(), getPermissionMessage());
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
            LocalizedMessageTable localizedMessages);

    @Data
    protected static class LocalizedMessageTable {
        private final String description;
        private final String usageMessage;
        private final String permissionMessage;
    }
}
