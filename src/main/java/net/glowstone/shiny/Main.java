package net.glowstone.shiny;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Texts;

/**
 * Todo: Javadoc for TestMain.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Mixin Test ===");
        System.out.println("Sponge Constants");
        System.out.println("  Shovel: " + ItemTypes.IRON_SHOVEL);
        System.out.println("  Pickaxe: " + ItemTypes.IRON_PICKAXE);
        System.out.println("  Stone: " + ItemTypes.STONE);
        System.out.println("Sponge Factory");
        System.out.println("  Translate: " + Texts.of("test"));

        // ---
        System.out.println("=== Plugin Load ===");
        new ShinyGame();
    }

}
