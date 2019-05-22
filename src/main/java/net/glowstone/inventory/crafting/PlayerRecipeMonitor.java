package net.glowstone.inventory.crafting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.UnlockRecipesMessage;
import net.glowstone.util.nbt.CompoundTag;

@Data
public final class PlayerRecipeMonitor {

    private final GlowPlayer player;
    private final Set<String> recipes;
    private final Set<String> toBeDisplayed;
    private boolean bookOpen;
    private boolean filterCraftable;

    /**
     * Creates an instance associated with the given player and with an empty recipe book.
     *
     * @param player the player
     */
    public PlayerRecipeMonitor(GlowPlayer player) {
        this.player = player;
        this.bookOpen = false;
        this.filterCraftable = false;
        this.recipes = new HashSet<>();
        this.toBeDisplayed = new HashSet<>();
    }

    /**
     * Creates a message to send this recipe book's state to the client.
     *
     * @return an {@link UnlockRecipesMessage} containing this recipe book's state
     */
    public UnlockRecipesMessage createInitMessage() {
        int status = UnlockRecipesMessage.ACTION_INIT;
        int[] recipeIds = new int[0]; // todo: conversion to internal IDs
        int[] toBeDisplayedIds = new int[0]; // todo: conversion to internal IDs
        // TODO: Smelting flags
        return new UnlockRecipesMessage(status, bookOpen, filterCraftable, bookOpen, filterCraftable,
                toBeDisplayedIds, recipeIds);
    }

    /**
     * Restores state from an NBT tag.
     *
     * @param playerData an NBT tag containing a compound subtag named recipeBook
     */
    public void read(CompoundTag playerData) {
        playerData.tryGetCompound("recipeBook").ifPresent(recipeBook -> {
            recipeBook.readBoolean("isFilteringCraftable", this::setFilterCraftable);
            recipeBook.readBoolean("isGuiOpen", this::setBookOpen);
            recipeBook.readStringList("recipes", recipes::addAll);
            recipeBook.readStringList("toBeDisplayed", toBeDisplayed::addAll);
        });
    }

    /**
     * Populates a recipeBook compound subtag and adds it to the given tag.
     *
     * @param playerData a compound tag describing the player
     */
    public void write(CompoundTag playerData) {
        CompoundTag recipeBook = new CompoundTag();
        recipeBook.putBool("isFilteringCraftable", filterCraftable);
        recipeBook.putBool("isGuiOpen", bookOpen);
        recipeBook.putStringList("recipes", new ArrayList<>(recipes));
        recipeBook.putStringList("toBeDisplayed", new ArrayList<>(toBeDisplayed));
        playerData.putCompound("recipeBook", recipeBook);
    }
}
