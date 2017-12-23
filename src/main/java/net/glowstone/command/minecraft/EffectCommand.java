package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.GlowPotionEffect;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class EffectCommand extends VanillaCommand {

    private static final List<String> VANILLA_IDS = GlowPotionEffect.getVanillaIds();

    /**
     * Creates the instance for this command.
     */
    public EffectCommand() {
        super("effect",
            "Gives a player an effect",
            "/effect <player> clear "
                    + "OR /effect <player> <effect> [seconds] [amplifier] [hideParticles]",
            Collections.emptyList());
        setPermission("minecraft.command.effect");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage:" + usageMessage);
            return false;
        }

        String name = args[0];
        List<GlowPlayer> players;

        if (name.startsWith("@") && name.length() >= 2) {
            CommandTarget target = new CommandTarget(sender, name);
            players = Arrays.stream(target.getMatched(CommandUtils.getLocation(sender)))
                .filter(GlowPlayer.class::isInstance)
                .map(GlowPlayer.class::cast)
                .collect(Collectors.toList());
        } else {
            GlowPlayer player = (GlowPlayer) Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + " Player '" + name + "' cannot be found");
                return false;
            } else {
                players = Collections.singletonList(player);
            }
        }

        if (args[1].equals("clear")) {
            for (GlowPlayer player : players) {
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
                sender.sendMessage("Cleared potion effects from " + player.getName());
            }
            return true;
        } else {
            PotionEffectType effectType = GlowPotionEffect.parsePotionEffectId(args[1]);
            if (effectType == null) {
                sender.sendMessage(ChatColor.RED + "Potion effect " + args[1] + " is unknown");
                return false;
            }

            int duration = 30 * 20;
            if (args.length >= 3 && args[2] != null) {
                try {
                    duration = Integer.parseInt(args[2]) * 20;
                } catch (NumberFormatException exc) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a valid integer");
                    return false;
                }
            }

            int amplifier = 1;
            if (args.length >= 4 && args[3] != null) {
                try {
                    amplifier = Integer.parseInt(args[3]);
                } catch (NumberFormatException exc) {
                    sender.sendMessage(ChatColor.RED + args[3] + " is not a valid integer");
                    return false;
                }
            }

            boolean hideParticles = false;
            if (args.length >= 5 && args[4] != null) {
                hideParticles = Boolean.parseBoolean(args[4]);
            }

            for (GlowPlayer player : players) {
                player.addPotionEffect(
                    new PotionEffect(effectType, duration, amplifier, false, hideParticles));
                sender.sendMessage(
                    "Given " + effectType.getName() + " (ID " + effectType.getId() + ") * "
                        + amplifier + " to " + player.getName() + " for " + duration / 20
                        + " seconds");
            }
            return true;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args == null) {
            return Collections.emptyList();
        } else if (args.length == 2) {
            String effectName = args[1];

            if (!effectName.startsWith("minecraft:")) {
                final int colonIndex = effectName.indexOf(':');
                effectName =
                    "minecraft:" + effectName.substring(colonIndex == -1 ? 0 : (colonIndex + 1));
            }

            return StringUtil
                .copyPartialMatches(effectName, VANILLA_IDS, new ArrayList<>(VANILLA_IDS.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
