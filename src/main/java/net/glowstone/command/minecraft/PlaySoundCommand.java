package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.GlowSound;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.SoundUtil;
import net.glowstone.util.lang.I;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PlaySoundCommand extends VanillaCommand {

    private static final List<String> SOURCES = Arrays.stream(SoundCategory.values()).map(SoundCategory::name).map(String::toLowerCase).collect(Collectors.toList());

    private static final Set<String> SOUNDS = GlowSound.getSounds().keySet();

    public PlaySoundCommand() {
        super("playsound", I.tr("command.minecraft.playsound.description"), I.tr("command.minecraft.playsound.usage"), Collections.emptyList());
        setPermission("minecraft.command.playsound");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length < 3 || args.length == 4 || args.length == 5) {
            sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.playsound.usage")));
            return false;
        }

        final World world = CommandUtils.getWorld(sender);

        if (world == null) {
            return false;
        }

        String stringSound = args[0], stringCategory = args[1], playerPattern = args[2];
        final Sound sound = GlowSound.getVanillaSound(stringSound.startsWith("minecraft:") ? stringSound : "minecraft:" + stringSound);
        final SoundCategory soundCategory = SoundUtil.buildSoundCategory(stringCategory);
        List<GlowPlayer> targets;
        boolean relativeLocation = false;
        double volume = 1, minimumVolume = 0, pitch = 1;

        if (sound == null) {
            sender.sendMessage(I.tr(sender, "command.minecraft.playsound.invalid.sound", stringSound));
            return false;
        }

        if (soundCategory == null) {
            sender.sendMessage(I.tr(sender, "command.minecraft.playsound.invalid.category", stringCategory));
            return false;
        }

        // Manage player(s)
        if (playerPattern.startsWith("@") && playerPattern.length() > 1 && CommandUtils.isPhysical(sender)) { // Manage selectors
            final Location senderLocation = CommandUtils.getLocation(sender);
            final Entity[] entities = new CommandTarget(sender, args[0]).getMatched(senderLocation);
            targets = Arrays.stream(entities).filter(GlowPlayer.class::isInstance).map(GlowPlayer.class::cast).collect(Collectors.toList());
        } else {
            final GlowPlayer player = (GlowPlayer) Bukkit.getPlayerExact(playerPattern);

            if (player == null) {
                sender.sendMessage(I.tr(sender, "command.generic.player.missing", playerPattern));
                return false;
            } else {
                targets = Collections.singletonList(player);
            }
        }

        if (args.length >= 9) {
            try {
                minimumVolume = Double.valueOf(args[8]);

                if (minimumVolume < 0 || minimumVolume > 1) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.playsound.invalid.volume", args[8]));
                    return false;
                }
            } catch (final NumberFormatException n) {
                sender.sendMessage(I.tr(sender, "command.generic.nan", args[8]));
                return false;
            }
        }

        if (args.length >= 8) {
            try {
                pitch = Double.valueOf(args[7]);

                if (pitch < 0 || pitch > 2) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.playsound.invalid.pitch", args[7]));
                    return false;
                } else if (pitch < 0.5) {
                    pitch = 0.5;
                }

            } catch (final NumberFormatException n) {
                sender.sendMessage(I.tr(sender, "command.generic.nan", args[7]));
                return false;
            }
        }

        if (args.length >= 7) {
            try {
                volume = Double.valueOf(args[6]);
            } catch (final NumberFormatException n) {
                sender.sendMessage(I.tr(sender, "command.generic.nan", args[6]));
                return false;
            }
        }

        if (args.length >= 6) {
            relativeLocation = args[3].startsWith("~") || args[4].startsWith("~") || args[5].startsWith("~");
        }

        for (final GlowPlayer target : targets) {
            Location soundLocation, targetLocation = target.getLocation();
            double targetVolume = volume;

            try {
                if (relativeLocation) {
                    soundLocation = CommandUtils.getLocation(targetLocation, args[3], args[4], args[5]);
                } else if (args.length >= 6) {
                    soundLocation = CommandUtils.getLocation(new Location(world, 0, 0, 0), args[3], args[4], args[5]);
                } else {
                    soundLocation = targetLocation;
                }
            } catch (final NumberFormatException n) {
                sender.sendMessage(I.tr(sender, "command.minecraft.playsound.invalid.position", args[3], args[4], args[5]));
                return false;
            }

            // If the target is outside the normal audible sphere
            if (targetLocation.distanceSquared(soundLocation) > Math.pow(volume, 2)) {
                if (minimumVolume <= 0) {
                    sender.sendMessage(I.tr(sender, "command.minecraft.playsound.toofar", target.getName()));
                    return false;
                } else {
                    final double deltaX = soundLocation.getX() - targetLocation.getX(),
                            deltaY = soundLocation.getX() - targetLocation.getY(),
                            deltaZ = soundLocation.getX() - targetLocation.getZ();
                    final double delta = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2) + Math.pow(deltaZ, 2));

                    soundLocation = targetLocation;
                    soundLocation.add(deltaX / delta, deltaY / delta, deltaZ / delta);
                    targetVolume = minimumVolume;
                }
            }

            target.playSound(soundLocation, sound, soundCategory, (float) targetVolume, (float) pitch);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args == null) {
            return Collections.emptyList();
        } else if (args.length == 1) {
            String sound = args[0];

            if (!sound.startsWith("minecraft:")) {
                final int colonIndex = sound.indexOf(':');
                sound = "minecraft:" + sound.substring(colonIndex == -1 ? 0 : (colonIndex + 1));
            }

            return StringUtil.copyPartialMatches(sound, SOUNDS, new ArrayList(SOUNDS.size()));
        } else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], SOURCES, new ArrayList(SOURCES.size()));
        } else if (args.length == 3) {
            return super.tabComplete(sender, alias, args);
        } else {
            return Collections.emptyList();
        }
    }
}
