package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.Data;

@Data
public final class RecipeBookStateMessage implements Message {

    private final RecipeBookType book;
    private final boolean bookOpen;
    private final boolean filterOpen;

    public enum RecipeBookType {
        CRAFTING,
        FURNACE,
        BLAST_FURNACE,
        SMOKER;

        public static RecipeBookType fromOrdinal(int ordinal) {
            switch (ordinal) {
                case 1:
                    return FURNACE;
                case 2:
                    return BLAST_FURNACE;
                case 3:
                    return SMOKER;
                case 0:
                default:
                    return CRAFTING;
            }
        }
    }
}
