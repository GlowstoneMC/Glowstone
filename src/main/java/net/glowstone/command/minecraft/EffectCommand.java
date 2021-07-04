package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.GlowPotionEffect;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.TickUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EffectCommand extends GlowVanillaCommand {

    private static final List<String> VANILLA_IDS = GlowPotionEffect.getVanillaIds();

    /**
     * Creates the instance for this command.
     */
    public EffectCommand() {
        super("effect");
        setPermission("minecraft.command.effect"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length < 2) {
            sendUsageMessage(sender, commandMessages);
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
                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                players = Collections.singletonList(player);
            }
        }

        if (args[1].equals("clear")) { // NON-NLS
            for (GlowPlayer player : players) {
                for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                    player.removePotionEffect(potionEffect.getType());
                }
                new LocalizedStringImpl("effect.cleared", commandMessages.getResourceBundle())
                        .send(sender, player.getName());
            }
            return true;
        } else {
            PotionEffectType effectType = GlowPotionEffect.parsePotionEffectId(args[1]);
            if (effectType == null) {
                new LocalizedStringImpl("effect.unknown", commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender, args[1]);
                return false;
            }

            int duration = TickUtil.secondsToTicks(30);
            if (args.length >= 3 && args[2] != null) {
                try {
                    duration = TickUtil.secondsToTicks(Integer.parseInt(args[2]));
                } catch (NumberFormatException exc) {
                    commandMessages.getGeneric(GenericMessage.NAN)
                            .sendInColor(ChatColor.RED, sender, args[2]);
                    return false;
                }
            }

            int amplifier = 0;
            if (args.length >= 4 && args[3] != null) {
                try {
                    amplifier = Integer.parseInt(args[3]);
                } catch (NumberFormatException exc) {
                    commandMessages.getGeneric(GenericMessage.NAN)
                            .sendInColor(ChatColor.RED, sender, args[3]);
                    return false;
                }
            }

            boolean hideParticles = false;
            if (args.length >= 5 && args[4] != null) {
                hideParticles = Boolean.parseBoolean(args[4]);
            }
            LocalizedStringImpl doneMessage = new LocalizedStringImpl("effect.done",
                    commandMessages.getResourceBundle());
            for (GlowPlayer player : players) {
                player.addPotionEffect(
                    new PotionEffect(effectType, duration, amplifier, false, !hideParticles));
                doneMessage.send(sender, effectType.getName(),
                        effectType.getId(), amplifier + 1, player.getName(), duration / 20);
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
            String effectName = CommandUtils.toNamespaced(args[1]);
            return StringUtil
                .copyPartialMatches(effectName, VANILLA_IDS, new ArrayList<>(VANILLA_IDS.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
