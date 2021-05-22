package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

class VillagerStore extends AgeableStore<GlowVillager> {

    public VillagerStore() {
        super(GlowVillager.class, EntityType.VILLAGER, GlowVillager::new);
    }

    @Override
    public void load(GlowVillager entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.tryGetInt("Profession")
                .filter(GlowVillager::isValidProfession)
                .map(GlowVillager::getProfessionById)
                .ifPresent(entity::setProfession);
        compound.tryGetInt("Career")
                .map(GlowVillager::getProfessionById)
                .ifPresent(career -> {
                    entity.setProfession(career);
                    entity.setCareerLevel(compound.tryGetInt("CareerLevel").orElse(1));
                });
        compound.readInt("Riches", entity::setRiches);
        compound.readBoolean("Willing", entity::setWilling);
        // Recipes
        compound.readCompound("Offers", offers -> offers.readCompoundList("Recipes",
            recipesList -> {
                entity.clearRecipes(); // clear defaults
                List<MerchantRecipe> recipes = new ArrayList<>(recipesList.size());
                for (CompoundTag recipeTag : recipesList) {
                    recipeTag.readItem("sell", sell -> {
                        List<ItemStack> ingredients = new ArrayList<>(2);
                        recipeTag.readItem("buy", ingredients::add);
                        recipeTag.readItem("buyB", ingredients::add);
                        boolean experienceReward = recipeTag.getBoolean("rewardExp", false);
                        int uses = recipeTag.getInt("uses");
                        int maxUses = recipeTag.getInt("maxUses");
                        MerchantRecipe recipe = new MerchantRecipe(sell, uses, maxUses,
                                experienceReward);
                        recipe.setIngredients(ingredients);
                        recipes.add(recipe);
                    });
                }
                entity.setRecipes(recipes);
            }));

        //TODO: remaining data
    }

    @Override
    public void save(GlowVillager entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity.getProfession() != null) {
            tag.putInt("Profession", entity.getProfession().ordinal());
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
