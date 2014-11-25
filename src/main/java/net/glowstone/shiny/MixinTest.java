package net.glowstone.shiny;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.translation.Translations;

/**
 * Todo: Javadoc for TestMain.
 */
public class MixinTest {

    public static void main(String[] args) {
        System.out.println("In main()");
        System.out.println("> Sponge Constants");
        System.out.println("Shovel: " + ItemTypes.IRON_SHOVEL);
        System.out.println("Pickaxe: " + ItemTypes.IRON_PICKAXE);
        System.out.println("> Sponge Factory");
        System.out.println("Translate: " + Translations.of("test"));
    }

}
