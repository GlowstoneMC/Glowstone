package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.LocalizedEnumNames;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;

import java.util.List;

public class DifficultyCommand extends GlowVanillaCommand {

    private static final LocalizedEnumNames<Difficulty> DIFFICULTIES
        = new LocalizedEnumNames<>(Difficulty::getByValue, "glowstone.difficulty.unknown",
        "glowstone.difficulty.names", "maps/difficulty", false);

    /**
     * Creates the instance for this command.
     */
    public DifficultyCommand() {
        super("difficulty");
        setPermission("minecraft.command.difficulty"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        if (args.length != 1) {
            sendUsageMessage(sender, messages);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        String difficultyId = args[0];
        Difficulty difficulty = DIFFICULTIES.nameToValue(messages.getLocale(), difficultyId);
        if (difficulty == null) {
            new LocalizedStringImpl("difficulty.unknown", messages.getResourceBundle())
                .sendInColor(ChatColor.RED, sender, difficultyId);
            return false;
        }
        world.setDifficulty(difficulty);
        new LocalizedStringImpl("difficulty.done", messages.getResourceBundle())
            .send(sender, world, DIFFICULTIES.valueToName(messages.getLocale(), difficulty));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return DIFFICULTIES.getAutoCompleteSuggestions(getBundle(sender).getLocale(), args[0]);
        }
        return super.tabComplete(sender, alias, args);
    }
}
