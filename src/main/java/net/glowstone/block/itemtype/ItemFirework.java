package net.glowstone.block.itemtype;

import java.util.UUID;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.passive.GlowFirework;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

public class ItemFirework extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
                                ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        spawnFirework(player, holding, target.getLocation().add(clickedLoc), player.getUniqueId(),
            null);
    }

    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        if (InventoryUtil.isEmpty(player.getEquipment().getChestplate())
            || player.getEquipment().getChestplate().getType() != Material.ELYTRA || !player
            .isGliding()) {
            return;
        }

        spawnFirework(player, holding, player.getLocation(), player.getUniqueId(), player);
    }

    private void spawnFirework(GlowPlayer player, ItemStack item, Location location, UUID spawner,
                               LivingEntity boostedEntity) {
        if (item.getType() != Material.FIREWORK_STAR ||
            !(item.getItemMeta() instanceof FireworkMeta)) {
            return;
        }
        new GlowFirework(location, spawner, boostedEntity, item);

        if (player.getGameMode() != GameMode.CREATIVE) {
            item.setAmount(item.getAmount() - 1);
        }
    }

    @Override
    public Context getContext() {
        return Context.ANY;
    }
}
