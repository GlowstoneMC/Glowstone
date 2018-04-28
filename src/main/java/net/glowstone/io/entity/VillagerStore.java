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
        compound.readInt("Profession", professionId -> {
            if (GlowVillager.isValidProfession(professionId)) {
                entity.setProfession(GlowVillager.getProfessionById(professionId));
            }
        });
        compound.readInt("Career", id -> {
            Villager.Career career = GlowVillager.getCareerById(id, entity.getProfession());
            if (career != null) {
                entity.setCareer(career);
            }
        });
        compound.readInt("Riches", entity::setRiches);
        compound.readBoolean("Willing", entity::setWilling);
        if (!compound.readInt("CareerLevel", entity::setCareerLevel)
                && entity.getCareer() != null) {
            entity.setCareerLevel(1);
        }
        // Recipes
        compound.readCompound("Offers", offers -> offers.readCompoundList("Recipes", recipesList -> {
            entity.clearRecipes(); // clear defaults
            List<MerchantRecipe> recipes = new ArrayList<>(recipesList.size());
            for (CompoundTag recipeTag : recipesList) {
                List<ItemStack> ingredients = new ArrayList<>(2);
                final ItemStack[] sell = {null};
                recipeTag.readItem("sell", item -> sell[0] = item);
                recipeTag.readItem("buy", ingredients::add);
                recipeTag.readItem("buyB", ingredients::add);
                boolean experienceReward = recipeTag.getBoolDefaultFalse("rewardExp");
                int uses = recipeTag.getInt("uses");
                int maxUses = recipeTag.getInt("maxUses");
                MerchantRecipe recipe = new MerchantRecipe(sell[0], uses, maxUses,
                    experienceReward);
                recipe.setIngredients(ingredients);
                recipes.add(recipe);
            }
            entity.setRecipes(recipes);
        }));

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
