package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowVanillaCommand;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class KillCommand extends GlowVanillaCommand {

    public KillCommand() {
        super("kill");
        setPermission("minecraft.command.kill"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Entity) {
                Entity entity = (Entity) sender;
                if (entity.isDead()) {
                    new LocalizedStringImpl("kill.self-dead", commandMessages.getResourceBundle())
                            .send(entity);
                } else if (entity instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) entity;
                    living.damage(Double.MAX_VALUE, EntityDamageEvent.DamageCause.SUICIDE);
                    new LocalizedStringImpl("kill.done", commandMessages.getResourceBundle())
                            .send(sender, CommandUtils.getName(entity));
                } else {
                    entity.remove();
                    new LocalizedStringImpl("kill.done", commandMessages.getResourceBundle())
                            .send(sender, CommandUtils.getName(entity));
                }
                return true;
            } else {
                new LocalizedStringImpl("kill.self-not-entity",
                        commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender);
                return false;
            }
        }
        if (args.length == 1) {
            String name = args[0];
            if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
                Location location = CommandUtils.getLocation(sender);
                CommandTarget target = new CommandTarget(sender, name);
                Entity[] matched = target.getMatched(location);
                if (matched.length == 0) {
                    commandMessages.getGeneric(GenericMessage.NO_MATCHES)
                            .sendInColor(ChatColor.RED, sender, name);
                    return false;
                }
                LocalizedStringImpl killDoneMessage = new LocalizedStringImpl("kill.done",
                        commandMessages.getResourceBundle());
                for (Entity entity : matched) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) entity;
                        living.damage(Double.MAX_VALUE, EntityDamageEvent.DamageCause.VOID);
                    } else {
                        entity.remove();
                    }
                    killDoneMessage.send(sender, CommandUtils.getName(entity));
                }
                return true;
            } else {
                Player player = Bukkit.getPlayerExact(name);
                if (player == null) {
                    commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                            .sendInColor(ChatColor.RED, sender, name);
                    return false;
                } else {
                    player.damage(Double.MAX_VALUE, EntityDamageEvent.DamageCause.VOID);
                    new LocalizedStringImpl("kill.done", commandMessages.getResourceBundle())
                            .send(sender, player.getName());
                    return true;
                }
            }
        }
        sendUsageMessage(sender, commandMessages);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
