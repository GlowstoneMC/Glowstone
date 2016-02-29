package net.glowstone.interfaces.block;

import org.bukkit.Material;

public interface IBlockType {

    boolean canTickRandomly();

    Material getMaterial();
}
