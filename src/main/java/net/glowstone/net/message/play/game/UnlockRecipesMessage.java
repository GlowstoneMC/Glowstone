package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public final class UnlockRecipesMessage implements Message {

    public static final int ACTION_INIT = 0;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_REMOVE = 2;

    private final int action;
    private final boolean craftingBookOpen;
    private final boolean craftingBookFilter;
    private final boolean smeltingBookOpen;
    private final boolean smeltingBookFilter;
    private final boolean blastBookOpen;
    private final boolean blastBookFilter;
    private final boolean smokerBookOpen;
    private final boolean smokerBookFilter;
    private final int[] recipes;
    private final int[] allRecipes; // allRecipes is only set when action = ACTION_INIT (0)

    public UnlockRecipesMessage(int action,
                                boolean craftingBookOpen, boolean craftingBookFilter,
                                boolean smeltingBookOpen, boolean smeltingBookFilter,
                                boolean blastBookOpen, boolean blastBookFilter,
                                boolean smokerBookOpen, boolean smokerBookFilter,
                                int[] recipes) {
        this(action, craftingBookOpen, craftingBookFilter, smeltingBookOpen, smeltingBookFilter,
                blastBookOpen, blastBookFilter, smokerBookOpen, smokerBookFilter, recipes, null);
    }
}
