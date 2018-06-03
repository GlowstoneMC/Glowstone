package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class DifficultyCommand extends VanillaCommand {

    private static final List<String> DIFFICULTIES = Arrays
        .asList("peaceful", "easy", "normal", "hard");

    /**
     * Creates the instance for this command.
     */
    public DifficultyCommand() {
        super("difficulty", "Sets the difficulty level.", "/difficulty <difficulty>",
            Collections.emptyList());
        setPermission("minecraft.command.difficulty");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        String difficultyId = args[0];
        Difficulty difficulty = null;
        switch (difficultyId.toLowerCase()) {
            case "peaceful":
            case "p":
            case "0":
                difficulty = Difficulty.PEACEFUL;
                break;
            case "easy":
            case "e":
            case "1":
                difficulty = Difficulty.EASY;
                break;
            case "normal":
            case "n":
            case "2":
                difficulty = Difficulty.NORMAL;
                break;
            case "hard":
            case "h":
            case "3":
                difficulty = Difficulty.HARD;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown difficulty: '" + difficultyId + "'");
                return false;
        }
        world.setDifficulty(difficulty);
        sender.sendMessage("Set difficulty of world '" + world.getName() + "' to " + DIFFICULTIES
            .get(difficulty.ordinal()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil
                .copyPartialMatches(args[0], DIFFICULTIES, new ArrayList<>(DIFFICULTIES.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
