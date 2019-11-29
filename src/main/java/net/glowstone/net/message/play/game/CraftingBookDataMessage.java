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
    private final boolean filterOpen;
    private final boolean smeltingBookOpen;
    private final boolean smeltingFilterOpen;

    /**
     * Creates a message about the given recipe.
     *
     * @param type     {@link #TYPE_DISPLAYED_RECIPE} or {@link #TYPE_STATUS}
     * @param recipeId the ID of a crafting recipe
     */
    public CraftingBookDataMessage(int type, int recipeId) {
        this.type = type;
        this.recipeId = recipeId;
        this.bookOpen = false;
        this.filterOpen = false;
        this.smeltingBookOpen = false;
        this.smeltingFilterOpen = false;
    }

    /**
     * Creates a message about the whole crafting book.
     *
     * @param type               {@link #TYPE_DISPLAYED_RECIPE} or {@link #TYPE_STATUS}
     * @param bookOpen           TODO: document this parameter
     * @param filterOpen         TODO: document this parameter
     * @param smeltingBookOpen   TODO: document this parameter
     * @param smeltingFilterOpen TODO: document this parameter
     */
    public CraftingBookDataMessage(int type, boolean bookOpen, boolean filterOpen,
                                   boolean smeltingBookOpen, boolean smeltingFilterOpen) {
        this.type = type;
        this.bookOpen = bookOpen;
        this.filterOpen = filterOpen;
        this.smeltingBookOpen = smeltingBookOpen;
        this.smeltingFilterOpen = smeltingFilterOpen;
        this.recipeId = -1;
    }
}
