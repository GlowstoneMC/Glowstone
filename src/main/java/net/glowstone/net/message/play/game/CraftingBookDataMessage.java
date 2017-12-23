package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class CraftingBookDataMessage implements Message {

    public static final int TYPE_DISPLAYED_RECIPE = 0;
    public static final int TYPE_STATUS = 1;

    private final int type;
    private final int recipeId;
    private final boolean bookOpen;
    private final boolean filter;

    /**
     * Creates a message about the given recipe.
     *
     * @param type {@link #TYPE_DISPLAYED_RECIPE} or {@link #TYPE_STATUS}
     * @param recipeId the ID of a crafting recipe
     */
    public CraftingBookDataMessage(int type, int recipeId) {
        this.type = type;
        this.recipeId = recipeId;
        this.bookOpen = false;
        this.filter = false;
    }

    /**
     * Creates a message about the whole crafting book.
     *
     * @param type {@link #TYPE_DISPLAYED_RECIPE} or {@link #TYPE_STATUS}
     * @param bookOpen TODO: document this parameter
     * @param filter TODO: document this parameter
     */
    public CraftingBookDataMessage(int type, boolean bookOpen, boolean filter) {
        this.type = type;
        this.bookOpen = bookOpen;
        this.filter = filter;
        this.recipeId = -1;
    }
}
