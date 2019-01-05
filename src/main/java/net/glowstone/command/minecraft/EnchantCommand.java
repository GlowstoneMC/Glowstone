package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.GlowEnchantment;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

public class EnchantCommand extends GlowVanillaCommand {

    private static List<String> VANILLA_IDS = GlowEnchantment.getVanillaIds();

    /**
     * Creates the instance for this command.
     */
    public EnchantCommand() {
        super("enchant", Collections.emptyList());
        setPermission("minecraft.command.enchant");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length < 3) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        String name = args[0];
        Stream<GlowPlayer> players;
        if (name.startsWith("@")) {
            CommandTarget target = new CommandTarget(sender, name);
            players = Arrays.stream(target.getMatched(CommandUtils.getLocation(sender)))
                .filter(GlowPlayer.class::isInstance)
                .map(GlowPlayer.class::cast);
        } else {
            GlowPlayer player = (GlowPlayer) Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                commandMessages.getNoSuchPlayer().sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                players = Collections.singletonList(player).stream();
            }
        }

        Enchantment enchantment = GlowEnchantment.parseEnchantment(args[1]);
        if (enchantment == null) {
            sender.sendMessage(ChatColor.RED + "Enchantment " + args[1] + " is unknown");
            return false;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException exc) {
            sender.sendMessage(ChatColor.RED + args[2] + " is not a valid integer");
            return false;
        }

        players
            .filter(player -> player.getItemInHand() != null)
            .filter(player -> player.getItemInHand().getData().getItemType() != Material.AIR)
            .filter(player -> enchantment.canEnchantItem(player.getItemInHand()))
            .forEach(player -> {
                ItemStack itemInHand = player.getItemInHand();
                itemInHand.addUnsafeEnchantment(enchantment, level);
                player.setItemInHand(itemInHand);
                sender.sendMessage("Enchanting succeeded");
            });
        return true;
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
                .copyPartialMatches(effectName, VANILLA_IDS, new ArrayList(VANILLA_IDS.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
