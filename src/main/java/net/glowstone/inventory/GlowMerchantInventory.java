package net.glowstone.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import lombok.Getter;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;

public class GlowMerchantInventory extends GlowInventory implements MerchantInventory {

    private static final int SLOT_INPUT_1 = 0;
    private static final int SLOT_INPUT_2 = 1;
    private static final int SLOT_RESULT = 2;

    @Getter
    private Merchant merchant;
    @Getter
    private int selectedRecipeIndex;

    /**
     * Creates the inventory for a merchant.
     *
     * @param owner the CUSTOMER as an {@link InventoryHolder}
     * @param merchant the merchant as a {@link Merchant}
     */
    public GlowMerchantInventory(InventoryHolder owner, Merchant merchant) {
        super(owner, InventoryType.MERCHANT);
        checkNotNull(merchant);

        this.merchant = merchant;
        this.selectedRecipeIndex = 0;

        getSlot(SLOT_INPUT_1).setType(InventoryType.SlotType.CONTAINER);
        getSlot(SLOT_INPUT_2).setType(InventoryType.SlotType.CONTAINER);
        getSlot(SLOT_RESULT).setType(InventoryType.SlotType.RESULT);
    }

    @Override
    public MerchantRecipe getSelectedRecipe() {
        return merchant.getRecipe(selectedRecipeIndex);
    }
}
