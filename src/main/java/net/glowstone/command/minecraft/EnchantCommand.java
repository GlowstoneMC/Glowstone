package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowVanillaCommand;
import net.glowstone.constants.GlowEnchantment;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NonNls;

public class EnchantCommand extends GlowVanillaCommand {

    @NonNls
    private static final String PREFIX = NamespacedKey.MINECRAFT + ':';
    private static List<String> VANILLA_IDS = GlowEnchantment.getVanillaIds();

    /**
     * Creates the instance for this command.
     */
    public EnchantCommand() {
        super("enchant");
        setPermission("minecraft.command.enchant"); // NON-NLS
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
                commandMessages.getGeneric(GenericMessage.NO_SUCH_PLAYER)
                        .sendInColor(ChatColor.RED, sender, name);
                return false;
            } else {
                players = Collections.singletonList(player).stream();
            }
        }

        Enchantment enchantment = GlowEnchantment.parseEnchantment(args[1]);
        if (enchantment == null) {
            new LocalizedStringImpl("enchant.unknown", commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, args[1]);
            return false;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException exc) {
            commandMessages.getGeneric(GenericMessage.NAN)
                    .sendInColor(ChatColor.RED, sender, args[2]);
            return false;
        }
        LocalizedStringImpl successMessage
                = new LocalizedStringImpl("enchant.done", commandMessages.getResourceBundle());
        players
            .filter(player -> player.getItemInHand() != null)
            .filter(player -> player.getItemInHand().getData().getItemType() != Material.AIR)
            .filter(player -> enchantment.canEnchantItem(player.getItemInHand()))
            .forEach(player -> {
                ItemStack itemInHand = player.getItemInHand();
                itemInHand.addUnsafeEnchantment(enchantment, level);
                player.setItemInHand(itemInHand);
                successMessage.send(sender);
            });
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args == null) {
            return Collections.emptyList();
        } else if (args.length == 2) {
            String effectName = CommandUtils.toNamespaced(args[1]);

            return StringUtil
                .copyPartialMatches(effectName, VANILLA_IDS, new ArrayList(VANILLA_IDS.size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
