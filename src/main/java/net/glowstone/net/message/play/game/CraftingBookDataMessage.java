package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class CraftingBookDataMessage implements Message {

    public static final int TYPE_DISPLAYED_RECIPE = 0;
    public static final int TYPE_STATUS = 1;

    private final int type;
    // type: displayed recipe (0)
    private final int recipeId;
    // type: status (1)
    private final boolean bookOpen;
    private final boolean filter;

    public CraftingBookDataMessage(int type, int recipeId) {
        this.type = type;
        this.recipeId = recipeId;
        this.bookOpen = false;
        this.filter = false;
    }

    public CraftingBookDataMessage(int type, boolean bookOpen, boolean filter) {
        this.type = type;
        this.bookOpen = bookOpen;
        this.filter = filter;
        this.recipeId = -1;
    }
}
