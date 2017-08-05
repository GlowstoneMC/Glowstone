package net.glowstone.inventory.crafting;

import lombok.Data;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.UnlockRecipesMessage;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public final class PlayerRecipeMonitor {

    private final GlowPlayer player;
    private boolean bookOpen;
    private boolean filterCraftable;
    private final Set<String> recipes, toBeDisplayed;

    public PlayerRecipeMonitor(GlowPlayer player) {
        this.player = player;
        this.bookOpen = false;
        this.filterCraftable = false;
        this.recipes = new HashSet<>();
        this.toBeDisplayed = new HashSet<>();
    }

    public UnlockRecipesMessage createInitMessage() {
        int status = UnlockRecipesMessage.ACTION_INIT;
        int[] recipeIds = new int[0]; // todo: conversion to internal IDs
        int[] toBeDisplayedIds = new int[0]; // todo: conversion to internal IDs
        return new UnlockRecipesMessage(status, bookOpen, filterCraftable, toBeDisplayedIds, recipeIds);
    }

    public void read(CompoundTag playerData) {
        if (!playerData.isCompound("recipeBook")) {
            return;
        }
        CompoundTag recipeBook = playerData.getCompound("recipeBook");
        if (recipeBook.isByte("isFilteringCraftable")) {
            setFilterCraftable(recipeBook.getBool("isFilteringCraftable"));
        }
        if (recipeBook.isByte("isGuiOpen")) {
            setBookOpen(recipeBook.getBool("isGuiOpen"));
        }
        if (recipeBook.isList("recipes", TagType.STRING)) {
            recipes.addAll(recipeBook.getList("recipes", TagType.STRING));
        }
        if (recipeBook.isList("toBeDisplayed", TagType.STRING)) {
            toBeDisplayed.addAll(recipeBook.getList("toBeDisplayed", TagType.STRING));
        }
    }

    public void write(CompoundTag playerData) {
        CompoundTag recipeBook = new CompoundTag();
        recipeBook.putBool("isFilteringCraftable", filterCraftable);
        recipeBook.putBool("isGuiOpen", bookOpen);
        recipeBook.putList("recipes", TagType.STRING, new ArrayList<>(recipes));
        recipeBook.putList("toBeDisplayed", TagType.STRING, new ArrayList<>(toBeDisplayed));
        playerData.putCompound("recipeBook", recipeBook);
    }
}
