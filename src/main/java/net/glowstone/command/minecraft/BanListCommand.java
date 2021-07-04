package net.glowstone.command.minecraft;

import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NonNls;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Stream;

public class BanListCommand extends GlowVanillaCommand {

    @NonNls
    private static final List<String> BAN_TYPES = Arrays.asList("ips", "players");

    /**
     * Creates the instance for this command.
     */
    public BanListCommand() {
        super("banlist");
        setPermission("minecraft.command.ban.list"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        final ResourceBundle resourceBundle = messages.getResourceBundle();
        BanList.Type banType;

        if (args.length > 0) {
            Collator caseInsensitive = Collator.getInstance(messages.getLocale());
            caseInsensitive.setStrength(Collator.PRIMARY);
            if (caseInsensitive.compare(args[0], "ips") == 0) { // NON-NLS
                banType = BanList.Type.IP;
            } else if (caseInsensitive.compare(args[0], "players") == 0) { // NON-NLS
                banType = BanList.Type.NAME;
            } else {
                sendUsageMessage(sender, messages);
                return false;
            }
        } else {
            banType = BanList.Type.NAME;
        }

        final Set<BanEntry> banEntries = Bukkit.getBanList(banType).getBanEntries();

        if (banEntries.isEmpty()) {
            new LocalizedStringImpl("banlist.empty", resourceBundle).send(sender);
        } else {
            final Stream<String> targets = banEntries.stream().map(BanEntry::getTarget);
            new LocalizedStringImpl("banlist.non-empty", resourceBundle).send(sender,
                    banEntries.size());
            sender.sendMessage(messages.joinList(targets));
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil
                .copyPartialMatches(args[0], BAN_TYPES, new ArrayList<>(BAN_TYPES.size()));
        } else {
            return args.length == 0 ? super.tabComplete(sender, alias, args)
                : Collections.emptyList();
        }
    }
}
