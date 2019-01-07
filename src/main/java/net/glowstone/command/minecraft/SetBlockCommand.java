package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class SetBlockCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SetBlockCommand() {
        super("setblock",
            "Changes a block to another block.",
            "/setblock <x> <y> <z> <block> [dataValue|state] [dataTag]",
            Collections.emptyList());
        setPermission("minecraft.command.setblock");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String itemName = CommandUtils.toNamespaced(args[3].toLowerCase());
        Material type = ItemIds.getBlock(itemName);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + itemName + " is not a valid block type");
            return false;
        }
        Location location = CommandUtils
            .getLocation(CommandUtils.getLocation(sender), args[0], args[1], args[2]);
        GlowBlock block = (GlowBlock) location.getBlock();
        byte dataValue = 0;
        if (args.length > 4) {
            String state = args[4];
            BlockStateData data = CommandUtils.readState(sender, type, state);
            if (data == null) {
                return false;
            }
            if (data.isNumeric()) {
                dataValue = data.getNumericValue();
            } else {
                try {
                    dataValue = StateSerialization.parseData(type, data).getData();
                } catch (InvalidBlockStateException e) {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    return false;
                }
            }
        }
        block.setType(type, dataValue, true);
        if (args.length > 5 && block.getBlockEntity() != null) {
            String dataTag = String
                .join(" ", new ArrayList<>(Arrays.asList(args)).subList(5, args.length));
            try {
                CompoundTag prev = new CompoundTag();
                block.getBlockEntity().saveNbt(prev);
                CompoundTag tag = Mojangson.parseCompound(dataTag);
                tag.mergeInto(prev, true);
                block.getBlockEntity().loadNbt(prev);
            } catch (MojangsonParseException e) {
                sender.sendMessage(ChatColor.RED + "Invalid Data Tag: " + e.getMessage());
                return false;
            }
        }
        sender.sendMessage("Block placed");
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
