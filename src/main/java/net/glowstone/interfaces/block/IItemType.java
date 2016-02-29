package net.glowstone.interfaces.block;

import org.bukkit.Material;

public interface IItemType {

    Material getMaterial();

    int getMaxStackSize();
}
