package net.glowstone.command.minecraft;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.LocalizedString;
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
    /**
     * Keys for localizable messages shared by more than one command.
     */
    @RequiredArgsConstructor
    public enum GenericMessage {
        DEFAULT_PERMISSION("_generic.no-permission"),
        NO_SUCH_PLAYER("_generic.no-such-player"),
        NAN("_generic.nan"),
        OFFLINE("_generic.offline"),
        NO_MATCHES("_generic.no-matches"),
        USAGE_IS("_generic.usage"),
        NOT_PHYSICAL("_generic.not-physical"),
        NOT_PHYSICAL_COORDS("_generic.not-physical-coord"),
        TOO_HIGH("_generic.too-high"),
        TOO_LOW("_generic.too-low"),
        INVALID_JSON("_generic.invalid-json");
        @NonNls private final String key;
    }

    @NonNls
    private static final String BUNDLE_BASE_NAME = "commands";
    @NonNls
    private static final String DESCRIPTION_SUFFIX = ".description";
    @NonNls
    private static final String USAGE_SUFFIX = ".usage";
    @NonNls
    private static final String PERMISSION_SUFFIX = ".no-permission";
    @NonNls
    private static final String DEFAULT_PERMISSION = "_generic.no-permission";

    private static final ResourceBundle SERVER_LOCALE_BUNDLE
            = ResourceBundle.getBundle(BUNDLE_BASE_NAME);
    public static final long CACHE_SIZE = 50;
    private static final LoadingCache<String, ResourceBundle> STRING_TO_BUNDLE_CACHE
        = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(CacheLoader.from(localeStr ->
                    ResourceBundle.getBundle(BUNDLE_BASE_NAME, Locale.forLanguageTag(localeStr))));
    private static final LoadingCache<ResourceBundle, ImmutableMap<GenericMessage, LocalizedString>>
            COMMON_MESSAGES_CACHE = CacheBuilder.newBuilder()
            .maximumSize(CACHE_SIZE)
            .build(CacheLoader.from(resourceBundle -> {
                // ImmutableMap.Builder does not optimize for enums, but immutableEnumMap does
                EnumMap<GenericMessage, LocalizedString> genericMessages
                        = new EnumMap<GenericMessage, LocalizedString>(GenericMessage.class);
                for (GenericMessage message : GenericMessage.values()) {
                    genericMessages.put(message, new LocalizedStringImpl(message.key, resourceBundle));
                }
                return Maps.immutableEnumMap(genericMessages);
            }));
    public static final String JOINER = "_generic._joiner";

    private final LoadingCache<ResourceBundle, CommandMessages> bundleToMessageCache;

    protected CommandMessages readResourceBundle(ResourceBundle bundle) {
        String name = getName();
        String permissionKey = name + PERMISSION_SUFFIX;
        return new CommandMessages(
                bundle,
                bundle.getString(name + DESCRIPTION_SUFFIX),
                bundle.getString(name + USAGE_SUFFIX),
                bundle.getString(
                        bundle.containsKey(permissionKey) ? permissionKey : DEFAULT_PERMISSION));
    }

    /**
     * Creates an instance with no aliases (i.e. only callable by one name), using the name to look
     * up the localized description etc.
     *
     * @param name the command name
     */
    public GlowVanillaCommand(@NonNls String name) {
        this(name, Collections.emptyList());
    }

    /**
     * Creates an instance, using the command's name to look up the localized description etc.
     *
     * @param name the command name
     * @param aliases synonyms to accept for the command
     */
    public GlowVanillaCommand(@NonNls String name, @NonNls List<String> aliases) {
        super(name, "", "", aliases);
        bundleToMessageCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).build(
                CacheLoader.from(this::readResourceBundle));
        CommandMessages defaultMessages = readResourceBundle(SERVER_LOCALE_BUNDLE);
        super.setDescription(defaultMessages.getDescription());
        super.setUsage(defaultMessages.getUsageMessage());
        super.setPermissionMessage(defaultMessages.getPermissionMessage());
    }

    /**
     * {@inheritDoc}
     * <p>This delegates to {@link #execute(CommandSender, String, String[], CommandMessages)}. If
     * the command sender is a player, then the description and usage message
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
            bundle = SERVER_LOCALE_BUNDLE;
        }
        if (localizedMessages == null) {
            localizedMessages = new CommandMessages(SERVER_LOCALE_BUNDLE,
                    getDescription(),
                    getUsage(),
                    getPermissionMessage());
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
     *                     the sender's locale, or set with setters
     * @return true if the command was successful, otherwise false
     */
    protected abstract boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages localizedMessages);

    protected static ResourceBundle getBundle(GlowPlayer sender) {
        String locale = sender.getLocale();
        if (locale == null) {
            return SERVER_LOCALE_BUNDLE;
        }
        try {
            return STRING_TO_BUNDLE_CACHE.get(locale);
        } catch (ExecutionException e) {
            ConsoleMessages.Error.I18n.COMMAND.log(e, locale);
            return SERVER_LOCALE_BUNDLE;
        }
    }

    protected static ResourceBundle getBundle(CommandSender sender) {
        return sender instanceof GlowPlayer ? getBundle((GlowPlayer) sender) : SERVER_LOCALE_BUNDLE;
    }

    /**
     * Works like {@link #testPermission(CommandSender)} but uses the specified error message.
     * @param target User to test
     * @param permissionMessage Error message if user lacks permission
     * @return true if they can use it, otherwise false
     */
    public boolean testPermission(CommandSender target, String permissionMessage) {
        if (testPermissionSilent(target)) {
            return true;
        }
        target.sendMessage(ChatColor.RED + permissionMessage);
        return false;
    }

    protected void sendUsageMessage(CommandSender sender,
            CommandMessages commandMessages) {
        commandMessages.getGeneric(GenericMessage.USAGE_IS)
                .sendInColor(ChatColor.RED, sender, commandMessages.getUsageMessage());
    }

    protected static class CommandMessages {
        // Only LocalizedString messages that apply to multiple commands should be in this class.
        // All others are instantiated on demand.
        private final ImmutableMap<GenericMessage, LocalizedString> genericMessages;
        @Getter
        private final Locale locale;
        @Getter
        private final ResourceBundle resourceBundle;
        @Getter
        private final String description;
        @Getter
        private final String usageMessage;
        @Getter
        private final String permissionMessage;
        @Getter
        private final String joiner;

        public LocalizedString getGeneric(GenericMessage which) {
            return genericMessages.get(which);
        }

        public CommandMessages(ResourceBundle bundle, String description,
                String usageMessage, String permissionMessage) {
            locale = bundle.getLocale();
            resourceBundle = bundle;
            this.description = description;
            this.usageMessage = usageMessage;
            this.permissionMessage = permissionMessage;
            try {
                this.genericMessages = COMMON_MESSAGES_CACHE.get(bundle);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            joiner = new LocalizedStringImpl(JOINER, bundle).get();
        }

        /**
         * Returns the given items as a comma-separated list. The comma character and surrounding
         * spacing are a localized string.
         *
         * @param objects the items to format as a list
         * @return a comma-separated list
         */
        public String joinList(Iterable<? extends CharSequence> objects) {
            return String.join(joiner, objects);
        }

        /**
         * Returns the given items as a comma-separated list. The comma character and surrounding
         * spacing are a localized string.
         *
         * @param objects the items to format as a list
         * @return a comma-separated list
         */
        public String joinList(Stream<? extends CharSequence> objects) {
            return objects.collect(Collectors.joining(joiner));
        }

        /**
         * Returns the given items as a comma-separated list. The comma character and surrounding
         * spacing are a localized string.
         *
         * @param objects the items to format as a list
         * @return a comma-separated list
         */
        public String joinList(CharSequence... objects) {
            return String.join(joiner, objects);
        }
    }
}
