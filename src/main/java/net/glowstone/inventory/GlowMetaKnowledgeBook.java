package net.glowstone.inventory;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.KnowledgeBookMeta;

import java.util.List;

public class GlowMetaKnowledgeBook extends GlowMetaItem implements KnowledgeBookMeta {

    public GlowMetaKnowledgeBook(GlowMetaItem meta) {
        super(meta);

        if (!(meta instanceof GlowMetaKnowledgeBook)) return;

        GlowMetaKnowledgeBook book = (GlowMetaKnowledgeBook) meta;
        if (book.hasRecipes()) {
            // add recipes
        }
    }
    @Override
    public boolean hasRecipes() {
        return false;
    }

    @Override
    public List<NamespacedKey> getRecipes() {
        return null;
    }

    @Override
    public void setRecipes(List<NamespacedKey> recipes) {

    }

    @Override
    public void addRecipe(NamespacedKey... recipes) {

    }

    @Override
    public KnowledgeBookMeta clone() {
        return new GlowMetaKnowledgeBook(this);
    }
}
