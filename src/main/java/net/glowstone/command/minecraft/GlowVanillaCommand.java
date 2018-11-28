package net.glowstone.command.minecraft;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.jetbrains.annotations.NonNls;

/**
 * A subclass of {@link VanillaCommand} with the additional feature that when
 * the command sender is
 * a {@link GlowPlayer}, description, usage and permission-error messages are
 * looked up in the
 * client's locale, temporarily overriding whatever has been or is subsequently
 * set in
 * {@link #setDescription(String)}, {@link #setUsage(String)} or
 * {@link #setPermissionMessage(String)}.
 */
public abstract class GlowVanillaCommand extends VanillaCommand {

    private static final String BUNDLE_BASE_NAME = "commands";
    private static final String DESCRIPTION_SUFFIX = ".description";
    private static final String USAGE_SUFFIX = ".usage";
    private static final String PERMISSION_SUFFIX = ".no-permission";
    private static final ResourceBundle SERVER_LOCALE = ResourceBundle.getBundle(BUNDLE_BASE_NAME);

    private final Lock localeChangeLock = new ReentrantLock();
    private final String resourceKey;
    private volatile Locale lastLoadedLocale;

    public GlowVanillaCommand(@NonNls String name, @NonNls String resourceKey,
            @NonNls List<String> aliases) {
        super(name,
                SERVER_LOCALE.getString(resourceKey + DESCRIPTION_SUFFIX),
                SERVER_LOCALE.getString(resourceKey + USAGE_SUFFIX),
                aliases);
        setPermissionMessage(SERVER_LOCALE.getString(resourceKey + PERMISSION_SUFFIX));
        this.resourceKey = resourceKey;
        lastLoadedLocale = SERVER_LOCALE.getLocale();
    }

    @Override
    public Command setDescription(String description) {
        localeChangeLock.lock();
        try {
            lastLoadedLocale = null;
            return super.setDescription(description);
        } finally {
            localeChangeLock.unlock();
        }
    }

    @Override
    public Command setUsage(String usage) {
        localeChangeLock.lock();
        try {
            lastLoadedLocale = null;
            return super.setUsage(usage);
        } finally {
            localeChangeLock.unlock();
        }
    }

    @Override
    public Command setPermissionMessage(String permissionMessage) {
        localeChangeLock.lock();
        try {
            lastLoadedLocale = null;
            return super.setPermissionMessage(permissionMessage);
        } finally {
            localeChangeLock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This delegates to {@link #innerExecute(CommandSender, String, String[])},
     * but first ensures
     * that if the command sender is a player, the description and usage message
     * are for that player's
     * locale.</p>
     */
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof GlowPlayer)) {
            return innerExecute(sender, commandLabel, args);
        }
        localeChangeLock.lock();
        try {
            Locale locale = Locale.forLanguageTag(((GlowPlayer) sender).getLocale());
            if (locale.equals(lastLoadedLocale)) {
                return innerExecute(sender, commandLabel, args);
            }
            lastLoadedLocale = locale;
            String oldDescription = getDescription();
            String oldUsage = getUsage();
            String oldPermissionMessage = getPermissionMessage();
            ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
            description = bundle.getString(resourceKey + DESCRIPTION_SUFFIX);
            usageMessage = bundle.getString(resourceKey + USAGE_SUFFIX);
            super.setPermissionMessage(bundle.getString(resourceKey + PERMISSION_SUFFIX));
            try {
                return innerExecute(sender, commandLabel, args);
            } finally {
                description = oldDescription;
                usageMessage = oldUsage;
                super.setPermissionMessage(oldPermissionMessage);
            }
        } finally {
            localeChangeLock.unlock();
        }
    }

    /**
     * Executes the command, returning its success.
     *
     * @param sender Source object which is executing this command
     * @param commandLabel The alias of the command used
     * @param args All arguments passed to the command, split via ' '
     * @return true if the command was successful, otherwise false
     */
    protected abstract boolean innerExecute(CommandSender sender, String commandLabel, String[] args);
}
