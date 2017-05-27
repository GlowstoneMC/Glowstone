package net.glowstone.block.itemtype;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

public class ItemKnowledgeBook extends ItemType {
    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        if (holding.getItemMeta() instanceof KnowledgeBookMeta) {
            KnowledgeBookMeta recipes = (KnowledgeBookMeta) holding.getItemMeta();
            if (recipes.hasRecipes()) {
                for (NamespacedKey recipe : recipes.getRecipes()) {
                    player.learnRecipe(((GlowServer) Bukkit.getServer()).getCraftingManager().getRecipeByKey(recipe), true);
                }
            }
        }
    }
}
