package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.inventory.ItemStack;

public class ItemGoldenApple extends ItemFood {

    public ItemGoldenApple() {
        super(4, 9.6f);
    }

    @Override
    public boolean eat(GlowPlayer player, ItemStack item) {
        if (!super.eat(player, item)) return false;

        //TODO potions

        return true;
    }
}
