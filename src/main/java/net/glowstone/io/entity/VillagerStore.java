package net.glowstone.io.entity;

import java.util.ArrayList;
import java.util.List;
import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

class VillagerStore extends AgeableStore<GlowVillager> {

    public VillagerStore() {
        super(GlowVillager.class, EntityType.VILLAGER, GlowVillager::new);
    }

    @Override
    public void load(GlowVillager entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isInt("Profession")) {
            int professionId = compound.getInt("Profession");
            if (GlowVillager.isValidProfession(professionId)) {
                entity.setProfession(GlowVillager.getProfessionById(professionId));
            }
        }
        if (compound.isInt("Career")) {
            int id = compound.getInt("Career");
            Villager.Career career = GlowVillager.getCareerById(id, entity.getProfession());
            if (career != null) {
                entity.setCareer(career);
            }
        }
        if (compound.isInt("Riches")) {
            entity.setRiches(compound.getInt("Riches"));
        }
        if (compound.isByte("Willing")) {
            entity.setWilling(compound.getBoolDefaultFalse("Willing"));
        }
        if (compound.isInt("CareerLevel")) {
            entity.setCareerLevel(compound.getInt("CareerLevel"));
        } else if (entity.getCareer() != null) {
            entity.setCareerLevel(1);
        }
        // Recipes
        compound.readCompound(offers -> {
            if (offers.isCompoundList("Recipes")) {
                entity.clearRecipes(); // clear defaults
                List<CompoundTag> recipesList = offers.getCompoundList("Recipes");
                List<MerchantRecipe> recipes = new ArrayList<>();
                for (CompoundTag recipeTag : recipesList) {
                    CompoundTag sellTag = recipeTag.getCompound("sell");
                    CompoundTag buy1tag = recipeTag.getCompound("buy");
                    CompoundTag buy2tag = recipeTag.tryGetCompound("buyB");
                    List<ItemStack> ingredients = new ArrayList<>();
                    ItemStack sell = NbtSerialization.readItem(sellTag);
                    ItemStack buy = NbtSerialization.readItem(buy1tag);
                    ingredients.add(buy);
                    if (buy2tag != null) {
                        ingredients.add(NbtSerialization.readItem(buy2tag));
                    }
                    boolean experienceReward = recipeTag.getBoolDefaultFalse("rewardExp");
                    int uses = recipeTag.getInt("uses");
                    int maxUses = recipeTag.getInt("maxUses");
                    MerchantRecipe recipe = new MerchantRecipe(sell, uses, maxUses,
                        experienceReward);
                    recipe.setIngredients(ingredients);
                    recipes.add(recipe);
                }
                entity.setRecipes(recipes);
            }
        }, "Offers");

        //TODO: remaining data
    }

    @Override
    public void save(GlowVillager entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity.getProfession() != null && entity.getProfession() != Villager.Profession.HUSK) {
            tag.putInt("Profession", entity.getProfession().ordinal());
        }
        if (entity.getCareer() != null) {
            tag.putInt("Career", GlowVillager.getCareerId(entity.getCareer()));
        }
        tag.putInt("Riches", entity.getRiches());
        tag.putBool("Willing", entity.isWilling());
        tag.putInt("CareerLevel", entity.getCareerLevel());
        // Recipes
        CompoundTag offers = new CompoundTag();
        List<CompoundTag> recipesList = new ArrayList<>();
        for (MerchantRecipe recipe : entity.getRecipes()) {
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.putBool("rewardExp", recipe.hasExperienceReward());
            recipeTag.putInt("uses", recipe.getUses());
            recipeTag.putInt("maxUses", recipe.getMaxUses());
            recipeTag.putCompound("sell", NbtSerialization.writeItem(recipe.getResult(), 0));
            recipeTag
                .putCompound("buy", NbtSerialization.writeItem(recipe.getIngredients().get(0), 0));
            if (recipe.getIngredients().size() > 1) {
                recipeTag.putCompound("buyB",
                    NbtSerialization.writeItem(recipe.getIngredients().get(1), 0));
            }
            recipesList.add(recipeTag);
        }
        offers.putCompoundList("Recipes", recipesList);
        tag.putCompound("Offers", offers);
        //TODO: remaining data
    }

}
