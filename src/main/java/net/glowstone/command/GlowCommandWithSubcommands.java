package net.glowstone.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.Getter;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NonNls;

public class GlowCommandWithSubcommands extends GlowVanillaCommand {

    protected static final Collator ENGLISH_CASE_INSENSITIVE = Collator.getInstance(Locale.ENGLISH);

    static {
        ENGLISH_CASE_INSENSITIVE.setStrength(Collator.PRIMARY);
    }

    protected final ImmutableSortedMap<String, Subcommand> subcommandMap;
    protected final ImmutableList<Subcommand> subcommands;
    protected final ImmutableList<String> subcommandNames;

    public GlowCommandWithSubcommands(String rootCommandName, List<Subcommand> subcommands) {
        this(rootCommandName, Collections.emptyList(), subcommands);
    }

    /**
     * Creates an instance.
     * @param rootCommandName the name of the root command
     * @param aliases aliases of the root command
     * @param subcommands subcommands, excluding the "help" subcommand which is auto-generated
     */
    public GlowCommandWithSubcommands(String rootCommandName, List<String> aliases,
            List<Subcommand> subcommands) {
        super(rootCommandName, aliases);
        Subcommand helpCommand = new Subcommand(rootCommandName, "help") {
            @Override
            protected boolean execute(CommandSender sender, String label, String[] args,
                    CommandMessages commandMessages) {
                for (Subcommand subcommand : subcommands) {
                    subcommand.sendHelp(sender, label, commandMessages.getResourceBundle());
                }
                return false;
            }
        };
        this.subcommands = new ImmutableList.Builder().addAll(subcommands).add(helpCommand).build();
        this.subcommandNames = this.subcommands.stream().map(Subcommand::getMainName)
                .collect(ImmutableList.toImmutableList());
        ImmutableSortedMap.Builder subcommandMapBuilder
                = new ImmutableSortedMap.Builder(ENGLISH_CASE_INSENSITIVE);
        for (Subcommand subcommand : this.subcommands) {
            subcommandMapBuilder.put(subcommand.getMainName(), subcommand);
            for (String name : subcommand.otherNames) {
                subcommandMapBuilder.put(name, subcommand);
            }
        }
        subcommandMap = subcommandMapBuilder.build();
    }

    @Override
    protected boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        Subcommand subcommand = null;
        if (args.length >= 1) {
            subcommand = subcommandMap.get(args[0]);
        }
        if (subcommand == null) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        return subcommand.execute(sender, commandLabel, args, commandMessages);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null"); // NON-NLS
        Preconditions.checkNotNull(args, "Arguments cannot be null"); // NON-NLS
        Preconditions.checkNotNull(alias, "Alias cannot be null"); // NON-NLS
        switch (args.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return StringUtil.copyPartialMatches(args[0], subcommandNames,
                        new ArrayList<>(subcommandNames.size()));
            default:
                Subcommand subcommand = subcommandMap.get(args[0]);
                return subcommand == null ? Collections.emptyList()
                        : subcommand.tabComplete(sender, alias, args);
        }
    }

    /**
     * A subcommand.
     */
    protected abstract static class Subcommand {

        final String[] otherNames;

        @Getter
        private final @NonNls
        String mainName;
        private final @NonNls String keyPrefix;
        private final @NonNls String usageKey;
        private final @NonNls String descriptionKey;

        protected Subcommand(String commandName, String mainName, String... otherNames) {
            this.otherNames = otherNames;
            this.mainName = mainName;
            keyPrefix = commandName + ".subcommand." + mainName;
            usageKey = keyPrefix + ".usage";
            descriptionKey = keyPrefix + ".description";
        }

        /**
         * Executed on tab completion for this subcommand, returning a list of
         * options the player can tab through.
         *
         * @param sender Source object which is executing this command
         * @param alias the alias being used
         * @param args All arguments passed to the command, split via ' ', including the subcommand
         *     itself
         * @return a list of tab-completions for the specified arguments. This
         *     will never be null. List may be immutable.
         * @throws IllegalArgumentException if sender, alias, or args is null
         */
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
                throws IllegalArgumentException {
            return Collections.emptyList();
        }

        public void sendHelp(CommandSender sender, String label, ResourceBundle resourceBundle) {
            sender.sendMessage("- " + ChatColor.GOLD + "/" + label + " "
                    + ChatColor.AQUA + new LocalizedStringImpl(usageKey, resourceBundle).get()
                    + ChatColor.GRAY + ": "
                    + new LocalizedStringImpl(descriptionKey, resourceBundle).get());
        }

        protected abstract boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages);
    }
}
