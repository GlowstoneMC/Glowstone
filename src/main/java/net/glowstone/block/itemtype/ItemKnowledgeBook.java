package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemKnowledgeBook extends ItemType {
    @Override
    public void rightClickAir(GlowPlayer player, ItemStack holding) {
        ItemMeta recipes = holding.getItemMeta();
        //player.learnRecipes(recipes.getRecipes());
    }
}
