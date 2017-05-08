package net.glowstone.block.itemtype;

import org.bukkit.Material;

public class ItemSkullItem extends ItemWearable {
     public ItemSkullItem() {
        super(0, ItemWearablePosition.HEAD);
        setPlaceAs(Material.SKULL);
    }
}
