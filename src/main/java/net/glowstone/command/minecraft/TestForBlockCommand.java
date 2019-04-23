package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowVanillaCommand;
import net.glowstone.constants.ItemIds;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

public class TestForBlockCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TestForBlockCommand() {
        super("testforblock");
        setPermission("minecraft.command.testforblock"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        if (args.length < 4) {
            sendUsageMessage(sender, messages);
            return false;
        }
        String itemName = CommandUtils.toNamespaced(args[3].toLowerCase());
        Material type = ItemIds.getBlock(itemName);
        ResourceBundle bundle = messages.getResourceBundle();
        if (type == null) {
            new LocalizedStringImpl("testforblock.invalid-block", bundle)
                    .sendInColor(ChatColor.RED, sender, itemName);
        }
        Location location = CommandUtils
                .getLocation(CommandUtils.getLocation(sender), args[0], args[1], args[2]);
        GlowBlock block = (GlowBlock) location.getBlock();
        if (block.getType() != type) {
            new LocalizedStringImpl("testforblock.wrong-block", bundle)
                    .sendInColor(ChatColor.RED, sender,
                            location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                            ItemIds.getName(block.getType()), ItemIds.getName(type));
            return false;
        }
        if (args.length > 4) {
            String state = args[4];
            BlockStateData data = CommandUtils.readState(sender, block.getType(), state);
            if (data == null) {
                return false;
            }
            if (data.isNumeric() && block.getData() != data.getNumericValue()) {
                new LocalizedStringImpl("testforblock.wrong-data", bundle)
                        .sendInColor(ChatColor.RED, sender,
                                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                                block.getData(), data);
                return false;
            } else if (!data.isNumeric()) {
                try {
                    boolean matches = StateSerialization
                            .matches(block.getType(), block.getState().getData(), data);
                    if (!matches) {
                        // TODO: Print the actual state of the block
                        new LocalizedStringImpl("testforblock.wrong-state",
                                bundle)
                                .sendInColor(ChatColor.RED, sender,
                                        location.getBlockX(), location.getBlockY(),
                                        location.getBlockZ(),
                                        state);
                        return false;
                    }
                } catch (InvalidBlockStateException e) {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    return false;
                }
            }
        }
        if (args.length > 5 && block.getBlockEntity() != null) {
            String dataTag = String
                    .join(" ", new ArrayList<>(Arrays.asList(args)).subList(5, args.length));
            try {
                CompoundTag tag = Mojangson.parseCompound(dataTag);
                CompoundTag blockTag = new CompoundTag();
                block.getBlockEntity().saveNbt(blockTag);
                if (!tag.matches(blockTag)) {
                    new LocalizedStringImpl("testforblock.wrong-data", bundle).sendInColor(
                            ChatColor.RED, sender,
                            location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                            blockTag, tag);
                    return false;
                }
            } catch (MojangsonParseException e) {
                messages.getGeneric(GenericMessage.INVALID_JSON)
                        .sendInColor(ChatColor.RED, sender, e.getMessage());
                return false;
            }
        }
        // All is well
        new LocalizedStringImpl("testforblock.done", bundle)
                .send(sender, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 4) {
            return ItemIds.getTabCompletion(args[3]);
        }
        return Collections.emptyList();
    }
}
