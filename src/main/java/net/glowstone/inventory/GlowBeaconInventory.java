package net.glowstone.inventory;

import com.google.common.collect.Sets;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Beacon;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class GlowBeaconInventory extends GlowInventory implements BeaconInventory {

    private static final Set<Material> ALLOWED_MATERIALS = Sets.newHashSet(
            Material.EMERALD, Material.DIAMOND,
            Material.GOLD_INGOT, Material.IRON_INGOT
    );
    private static final int INPUT_SLOT = 0;

    public GlowBeaconInventory(Beacon owner) {
        super(owner, InventoryType.BEACON);

        getSlot(INPUT_SLOT).setType(InventoryType.SlotType.CRAFTING);
    }

    public void setActiveEffects(int primaryId, int secondaryId) {
        if (!ALLOWED_MATERIALS.contains(getItem().getType())) {
            return;
        }

        PotionEffectType primaryType = PotionEffectType.getById(primaryId);
        if (primaryType != null) {
            ((Beacon) getHolder()).setPrimaryEffect(primaryType);
        }
        PotionEffectType secondaryType = PotionEffectType.getById(secondaryId);
        if (secondaryType != null) {
            ((Beacon) getHolder()).setSecondaryEffect(secondaryType);
        }

        getItem().add(-1);
    }

    @Override
    public void setItem(ItemStack itemStack) {
        setItem(INPUT_SLOT, itemStack);
    }

    @Override
    public ItemStack getItem() {
        return getItem(INPUT_SLOT);
    }

    @Override
    public boolean itemPlaceAllowed(int slot, ItemStack stack) {
        if (slot == INPUT_SLOT) {
            return ALLOWED_MATERIALS.contains(stack.getType());
        }
        return super.itemPlaceAllowed(slot, stack);
    }
}
