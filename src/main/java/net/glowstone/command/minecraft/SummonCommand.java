package net.glowstone.command.minecraft;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.entity.CustomEntityDescriptor;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.Summonable;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NonNls;

public class SummonCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SummonCommand() {
        super("summon");
        setPermission("minecraft.command.summon"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL).send(sender);
            return true;
        }

        Location location = CommandUtils.getLocation(sender);

        if (args.length == 0) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        if (args.length >= 4) {
            location = CommandUtils.getLocation(location, args[1], args[2], args[3]);
        }
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        CompoundTag tag = null;
        if (args.length >= 5) {
            String data = String
                    .join(" ", new ArrayList<>(Arrays.asList(args)).subList(4, args.length));
            try {
                tag = Mojangson.parseCompound(data);
            } catch (MojangsonParseException e) {
                commandMessages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, e.getMessage());
            }
        }

        String entityName = args[0];
        if (!checkSummon(sender, entityName, commandMessages)) {
            return true;
        }
        GlowEntity entity;
        if (EntityType.fromName(entityName) != null) {
            entity = (GlowEntity) location.getWorld()
                    .spawnEntity(location, EntityType.fromName(entityName));
        } else {
            Class<? extends GlowEntity> clazz = EntityRegistry.getCustomEntityDescriptor(entityName)
                    .getEntityClass();
            entity = ((GlowWorld) location.getWorld())
                    .spawn(location, clazz, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
        if (tag != null) {
            EntityStorage.load(entity, tag);
        }
        new LocalizedStringImpl("summon.done", commandMessages.getResourceBundle())
                .send(sender);
        return true;
    }

    private boolean checkSummon(CommandSender sender, String type,
            CommandMessages messages) {
        if (type == null) {
            return false;
        }
        EntityType entityType = EntityType.fromName(type);
        if (entityType == null) {
            if (EntityRegistry.isCustomEntityRegistered(type)) {
                CustomEntityDescriptor descriptor = EntityRegistry.getCustomEntityDescriptor(type);
                if (!descriptor.isSummonable()) {
                    sendErrorIfSenderNonNull(sender, messages, type, "summon.error");
                    return false;
                }
                return true;
            } else {
                sendErrorIfSenderNonNull(sender, messages, type, "summon.not-found");
                return false;
            }
        } else {
            String canonicalName = entityType.name();
            if (EntityRegistry.getEntity(entityType) == null) {
                sendErrorIfSenderNonNull(sender, messages, canonicalName, "summon.not-impl");
                return false;
            } else if (!entityType.isSpawnable() && !Summonable.class
                    .isAssignableFrom(EntityRegistry.getEntity(entityType))) {
                sendErrorIfSenderNonNull(sender, messages, canonicalName, "summon.error");
                return false;
            } else {
                return true;
            }
        }
    }

    private void sendErrorIfSenderNonNull(@Nullable CommandSender sender,
            @Nullable CommandMessages messages, String entityType, @NonNls String key) {
        if (sender != null) {
            new LocalizedStringImpl(key, messages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, entityType);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null"); // NON-NLS
        Preconditions.checkNotNull(args, "Arguments cannot be null"); // NON-NLS
        Preconditions.checkNotNull(alias, "Alias cannot be null"); // NON-NLS
        if (args.length == 1) {
            String arg = args[0];
            ArrayList<String> completion = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (checkSummon(null, type.getName(), null) && type.getName().toLowerCase()
                        .startsWith(arg)) {
                    completion.add(type.getName());
                }
            }
            EntityRegistry.getRegisteredCustomEntities().forEach((d) -> {
                if (d.getId().toLowerCase().startsWith(arg)) {
                    completion.add(d.getId().toLowerCase());
                }
            });
            return completion;
        } else {
            return Collections.emptyList();
        }
    }
}
