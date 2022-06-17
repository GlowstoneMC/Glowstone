package net.glowstone.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.FurnaceEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GlowFurnace extends GlowContainer implements Furnace {

    @Getter
    @Setter
    private short burnTime;
    @Getter
    @Setter
    private short cookTime;

    /**
     * /**
     * Creates an instance for the given block.
     *
     * @param block the furnace block
     */
    public GlowFurnace(GlowBlock block) {
        super(block);
        burnTime = getBlockEntity().getBurnTime();
        cookTime = getBlockEntity().getCookTime();
    }

    /**
     * Creates an instance for the given block.
     *
     * @param block    the furnace block
     * @param burnTime the number of ticks before this furnace must consume more fuel, or 0 if not
     *                 burning
     * @param cookTime the number of ticks the current item has been cooking
     */
    public GlowFurnace(GlowBlock block, short burnTime, short cookTime) {
        super(block);
        this.burnTime = burnTime;
        this.cookTime = cookTime;
    }

    private FurnaceEntity getBlockEntity() {
        return (FurnaceEntity) getBlock().getBlockEntity();
    }

    // TODO: furnace cook API
    @Override
    public int getCookTimeTotal() {
        return 0;
    }

    @Override
    public void setCookTimeTotal(int cookTimeTotal) {

    }

    @Override
    public @NotNull Map<CookingRecipe<?>, Integer> getRecipesUsed() {
        return null;
    }

    @Override
    public double getCookSpeedMultiplier() {
        return 0;
    }

    @Override
    public void setCookSpeedMultiplier(double multiplier) {

    }

    @Override
    public int getRecipeUsedCount(@NotNull NamespacedKey furnaceRecipe) {
        return 0;
    }

    @Override
    public boolean hasRecipeUsedCount(@NotNull NamespacedKey furnaceRecipe) {
        return false;
    }

    @Override
    public void setRecipeUsedCount(@NotNull CookingRecipe<?> furnaceRecipe, int count) {

    }

    @Override
    public void setRecipesUsed(@NotNull Map<CookingRecipe<?>, Integer> recipesUsed) {

    }

    @Override
    public FurnaceInventory getInventory() {
        return (FurnaceInventory) getBlockEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            FurnaceEntity furnace = getBlockEntity();
            furnace.setBurnTime(burnTime);
            furnace.setCookTime(cookTime);
        }
        return result;
    }

    @Override
    public FurnaceInventory getSnapshotInventory() {
        throw new UnsupportedOperationException();
    }
}
